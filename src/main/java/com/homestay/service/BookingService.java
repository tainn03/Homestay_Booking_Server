package com.homestay.service;

import com.homestay.constants.BookingStatus;
import com.homestay.constants.DiscountType;
import com.homestay.constants.PaymentStatus;
import com.homestay.dto.response.BookingResponse;
import com.homestay.dto.response.RoomResponse;
import com.homestay.exception.BusinessException;
import com.homestay.exception.ErrorCode;
import com.homestay.mapper.BookingMapper;
import com.homestay.mapper.DiscountMapper;
import com.homestay.model.*;
import com.homestay.repository.BookingRepository;
import com.homestay.repository.RoomRepository;
import com.homestay.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingService {
    BookingRepository bookingRepository;
    RoomRepository roomRepository;
    UserRepository userRepository;
    BookingMapper bookingMapper;
    DiscountMapper discountMapper;

    public BookingResponse booking(String homestayId, String checkIn, String checkOut, int guests, String status) {
        if (LocalDate.parse(checkIn).isAfter(LocalDate.parse(checkOut))) {
            throw new BusinessException(ErrorCode.CHECKIN_AFTER_CHECKOUT);
        }
        if (LocalDate.parse(checkIn).isBefore(LocalDate.now()) || LocalDate.parse(checkOut).isBefore(LocalDate.now())) {
            throw new BusinessException(ErrorCode.CHECKIN_CHECKOUT_IN_PAST);
        }

        List<Room> availableRooms = roomRepository.findAvailableRoomsByHomestayId(homestayId, LocalDate.parse(checkIn), LocalDate.parse(checkOut));
        if (availableRooms.isEmpty()) {
            throw new BusinessException(ErrorCode.NO_AVAILABLE_ROOMS);
        }

        List<Room> selectedRooms = new ArrayList<>();
        int remainingGuests = guests;

        // Sắp xếp phòng theo sức chứa tăng dần để tối ưu hóa việc chọn phòng nhỏ nhất
        availableRooms.sort(Comparator.comparingInt(Room::getSize));
        int finalRemainingGuests = remainingGuests;
        Room suitableRoom = availableRooms.stream()
                .filter(room -> room.getSize() >= finalRemainingGuests)
                .min(Comparator.comparingInt(Room::getSize))
                .orElse(null);
        if (suitableRoom != null) {
            selectedRooms.add(suitableRoom);
            remainingGuests = 0;
        } else {
            for (Room room : availableRooms) {
                if (room.getSize() >= remainingGuests) {
                    selectedRooms.add(room);
                    remainingGuests -= room.getSize();
                    break;
                } else {
                    selectedRooms.add(room);
                    remainingGuests -= room.getSize();
                }

                if (remainingGuests <= 0) break;
            }
        }
        if (remainingGuests > 0) {
            throw new BusinessException(ErrorCode.NO_AVAILABLE_ROOMS);
        }

        // Lọc ra các khuyến mãi áp dụng
        Set<Discount> applicableDiscounts = new HashSet<>();
        selectedRooms.stream()
                .flatMap(room -> room.getDiscounts().stream())
                .filter(discount -> (discount.getStartDate() == null || !discount.getStartDate().isAfter(LocalDate.parse(checkOut).atStartOfDay())) &&
                        (discount.getEndDate() == null || !discount.getEndDate().isBefore(LocalDate.parse(checkIn).atStartOfDay())))
                .sorted(Comparator.comparing(discount -> {
                    if (Objects.equals(discount.getType(), DiscountType.WEEKLY.toString())) return 1;
                    if (Objects.equals(discount.getType(), DiscountType.MONTHLY.toString())) return 2;
                    return 3;
                }))
                .forEach(applicableDiscounts::add);

        // Tính toán chi phí và giảm giá
        int numOfWeekend = 0;
        int numOfWeekday = 0;
        int originalCost = 0;
        int totalCost = 0;
        int totalDiscount = 0;

        for (LocalDate date = LocalDate.parse(checkIn); !date.isEqual(LocalDate.parse(checkOut)); date = date.plusDays(1)) {
            LocalDate finalDate = date;
            double dailyRate = selectedRooms.stream()
                    .mapToDouble(room -> {
                        Optional<PriceCalendar> calendarPrice = room.getPriceCalendars().stream()
                                .filter(pc -> pc.getDate().equals(finalDate.toString()))
                                .findFirst();
                        return calendarPrice.map(PriceCalendar::getPrice)
                                .orElse(finalDate.getDayOfWeek() == DayOfWeek.SATURDAY || finalDate.getDayOfWeek() == DayOfWeek.SUNDAY
                                        ? room.getWeekendPrice()
                                        : room.getPrice());
                    })
                    .sum();
            originalCost += (int) dailyRate;

            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                numOfWeekend++;
            } else {
                numOfWeekday++;
            }

            int valueOfDiscountValueInDay = 0;
            for (Discount discount : applicableDiscounts) {
                if (discount.getStartDate() != null && discount.getEndDate() != null
                        && !date.isBefore(discount.getStartDate().toLocalDate())
                        && !date.isAfter(discount.getEndDate().toLocalDate())) {
                    valueOfDiscountValueInDay += (int) discount.getValue();
                }
            }
            totalDiscount += valueOfDiscountValueInDay;
            dailyRate -= (dailyRate * valueOfDiscountValueInDay) / 100;
            totalCost += (int) dailyRate;
        }

        // Check for weekly (7 nights) or monthly (28 nights) discounts and update the discount
        int totalNights = (int) ChronoUnit.DAYS.between(LocalDate.parse(checkIn), LocalDate.parse(checkOut));
        for (Discount discount : applicableDiscounts) {
            if ((Objects.equals(discount.getType(), DiscountType.WEEKLY.toString()) && totalNights >= 7) ||
                    (Objects.equals(discount.getType(), DiscountType.MONTHLY.toString()) && totalNights >= 28)) {
                totalDiscount += (int) discount.getValue();
                totalCost -= (int) ((totalCost * discount.getValue()) / 100);
            }
        }

        Booking booking = Booking.builder()
                .checkIn(LocalDate.parse(checkIn))
                .checkOut(LocalDate.parse(checkOut))
                .status(BookingStatus.REVIEW.name())
                .totalCost(totalCost)
                .originalTotal(originalCost)
                .totalDiscount(totalDiscount)
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
            bookingRepository.save(booking);
        }

        BookingResponse bookingResponse = bookingMapper.toBookingResponse(booking);
        bookingResponse.setRooms(selectedRooms.stream()
                .map(room -> RoomResponse.builder()
                        .id(room.getId())
                        .name(room.getName())
                        .size(room.getSize())
                        .build())
                .toList());
