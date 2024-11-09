package com.homestay.service;

import com.homestay.constants.BookingStatus;
import com.homestay.constants.DiscountType;
import com.homestay.constants.PaymentStatus;
import com.homestay.dto.response.BookingResponse;
import com.homestay.exception.BusinessException;
import com.homestay.exception.ErrorCode;
import com.homestay.mapper.*;
import com.homestay.model.*;
import com.homestay.repository.BookingRepository;
import com.homestay.repository.HomestayRepository;
import com.homestay.repository.RoomRepository;
import com.homestay.repository.UserRepository;
import com.homestay.service.external.EmailService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingService {
    EmailService emailService;
    BookingRepository bookingRepository;
    RoomRepository roomRepository;
    UserRepository userRepository;
    HomestayRepository homestayRepository;
    BookingMapper bookingMapper;
    DiscountMapper discountMapper;
    RoomMapper roomMapper;
    HomestayMapper homestayMapper;
    UserMapper userMapper;

    @Scheduled(fixedRate = 1000 * 60 * 60 * 12)
    public void remindBooking() {
        List<Booking> bookings = bookingRepository.findByStatus(BookingStatus.PENDING.name());
        for (Booking booking : bookings) {
            if (LocalDate.now().isEqual(booking.getCheckIn().minusDays(1))) {
                if (Objects.equals(booking.getStatus(), BookingStatus.PENDING.name())) {
                    emailService.sendEmail(booking.getUser().getEmail(), booking.getUser().getFullName(),
                            "http://localhost:3000/pay?id=" + booking.getId(),
                            "Ngày mai là ngày check-in của bạn, vui lòng thanh toán để hoàn tất quá trình đặt phòng.",
                            "Nhắc hẹn thanh toán", "Thanh toán ngay");
                } else if (Objects.equals(booking.getStatus(), BookingStatus.PAID.name())) {
                    emailService.sendEmail(booking.getUser().getEmail(), booking.getUser().getFullName(),
                            "http://localhost:3000/bookingdetail?id=" + booking.getId(),
                            "Ngày mai là ngày check-in của bạn, vui lòng chuẩn bị.",
                            "Nhắc hẹn check-in", "Chuẩn bị ngay");
                }
            }
        }
    }

    @Transactional
    public BookingResponse booking(String homestayId, String checkIn, String checkOut, int guests, String status, String roomId) {
        Homestay homestay = homestayRepository.findById(homestayId)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));
        if (LocalDate.parse(checkIn).isAfter(LocalDate.parse(checkOut))) {
            throw new BusinessException(ErrorCode.CHECKIN_AFTER_CHECKOUT);
        }
        if (LocalDate.parse(checkIn).isBefore(LocalDate.now()) || LocalDate.parse(checkOut).isBefore(LocalDate.now())) {
            throw new BusinessException(ErrorCode.CHECKIN_CHECKOUT_IN_PAST);
        }

        List<Room> selectedRooms = new ArrayList<>();
        int remainingGuests = guests;

        // Get available rooms
        List<Room> availableRooms = roomRepository.findAvailableRoomsByHomestayId(homestayId, LocalDate.parse(checkIn), LocalDate.parse(checkOut));
        if (availableRooms.isEmpty() || remainingGuests > availableRooms.stream().mapToInt(Room::getSize).sum()) {
            throw new BusinessException(ErrorCode.NO_AVAILABLE_ROOMS);
        }

        // If roomId is provided, add the room to the selected rooms list
        if (!Objects.equals(roomId, "")) {
            if (availableRooms.stream().anyMatch(room -> room.getId().equals(roomId))) {
                Room requireRoom = availableRooms.stream()
                        .filter(room -> room.getId().equals(roomId))
                        .findFirst()
                        .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));
                selectedRooms.add(requireRoom);
                availableRooms.remove(requireRoom);
                remainingGuests -= requireRoom.getSize();
            } else {
                throw new BusinessException(ErrorCode.ROOM_NOT_AVAILABLE);
            }
        }

        // Sort rooms based on the number of guests
        if (remainingGuests <= availableRooms.stream().mapToInt(Room::getSize).max().orElse(0) && remainingGuests > 0) {
            availableRooms.sort(Comparator.comparingInt(Room::getSize));
            int finalRemainingGuests = remainingGuests;
            Room suitableRoom = availableRooms.stream()
                    .filter(room -> room.getSize() >= finalRemainingGuests)
                    .min(Comparator.comparingInt(Room::getSize))
                    .orElseThrow(() -> new BusinessException(ErrorCode.NO_AVAILABLE_ROOMS));
            selectedRooms.add(suitableRoom);
        } else {
            while (remainingGuests > 0) {
                availableRooms.sort(Comparator.comparingInt(Room::getSize));
                Room largestRoom = availableRooms.getLast();
                selectedRooms.add(largestRoom);
                remainingGuests -= largestRoom.getSize();
                availableRooms.remove(largestRoom);
                if (remainingGuests <= availableRooms.stream().mapToInt(Room::getSize).max().orElse(0)) {
                    int finalRemainingGuests1 = remainingGuests;
                    Room suitableRoom = availableRooms.stream()
                            .filter(room -> room.getSize() >= finalRemainingGuests1)
                            .min(Comparator.comparingInt(Room::getSize))
                            .orElseThrow(() -> new BusinessException(ErrorCode.NO_AVAILABLE_ROOMS));
                    selectedRooms.add(suitableRoom);
                    remainingGuests -= suitableRoom.getSize();
                }
            }
        }

        // Calculate costs and discounts
        double originalCost = 0;
        double totalCost = 0;
        double totalDiscount = 0;
        int totalNights = (int) ChronoUnit.DAYS.between(LocalDate.parse(checkIn), LocalDate.parse(checkOut));
        for (LocalDate date = LocalDate.parse(checkIn); !date.isEqual(LocalDate.parse(checkOut)); date = date.plusDays(1)) {
            LocalDate finalDate = date;
            for (Room room : selectedRooms) {
                double dailyRate = room.getPrice();
                if (finalDate.getDayOfWeek() == DayOfWeek.SATURDAY || finalDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    dailyRate = room.getWeekendPrice();
                }
                Optional<PriceCalendar> calendarPrice = room.getPriceCalendars().stream()
                        .filter(pc -> pc.getDate().equals(finalDate.toString()))
                        .findFirst();
                if (calendarPrice.isPresent()) {
                    dailyRate = calendarPrice.get().getPrice();
                }

                originalCost += dailyRate;

                double discountValue = 0;
                for (Discount discount : room.getDiscounts()) {
                    if (discount.getStartDate() != null && discount.getEndDate() != null
                            && !finalDate.isBefore(discount.getStartDate().toLocalDate())
                            && !finalDate.isAfter(discount.getEndDate().toLocalDate())) {
                        discountValue += discount.getValue();
                    } else if ((Objects.equals(discount.getType(), DiscountType.WEEKLY.toString()) && totalNights >= 7) ||
                            (Objects.equals(discount.getType(), DiscountType.MONTHLY.toString()) && totalNights >= 28)) {
                        discountValue += discount.getValue();
                    }
                }
                if (discountValue == 0) {
                    for (Discount discount : homestay.getDiscounts()) {
                        if (discount.getStartDate() != null && discount.getEndDate() != null
                                && !finalDate.isBefore(discount.getStartDate().toLocalDate())
                                && !finalDate.isAfter(discount.getEndDate().toLocalDate())) {
                            discountValue += discount.getValue();
                        } else if ((Objects.equals(discount.getType(), DiscountType.WEEKLY.toString()) && totalNights >= 7) ||
                                (Objects.equals(discount.getType(), DiscountType.MONTHLY.toString()) && totalNights >= 28)) {
                            discountValue += discount.getValue();
                        }
                    }
                }
                totalDiscount += discountValue;
                dailyRate -= (dailyRate * discountValue) / 100;
                totalCost += dailyRate;
            }
        }

        Booking booking = Booking.builder()
                .checkIn(LocalDate.parse(checkIn))
                .checkOut(LocalDate.parse(checkOut))
                .status(BookingStatus.REVIEW.name())
                .totalCost((int) totalCost)
                .originalTotal((int) originalCost)
                .totalDiscount((int) totalDiscount)
                .guests(guests)
                .note("Xem xét đặt phòng")
                .rooms(selectedRooms)
                .build();
        if (Objects.equals(status, BookingStatus.PENDING.name())) {
            User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            booking.setUser(user);
            booking.setStatus(BookingStatus.PENDING.name());
            booking.setNote("Chưa thanh toán");
            booking.setId(bookingRepository.save(booking).getId());
        }

        return getBookingResponse(booking);
    }

    public BookingResponse getBooking(String id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKING_NOT_FOUND));
        return getBookingResponse(booking);
    }

    public void createPayment(String orderInfo, LocalDateTime paymentTime, int paymentStatus, String totalPrice, String transactionId, String vnpBankCode, String vnpBankTranNo, String vnpCardType, String vnpTxnRef, String vnpSecureHash) {
        Booking booking = bookingRepository.findById(orderInfo)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKING_NOT_FOUND));

        Payment payment = Payment.builder()
                .amount(Integer.parseInt(totalPrice))
                .transactionId(transactionId)
                .date(paymentTime.toLocalDate())
                .status(paymentStatus == 1 ? PaymentStatus.SUCCESS.name() : PaymentStatus.FAILED.name())
                .note("Thanh toán qua VNPAY")
                .paymentMethod(vnpCardType)
                .booking(booking)
                .build();

        booking.setPayment(payment);
        booking.setNote("Đã thanh toán");
        booking.setStatus(BookingStatus.PAID.name());
        bookingRepository.save(booking);
    }

    public List<BookingResponse> getMyBooking() {
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        List<Booking> bookings = bookingRepository.findByUserId(user.getId());
        return bookings.stream()
                .map(this::getBookingResponse)
                .sorted(Comparator.comparing(BookingResponse::getCreatedAt).reversed())
                .toList();
    }

    public List<BookingResponse> getAllBookings() {
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        List<Booking> bookings = bookingRepository.findByHomestayIds(user.getHomestays().stream()
                .map(Homestay::getId)
                .collect(Collectors.toSet()));
        return bookings.stream()
                .map(this::getBookingResponse)
                .sorted(Comparator.comparing(BookingResponse::getCreatedAt).reversed())
                .toList();
    }

    private BookingResponse getBookingResponse(Booking booking) {
        BookingResponse response = bookingMapper.toBookingResponse(booking);
        response.setUserInformation(userMapper.toUserResponse(booking.getUser()));
        response.setRooms(booking.getRooms().stream()
                .map(roomMapper::toRoomResponse)
                .collect(toList()));
        response.setHomestayId(booking.getRooms().getFirst().getHomestay().getId());
        response.setHomestay(homestayRepository.findById(booking.getRooms().getFirst().getHomestay().getId())
                .map(homestayMapper::toHomestayResponse)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND)));
        response.setDiscounts(booking.getRooms().stream()
                .flatMap(room -> room.getDiscounts().stream())
                .map(discountMapper::toDiscountResponse)
                .collect(Collectors.toSet()));
        return response;
    }

    public BookingResponse updateBookingStatus(String id, String status, String reason) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKING_NOT_FOUND));
        booking.setStatus(status);
        booking.setNote(reason != null ? reason : "");
        bookingRepository.save(booking);
        return getBookingResponse(booking);
    }

    @Transactional
    public BookingResponse updateBooking(String id, String checkIn, String checkOut, int guests) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKING_NOT_FOUND));

        if (LocalDate.parse(checkIn).isAfter(LocalDate.parse(checkOut))) {
            throw new BusinessException(ErrorCode.CHECKIN_AFTER_CHECKOUT);
        }
        if (LocalDate.parse(checkIn).isBefore(LocalDate.now()) || LocalDate.parse(checkOut).isBefore(LocalDate.now())) {
            throw new BusinessException(ErrorCode.CHECKIN_CHECKOUT_IN_PAST);
        }

        List<Room> selectedRooms = booking.getRooms();
        int remainingGuests = guests;

        // Get available rooms
        List<Room> availableRooms = roomRepository.findAvailableRoomsByHomestayId(booking.getRooms().getFirst().getHomestay().getId(), LocalDate.parse(checkIn), LocalDate.parse(checkOut));
        selectedRooms.stream().map(Room::getId).forEach(roomId -> availableRooms.removeIf(room -> room.getId().equals(roomId)));
        availableRooms.addAll(selectedRooms);
        selectedRooms.clear();
        if (availableRooms.isEmpty() || remainingGuests > availableRooms.stream().mapToInt(Room::getSize).sum()) {
            throw new BusinessException(ErrorCode.NO_AVAILABLE_ROOMS);
        }

        // Sort rooms based on the number of guests
        if (remainingGuests <= availableRooms.stream().mapToInt(Room::getSize).max().orElse(0) && remainingGuests > 0) {
            availableRooms.sort(Comparator.comparingInt(Room::getSize));
            int finalRemainingGuests = remainingGuests;
            Room suitableRoom = availableRooms.stream()
                    .filter(room -> room.getSize() >= finalRemainingGuests)
                    .min(Comparator.comparingInt(Room::getSize))
                    .orElseThrow(() -> new BusinessException(ErrorCode.NO_AVAILABLE_ROOMS));
            selectedRooms.add(suitableRoom);
        } else {
            while (remainingGuests > 0) {
                availableRooms.sort(Comparator.comparingInt(Room::getSize));
                Room largestRoom = availableRooms.getLast();
                selectedRooms.add(largestRoom);
                remainingGuests -= largestRoom.getSize();
                availableRooms.remove(largestRoom);
                if (remainingGuests <= availableRooms.stream().mapToInt(Room::getSize).max().orElse(0)) {
                    int finalRemainingGuests1 = remainingGuests;
                    Room suitableRoom = availableRooms.stream()
                            .filter(room -> room.getSize() >= finalRemainingGuests1)
                            .min(Comparator.comparingInt(Room::getSize))
                            .orElseThrow(() -> new BusinessException(ErrorCode.NO_AVAILABLE_ROOMS));
                    selectedRooms.add(suitableRoom);
                    remainingGuests -= suitableRoom.getSize();
                }
            }
        }

        // Calculate costs and discounts
        double originalCost = 0;
        double totalCost = 0;
        double totalDiscount = 0;
        int totalNights = (int) ChronoUnit.DAYS.between(LocalDate.parse(checkIn), LocalDate.parse(checkOut));
        for (LocalDate date = LocalDate.parse(checkIn); !date.isEqual(LocalDate.parse(checkOut)); date = date.plusDays(1)) {
            LocalDate finalDate = date;
            for (Room room : selectedRooms) {
                double dailyRate = room.getPrice();
                if (finalDate.getDayOfWeek() == DayOfWeek.SATURDAY || finalDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    dailyRate = room.getWeekendPrice();
                }
                Optional<PriceCalendar> calendarPrice = room.getPriceCalendars().stream()
                        .filter(pc -> pc.getDate().equals(finalDate.toString()))
                        .findFirst();
                if (calendarPrice.isPresent()) {
                    dailyRate = calendarPrice.get().getPrice();
                }

                originalCost += dailyRate;

                double discountValue = 0;
                for (Discount discount : room.getDiscounts()) {
                    if (discount.getStartDate() != null && discount.getEndDate() != null
                            && !finalDate.isBefore(discount.getStartDate().toLocalDate())
                            && !finalDate.isAfter(discount.getEndDate().toLocalDate())) {
                        discountValue += discount.getValue();
                    } else if ((Objects.equals(discount.getType(), DiscountType.WEEKLY.toString()) && totalNights >= 7) ||
                            (Objects.equals(discount.getType(), DiscountType.MONTHLY.toString()) && totalNights >= 28)) {
                        discountValue += discount.getValue();
                    }
                }
                if (discountValue == 0) {
                    for (Discount discount : booking.getRooms().get(0).getHomestay().getDiscounts()) {
                        if (discount.getStartDate() != null && discount.getEndDate() != null
                                && !finalDate.isBefore(discount.getStartDate().toLocalDate())
                                && !finalDate.isAfter(discount.getEndDate().toLocalDate())) {
                            discountValue += discount.getValue();
                        } else if ((Objects.equals(discount.getType(), DiscountType.WEEKLY.toString()) && totalNights >= 7) ||
                                (Objects.equals(discount.getType(), DiscountType.MONTHLY.toString()) && totalNights >= 28)) {
                            discountValue += discount.getValue();
                        }
                    }
                }
                totalDiscount += discountValue;
                dailyRate -= (dailyRate * discountValue) / 100;
                totalCost += dailyRate;
            }
        }

        booking.setCheckIn(LocalDate.parse(checkIn));
        booking.setCheckOut(LocalDate.parse(checkOut));
        booking.setGuests(guests);
        booking.setTotalCost((int) totalCost);
        booking.setOriginalTotal((int) originalCost);
        booking.setTotalDiscount((int) totalDiscount);
        booking.setRooms(selectedRooms);

        bookingRepository.save(booking);

        return getBookingResponse(booking);
    }
}
