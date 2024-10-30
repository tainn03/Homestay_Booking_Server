package com.homestay.service;

import com.homestay.constants.BookingStatus;
import com.homestay.constants.DiscountType;
import com.homestay.dto.response.BookingResponse;
import com.homestay.dto.response.RoomResponse;
import com.homestay.exception.BusinessException;
import com.homestay.exception.ErrorCode;
import com.homestay.mapper.BookingMapper;
import com.homestay.mapper.DiscountMapper;
import com.homestay.model.*;
import com.homestay.repository.BookingRepository;
import com.homestay.repository.HomestayRepository;
import com.homestay.repository.RoomRepository;
import com.homestay.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingService {
    BookingRepository bookingRepository;
    RoomRepository roomRepository;
    UserRepository userRepository;
    HomestayRepository homestayRepository;
    BookingMapper bookingMapper;
    DiscountMapper discountMapper;

    public BookingResponse getReviewBooking(String homestayId, String checkIn, String checkOut, int guests) {
        Homestay homestay = homestayRepository.findById(homestayId)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));

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

        // Tính toán chi phí và giảm giá
        List<Discount> applicableDiscounts = new ArrayList<>(homestay.getDiscounts().stream()
                .filter(d -> (d.getStartDate() == null || !d.getStartDate().isAfter(LocalDate.parse(checkOut).atStartOfDay())) &&
                        (d.getEndDate() == null || !d.getEndDate().isBefore(LocalDate.parse(checkIn).atStartOfDay())))
                .sorted(Comparator.comparing(d -> {
                    if (Objects.equals(d.getType(), DiscountType.WEEKLY.toString())) return 1;
                    if (Objects.equals(d.getType(), DiscountType.MONTHLY.toString())) return 2;
                    return 3;
                }))
                .toList());

        int numOfWeekend = 0;
        int numOfWeekday = 0;
        double originalCost = 0;
        double totalCost = 0;
        int totalDiscount = 0;

        for (LocalDate date = LocalDate.parse(checkIn); !date.isEqual(LocalDate.parse(checkOut)); date = date.plusDays(1)) {
            double dailyRate = date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY
                    ? homestay.getWeekendPrice()
                    : homestay.getPrice();

            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                numOfWeekend++;
            } else {
                numOfWeekday++;
            }

            double dailyCost = dailyRate * selectedRooms.size();
            originalCost += dailyCost;

            int valueOfDiscountValueInDay = 0;
            for (Discount discount : applicableDiscounts) {
                if (discount.getStartDate() != null && discount.getEndDate() != null
                        && !date.isBefore(discount.getStartDate().toLocalDate())
                        && !date.isAfter(discount.getEndDate().toLocalDate())) {
                    valueOfDiscountValueInDay += (int) discount.getValue();
                }
            }
            totalDiscount += valueOfDiscountValueInDay;
            dailyCost -= (dailyCost * valueOfDiscountValueInDay) / 100;
            totalCost += dailyCost;
        }

        // Check for weekly (7 nights) or monthly (28 nights) discounts and update the discount
        long totalNights = ChronoUnit.DAYS.between(LocalDate.parse(checkIn), LocalDate.parse(checkOut));
        for (Discount discount : applicableDiscounts) {
            if ((Objects.equals(discount.getType(), DiscountType.WEEKLY.toString()) && totalNights >= 7) ||
                    (Objects.equals(discount.getType(), DiscountType.MONTHLY.toString()) && totalNights >= 28)) {
                totalDiscount += (int) discount.getValue();
                totalCost -= (totalCost * discount.getValue()) / 100;
            }
        }

        Booking booking = Booking.builder()
                .checkIn(LocalDate.parse(checkIn))
                .checkOut(LocalDate.parse(checkOut))
                .status(BookingStatus.REVIEW.name())
                .total(totalCost)
                .guests(guests)
                .note("Xem xét đặt phòng")
                .rooms(selectedRooms)
                .build();

        return getBookingResponse(selectedRooms, applicableDiscounts, numOfWeekend, numOfWeekday, originalCost, totalDiscount, (int) totalNights, booking);
    }


    public BookingResponse createBooking(String homestayId, String checkIn, String checkOut, int guests) {
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Homestay homestay = homestayRepository.findById(homestayId)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));

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

        // Tính toán chi phí và giảm giá
        List<Discount> applicableDiscounts = new ArrayList<>(homestay.getDiscounts().stream()
                .filter(d -> (d.getStartDate() == null || !d.getStartDate().isAfter(LocalDate.parse(checkOut).atStartOfDay())) &&
                        (d.getEndDate() == null || !d.getEndDate().isBefore(LocalDate.parse(checkIn).atStartOfDay())))
                .sorted(Comparator.comparing(d -> {
                    if (Objects.equals(d.getType(), DiscountType.WEEKLY.toString())) return 1;
                    if (Objects.equals(d.getType(), DiscountType.MONTHLY.toString())) return 2;
                    return 3;
                }))
                .toList());

        int numOfWeekend = 0;
        int numOfWeekday = 0;
        double originalCost = 0;
        double totalCost = 0;
        int totalDiscount = 0;

        for (LocalDate date = LocalDate.parse(checkIn); !date.isEqual(LocalDate.parse(checkOut)); date = date.plusDays(1)) {
            double dailyRate = date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY
                    ? homestay.getWeekendPrice()
                    : homestay.getPrice();

            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                numOfWeekend++;
            } else {
                numOfWeekday++;
            }

            double dailyCost = dailyRate * selectedRooms.size();
            originalCost += dailyCost;

            int valueOfDiscountValueInDay = 0;
            for (Discount discount : applicableDiscounts) {
                if (discount.getStartDate() != null && discount.getEndDate() != null
                        && !date.isBefore(discount.getStartDate().toLocalDate())
                        && !date.isAfter(discount.getEndDate().toLocalDate())) {
                    valueOfDiscountValueInDay += (int) discount.getValue();
                }
            }
            totalDiscount += valueOfDiscountValueInDay;
            dailyCost -= (dailyCost * valueOfDiscountValueInDay) / 100;
            totalCost += dailyCost;
        }

        // Check for weekly (7 nights) or monthly (28 nights) discounts and update the discount
        long totalNights = ChronoUnit.DAYS.between(LocalDate.parse(checkIn), LocalDate.parse(checkOut));
        for (Discount discount : applicableDiscounts) {
            if ((Objects.equals(discount.getType(), DiscountType.WEEKLY.toString()) && totalNights >= 7) ||
                    (Objects.equals(discount.getType(), DiscountType.MONTHLY.toString()) && totalNights >= 28)) {
                totalDiscount += (int) discount.getValue();
                totalCost -= (totalCost * discount.getValue()) / 100;
            }
        }

        Booking booking = Booking.builder()
                .checkIn(LocalDate.parse(checkIn))
                .checkOut(LocalDate.parse(checkOut))
                .status(BookingStatus.PENDING.name())
                .total(totalCost)
                .guests(guests)
                .user(user)
                .note("Chưa thanh toán")
                .rooms(selectedRooms)
                .build();
        Booking savedBooking = bookingRepository.save(booking);

        return getBookingResponse(selectedRooms, applicableDiscounts, numOfWeekend, numOfWeekday, originalCost, totalDiscount, (int) totalNights, savedBooking);
    }

    private BookingResponse getBookingResponse(List<Room> selectedRooms, List<Discount> applicableDiscounts, int numOfWeekend, int numOfWeekday, double originalCost, int totalDiscount, int totalNights, Booking booking) {
        BookingResponse bookingResponse = bookingMapper.toBookingResponse(booking);
        bookingResponse.setRooms(selectedRooms.stream()
                .map(room -> RoomResponse.builder()
                        .id(room.getId())
                        .name(room.getName())
                        .size(room.getSize())
                        .build())
                .toList());
        bookingResponse.setNights(totalNights);
        bookingResponse.setTotalDiscount(totalDiscount);
        bookingResponse.setOriginalTotal(originalCost);
        bookingResponse.setNumOfWeekend(numOfWeekend);
        bookingResponse.setNumOfWeekday(numOfWeekday);
        bookingResponse.setDiscounts(applicableDiscounts.stream()
                .map(discountMapper::toDiscountResponse)
                .toList());

        return bookingResponse;
    }

    public BookingResponse getBooking(String id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKING_NOT_FOUND));
        Homestay homestay = booking.getRooms().getFirst().getHomestay();
        List<Discount> applicableDiscounts = homestay.getDiscounts().stream()
                .filter(d -> (d.getStartDate() == null || !d.getStartDate().isAfter(booking.getCheckIn().atStartOfDay())) &&
                        (d.getEndDate() == null || !d.getEndDate().isBefore(booking.getCheckOut().atStartOfDay())))
                .sorted(Comparator.comparing(d -> {
                    if (Objects.equals(d.getType(), DiscountType.WEEKLY.toString())) return 1;
                    if (Objects.equals(d.getType(), DiscountType.MONTHLY.toString())) return 2;
                    return 3;
                }))
                .toList();

        BookingResponse bookingResponse = bookingMapper.toBookingResponse(booking);
        bookingResponse.setRooms(booking.getRooms().stream()
                .map(room -> RoomResponse.builder()
                        .id(room.getId())
                        .name(room.getName())
                        .size(room.getSize())
                        .bookings(room.getBookings().stream().map(Booking::getId).collect(Collectors.toSet()))
                        .build())
                .toList());
        bookingResponse.setNights((int) ChronoUnit.DAYS.between(booking.getCheckIn(), booking.getCheckOut()));
        bookingResponse.setDiscounts(applicableDiscounts.stream()
                .map(discountMapper::toDiscountResponse)
                .toList());
        bookingResponse.setNumOfWeekend((int) booking.getCheckIn().datesUntil(booking.getCheckOut())
                .filter(date -> date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)
                .count());
        bookingResponse.setNumOfWeekday((int) booking.getCheckIn().datesUntil(booking.getCheckOut())
                .filter(date -> date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY)
                .count());
        bookingResponse.setHomestayId(homestay.getId());
        bookingResponse.setOriginalTotal(calculateOriginalTotal(booking, homestay));
        bookingResponse.setTotalDiscount(calculateTotalDiscount(booking, applicableDiscounts, booking.getCheckIn(), booking.getCheckOut()));
        return bookingResponse;
    }

    private double calculateOriginalTotal(Booking booking, Homestay homestay) {
        double originalTotal = 0;
        for (LocalDate date = booking.getCheckIn(); !date.isEqual(booking.getCheckOut()); date = date.plusDays(1)) {
            double dailyRate = date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY
                    ? homestay.getWeekendPrice()
                    : homestay.getPrice();
            originalTotal += dailyRate * booking.getRooms().size();
        }
        return originalTotal;
    }

    private int calculateTotalDiscount(Booking booking, List<Discount> applicableDiscounts, LocalDate checkIn, LocalDate checkOut) {
        int totalDiscount = 0;
        for (LocalDate date = booking.getCheckIn(); !date.isEqual(booking.getCheckOut()); date = date.plusDays(1)) {
            int valueOfDiscountValueInDay = 0;
            for (Discount discount : applicableDiscounts) {
                if (discount.getStartDate() != null && discount.getEndDate() != null
                        && !date.isBefore(discount.getStartDate().toLocalDate())
                        && !date.isAfter(discount.getEndDate().toLocalDate())) {
                    valueOfDiscountValueInDay += (int) discount.getValue();
                }
            }
            totalDiscount += valueOfDiscountValueInDay;
        }
        long totalNights = ChronoUnit.DAYS.between(LocalDate.parse(String.valueOf(checkIn)), LocalDate.parse(String.valueOf(checkOut)));
        for (Discount discount : applicableDiscounts) {
            if ((Objects.equals(discount.getType(), DiscountType.WEEKLY.toString()) && totalNights >= 7) ||
                    (Objects.equals(discount.getType(), DiscountType.MONTHLY.toString()) && totalNights >= 28)) {
                totalDiscount += (int) discount.getValue();
            }
        }
        return totalDiscount;
    }
}
