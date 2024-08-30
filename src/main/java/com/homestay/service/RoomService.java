package com.homestay.service;

import com.homestay.dto.request.RoomRequest;
import com.homestay.dto.response.RoomResponse;
import com.homestay.exception.BusinessException;
import com.homestay.exception.ErrorCode;
import com.homestay.model.Homestay;
import com.homestay.model.Room;
import com.homestay.repository.HomestayRepository;
import com.homestay.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class RoomService {
    @Autowired
    RoomRepository roomRepository;

    @Autowired
    HomestayRepository homestayRepository;

    public RoomResponse createRoom(RoomRequest request) {
        Homestay homestay = homestayRepository.findById(request.getHomestayId())
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));

        Room room = Room.builder()
                .name(request.getName())
                .size(request.getSize())
                .description(request.getDescription())
                .homestay(homestay)
                .build();
        Room savedRoom = roomRepository.save(room);

        return RoomResponse.builder()
                .id(savedRoom.getId())
                .name(savedRoom.getName())
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
                        .size(room.getSize())
                        .description(room.getDescription())
                        .homestayName(room.getHomestay().getName())
//                        .amenities(room.getAmenities().stream().map(amenity -> amenity.getName()).collect(Collectors.toSet()))
//                        .bookings(room.getBookings().stream().map(booking -> booking.getId()).collect(Collectors.toSet()))
                        .build())
                .toList();
    }

    public RoomResponse getRoomById(String id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));
        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .size(room.getSize())
                .description(room.getDescription())
                .homestayName(room.getHomestay().getName())
//                .amenities(room.getAmenities().stream().map(amenity -> amenity.getName()).collect(Collectors.toSet()))
//                .bookings(room.getBookings().stream().map(booking -> booking.getId()).collect(Collectors.toSet()))
                .build();
    }

    public RoomResponse updateRoom(String id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));
        Homestay homestay = homestayRepository.findById(request.getHomestayId())
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));

        room.setName(request.getName());
        room.setSize(request.getSize());
        room.setDescription(request.getDescription());
        room.setHomestay(homestay);
        Room updatedRoom = roomRepository.save(room);

        return RoomResponse.builder()
                .id(updatedRoom.getId())
                .name(updatedRoom.getName())
                .size(updatedRoom.getSize())
                .description(updatedRoom.getDescription())
                .homestayName(updatedRoom.getHomestay().getName())
//                .amenities(updatedRoom.getAmenities().stream().map(amenity -> amenity.getName()).collect(Collectors.toSet()))
//                .bookings(updatedRoom.getBookings().stream().map(booking -> booking.getId()).collect(Collectors.toSet()))
                .build();
    }

    public void deleteRoom(String id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));
        roomRepository.delete(room);
    }
}
