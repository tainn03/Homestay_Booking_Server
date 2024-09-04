package com.homestay.service;

import com.homestay.dto.request.RoomRequest;
import com.homestay.dto.response.RoomResponse;
import com.homestay.exception.BusinessException;
import com.homestay.exception.ErrorCode;
import com.homestay.model.Amenity;
import com.homestay.model.Homestay;
import com.homestay.model.Room;
import com.homestay.repository.AmenityRepository;
import com.homestay.repository.HomestayRepository;
import com.homestay.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class RoomService {
    @Autowired
    RoomRepository roomRepository;

    @Autowired
    HomestayRepository homestayRepository;

    @Autowired
    AmenityRepository amenityRepository;

    public RoomResponse createRoom(RoomRequest request) {
        Homestay homestay = homestayRepository.findById(request.getHomestayId())
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));

        Room existingRoom = roomRepository.findByNameAndHomestayId(request.getName(), request.getHomestayId());
        if (Optional.ofNullable(existingRoom).isPresent()) {
            throw new BusinessException(ErrorCode.ROOM_ALREADY_EXISTS);
        }

        Room room = Room.builder()
                .name(request.getName())
                .price(request.getPrice())
                .size(request.getSize())
                .description(request.getDescription())
                .homestay(homestay)
                .build();
        Room savedRoom = roomRepository.save(room);

        return RoomResponse.builder()
                .id(savedRoom.getId())
                .name(savedRoom.getName())
                .price(savedRoom.getPrice())
                .size(savedRoom.getSize())
                .description(savedRoom.getDescription())
                .homestayName(savedRoom.getHomestay().getName())
//                .amenities(savedRoom.getAmenities().stream().map(amenity -> amenity.getName()).collect(Collectors.toSet()))
//                .bookings(savedRoom.getBookings().stream().map(booking -> booking.getId()).collect(Collectors.toSet()))
                .build();
    }

    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(room -> RoomResponse.builder()
                        .id(room.getId())
                        .name(room.getName())
                        .price(room.getPrice())
                        .size(room.getSize())
                        .description(room.getDescription())
                        .homestayName(room.getHomestay().getName())
                        .amenities(room.getAmenities().stream().map(amenity -> amenity.getName()).collect(Collectors.toSet()))
                        .bookings(room.getBookings().stream().map(booking -> booking.getId()).collect(Collectors.toSet()))
                        .build())
                .toList();
    }

    public RoomResponse getRoomById(String id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));
        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .price(room.getPrice())
                .size(room.getSize())
                .description(room.getDescription())
                .homestayName(room.getHomestay().getName())
                .amenities(room.getAmenities().stream().map(amenity -> amenity.getName()).collect(Collectors.toSet()))
                .bookings(room.getBookings().stream().map(booking -> booking.getId()).collect(Collectors.toSet()))
                .build();

    }

    public RoomResponse updateRoom(String id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));
        Homestay homestay = homestayRepository.findById(request.getHomestayId())
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));
        Set<Amenity> amenities = new HashSet<>(amenityRepository.findAllById(request.getAmenityIds()));

        room.setName(request.getName());
        room.setPrice(request.getPrice());
        room.setSize(request.getSize());
        room.setDescription(request.getDescription());
        room.setHomestay(homestay);
        room.setAmenities(amenities); // Update amenities
        Room updatedRoom = roomRepository.save(room);

        return RoomResponse.builder()
                .id(updatedRoom.getId())
                .name(updatedRoom.getName())
                .price(updatedRoom.getPrice())
                .size(updatedRoom.getSize())
                .description(updatedRoom.getDescription())
                .homestayName(updatedRoom.getHomestay().getName())
                .amenities(updatedRoom.getAmenities().stream().map(amenity -> amenity.getName()).collect(Collectors.toSet()))
                .bookings(updatedRoom.getBookings().stream().map(booking -> booking.getId()).collect(Collectors.toSet()))
                .build();
    }

    public void deleteRoom(String id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));
        roomRepository.delete(room);
    }

    public List<RoomResponse> getRoomsByHomestayId(String homestayId) {
        Homestay homestay = homestayRepository.findById(homestayId)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));
        return roomRepository.findByHomestayId(homestayId).stream()
                .map(room -> RoomResponse.builder()
                        .id(room.getId())
                        .name(room.getName())
                        .price(room.getPrice())
                        .size(room.getSize())
                        .description(room.getDescription())
                        .homestayName(room.getHomestay().getName())
                        .amenities(room.getAmenities().stream().map(amenity -> amenity.getName()).collect(Collectors.toSet()))
                        .bookings(room.getBookings().stream().map(booking -> booking.getId()).collect(Collectors.toSet()))
                        .build())
                .toList();
    }
}