//        bookingResponse.setNights(totalNights);
//        bookingResponse.setTotalDiscount(totalDiscount);
//        bookingResponse.setOriginalTotal(originalCost);
//        bookingResponse.setNumOfWeekend(numOfWeekend);
//        bookingResponse.setNumOfWeekday(numOfWeekday);
        bookingResponse.setDiscounts(applicableDiscounts.stream()
                .map(discountMapper::toDiscountResponse)
                .toList());

        return bookingResponse;
    }

    public BookingResponse getBooking(String id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKING_NOT_FOUND));
        BookingResponse response = bookingMapper.toBookingResponse(booking);
        response.setRooms(booking.getRooms().stream()
                .map(room -> RoomResponse.builder()
                        .id(room.getId())
                        .name(room.getName())
                        .size(room.getSize())
                        .build())
                .toList());
        response.setDiscounts(booking.getRooms().stream()
                .flatMap(room -> room.getDiscounts().stream())
                .map(discountMapper::toDiscountResponse)
                .toList());
        return response;
    }

    public void createPayment(String orderInfo, LocalDateTime paymentTime, int paymentStatus, String totalPrice, String transactionId, String vnpBankCode, String vnpBankTranNo, String vnpCardType, String vnpTxnRef, String vnpSecureHash) {
        Booking booking = bookingRepository.findById(orderInfo)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKING_NOT_FOUND));

        Payment payment = Payment.builder()
                .amount(Integer.parseInt(totalPrice))
                .transactionId(transactionId)
                .date(paymentTime.toLocalDate())
                .status(paymentStatus == 1 ? PaymentStatus.SUCCESS.name() : PaymentStatus.FAILED.name())
                .note("Payment via VNPay with info: vnpBankCode = " + vnpBankCode + ", vnpBankTranNo = " + vnpBankTranNo + ", vnpCardType = " + vnpCardType + ", vnpTxnRef = " + vnpTxnRef + ", vnpSecureHash = " + vnpSecureHash)
                .paymentMethod(vnpCardType)
                .booking(booking)
                .build();

        booking.setPayment(payment);
        bookingRepository.save(booking);
    }
}
