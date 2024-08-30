package com.homestay.service;

import com.homestay.dto.request.BookingRequest;
import com.homestay.dto.response.BookingResponse;
import com.homestay.enums.BookingStatus;
import com.homestay.exception.BusinessException;
import com.homestay.exception.ErrorCode;
import com.homestay.model.Booking;
import com.homestay.model.Room;
import com.homestay.repository.BookingRepository;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingService {
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;

    public BookingResponse createBooking(@Valid BookingRequest request) {
        // Validate check-in and check-out dates
        if (LocalDate.parse(request.getCheckIn()).isAfter(LocalDate.parse(request.getCheckOut()))) {
            throw new BusinessException(ErrorCode.CHECKIN_AFTER_CHECKOUT);
        }

        // Find available rooms
        List<Room> availableRooms = roomRepository.findAvailableRooms(LocalDate.parse(request.getCheckIn()), LocalDate.parse(request.getCheckOut()));
        if (availableRooms.isEmpty()) {
            throw new BusinessException(ErrorCode.NO_AVAILABLE_ROOMS);
        }

        // Select the first available room (or implement a more complex selection logic)
        Room selectedRoom = availableRooms.stream()
                .filter(room -> room.getSize() >= request.getGuests())
                .findFirst()
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
                .build();
    }

    public BookingResponse updateBooking(String id, @Valid BookingRequest request) {
        // Validate check-in and check-out dates
        if (LocalDate.parse(request.getCheckIn()).isAfter(LocalDate.parse(request.getCheckOut()))) {
            throw new BusinessException(ErrorCode.CHECKIN_AFTER_CHECKOUT);
        }

        // Find the existing booking
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKING_NOT_FOUND));

        // Find available rooms
        List<Room> availableRooms = roomRepository.findAvailableRooms(LocalDate.parse(request.getCheckIn()), LocalDate.parse(request.getCheckOut()));
        if (availableRooms.isEmpty()) {
            throw new BusinessException(ErrorCode.NO_AVAILABLE_ROOMS);
        }

        // Select the first available room (or implement a more complex selection logic)
        Room selectedRoom = availableRooms.get(0);

        // Calculate total cost based on the number of days
        long days = ChronoUnit.DAYS.between(LocalDate.parse(request.getCheckIn()), LocalDate.parse(request.getCheckOut()));
        double totalCost = days * selectedRoom.getPrice();

        // Update the booking details
        existingBooking.setCheckIn(LocalDate.parse(request.getCheckIn()));
        existingBooking.setCheckOut(LocalDate.parse(request.getCheckOut()));
        existingBooking.setStatus(request.getStatus());
        existingBooking.setTotal(totalCost);
        existingBooking.setGuests(request.getGuests());
        existingBooking.setNote(request.getNote());
        existingBooking.setRoom(selectedRoom);
//        existingBooking.setPayment(request.getPayment());

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
//                .payment(existingBooking.getPayment())
                .build();
    }

    public void deleteBooking(String id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKING_NOT_FOUND));
        bookingRepository.delete(booking);
    }
}
