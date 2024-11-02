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
    HomestayRepository homestayRepository;

    public BookingResponse booking(String homestayId, String checkIn, String checkOut, int guests, String status) {
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
        bookingResponse.setDiscounts(selectedRooms.stream()
                .flatMap(room -> room.getDiscounts().stream())
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
