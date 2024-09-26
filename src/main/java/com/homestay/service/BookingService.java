package com.homestay.service;

import com.homestay.constants.BookingStatus;
import com.homestay.dto.request.BookingRequest;
import com.homestay.dto.response.BookingResponse;
import com.homestay.exception.BusinessException;
import com.homestay.exception.ErrorCode;
import com.homestay.model.Booking;
import com.homestay.model.Room;
import com.homestay.repository.BookingRepository;
import com.homestay.repository.HomestayRepository;
import com.homestay.repository.RoomRepository;
import com.homestay.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingService {
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    HomestayRepository homestayRepository;

    public BookingResponse createBooking(@Valid BookingRequest request, String homestayId) {
        // Validate check-in and check-out dates
        if (LocalDate.parse(request.getCheckIn()).isAfter(LocalDate.parse(request.getCheckOut()))) {
            throw new BusinessException(ErrorCode.CHECKIN_AFTER_CHECKOUT);
        }

        // validate date in the past
        if (LocalDate.parse(request.getCheckIn()).isBefore(LocalDate.now()) || LocalDate.parse(request.getCheckOut()).isBefore(LocalDate.now())) {
            throw new BusinessException(ErrorCode.CHECKIN_CHECKOUT_IN_PAST);
        }

        // Validate the Homestay
        if (!homestayRepository.existsById(homestayId)) {
            throw new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND);
        }

        // Find available rooms
        List<Room> availableRooms = roomRepository.findAvailableRoomsByHomestayId(homestayId, LocalDate.parse(request.getCheckIn()), LocalDate.parse(request.getCheckOut()));
        if (availableRooms.isEmpty()) {
            throw new BusinessException(ErrorCode.NO_AVAILABLE_ROOMS);
        }

        // Select the first available room (smaller rooms are preferred)
        Room selectedRoom = availableRooms.stream()
                .filter(room -> room.getSize() >= request.getGuests())
                .min(Comparator.comparingInt(Room::getSize))
                .orElseThrow(() -> new BusinessException(ErrorCode.NO_AVAILABLE_ROOMS));

        // Calculate total cost based on the number of days
        long days = ChronoUnit.DAYS.between(LocalDate.parse(request.getCheckIn()), LocalDate.parse(request.getCheckOut()));
        double totalCost = days * selectedRoom.getPrice();

        // Create and save the booking
        Booking booking = Booking.builder()
                .checkIn(LocalDate.parse(request.getCheckIn()))
                .checkOut(LocalDate.parse(request.getCheckOut()))
                .status(BookingStatus.PENDING.name())
                .total(totalCost)
                .guests(request.getGuests())
                .note(request.getNote())
                .user(userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND)))
                .room(selectedRoom)
//                .payment(request.getPayment())
                .build();

        bookingRepository.save(booking);

        // Return the booking response
        return BookingResponse.builder()
                .id(booking.getId())
                .checkIn(booking.getCheckIn())
                .checkOut(booking.getCheckOut())
                .status(booking.getStatus())
                .total(booking.getTotal())
                .guests(booking.getGuests())
                .note(booking.getNote())
                .user(booking.getUser().getUsername())
                .roomName(booking.getRoom().getName())
                .homestayName(booking.getRoom().getHomestay().getName())
//                .payment(booking.getPayment())
                .build();
    }

    // BookingService.java
    public List<BookingResponse> getBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream().map(booking -> BookingResponse.builder()
                        .id(booking.getId())
                        .checkIn(booking.getCheckIn())
                        .checkOut(booking.getCheckOut())
                        .status(booking.getStatus())
                        .total(booking.getTotal())
                        .guests(booking.getGuests())
                        .note(booking.getNote())
                        .user(booking.getUser().getUsername())
                        .roomName(booking.getRoom().getName())
                        .homestayName(booking.getRoom().getHomestay().getName())
                        .build())
                .collect(Collectors.toList());
    }

    public BookingResponse getBooking(String id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKING_NOT_FOUND));
        return BookingResponse.builder()
                .id(booking.getId())
                .checkIn(booking.getCheckIn())
                .checkOut(booking.getCheckOut())
                .status(booking.getStatus())
                .total(booking.getTotal())
                .guests(booking.getGuests())
                .note(booking.getNote())
                .user(booking.getUser().getUsername())
                .roomName(booking.getRoom().getName())
                .homestayName(booking.getRoom().getHomestay().getName())
                .build();
    }

    public BookingResponse updateBooking(String id, String status) {
        // Find the existing booking
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKING_NOT_FOUND));

        // Update the booking details
        existingBooking.setStatus(status);

        // Save the updated booking
        bookingRepository.save(existingBooking);

        // Return the updated booking response
        return BookingResponse.builder()
                .id(existingBooking.getId())
                .checkIn(existingBooking.getCheckIn())
                .checkOut(existingBooking.getCheckOut())
                .status(existingBooking.getStatus())
                .total(existingBooking.getTotal())
                .guests(existingBooking.getGuests())
                .note(existingBooking.getNote())
                .user(existingBooking.getUser().getUsername())
                .roomName(existingBooking.getRoom().getName())
                .homestayName(existingBooking.getRoom().getHomestay().getName())
//                .payment(existingBooking.getPayment())
                .build();
    }

    public void deleteBooking(String id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKING_NOT_FOUND));
        bookingRepository.delete(booking);
    }
}
