package com.homestay.service;

import com.homestay.dto.request.AmenityRequest;
import com.homestay.dto.response.AmenityResponse;
import com.homestay.exception.BusinessException;
import com.homestay.exception.ErrorCode;
import com.homestay.model.Amenity;
import com.homestay.repository.AmenityRepository;
import com.homestay.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AmenityService {
    @Autowired
    AmenityRepository amenityRepository;
    @Autowired
    RoomRepository roomRepository;

    public List<AmenityResponse> getAmenities() {
        return amenityRepository.findAll().stream()
                .map(amenity -> AmenityResponse.builder()
                        .id(amenity.getId())
                        .name(amenity.getName())
                        .description(amenity.getDescription())
                        .icon(amenity.getIcon())
                        .rooms(amenity.getRooms().stream()
                                .map(room -> room.getName())
                                .collect(Collectors.toSet()))
                        .build())
                .collect(Collectors.toList());
    }

    public AmenityResponse createAmenity(AmenityRequest amenityRequest) {
        if (amenityRepository.existsByName(amenityRequest.getName())) {
            throw new BusinessException(ErrorCode.AMENITY_ALREADY_EXISTS);
        }
        Amenity amenity = Amenity.builder()
                .name(amenityRequest.getName())
                .description(amenityRequest.getDescription())
                .icon(amenityRequest.getIcon())
                .build();
        Amenity savedAmenity = amenityRepository.save(amenity);
        return AmenityResponse.builder()
                .id(savedAmenity.getId())
                .name(savedAmenity.getName())
                .description(savedAmenity.getDescription())
                .icon(savedAmenity.getIcon())
                .build();
    }

    public AmenityResponse getAmenityById(String id) {
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.AMENITY_NOT_FOUND));
        return AmenityResponse.builder()
                .id(amenity.getId())
                .name(amenity.getName())
                .description(amenity.getDescription())
                .icon(amenity.getIcon())
                .rooms(amenity.getRooms().stream()
                        .map(room -> room.getName())
                        .collect(Collectors.toSet()))
                .build();
    }

    public AmenityResponse updateAmenity(String id, AmenityRequest amenityRequest) {
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.AMENITY_NOT_FOUND));

        amenity.setName(amenityRequest.getName());
        amenity.setDescription(amenityRequest.getDescription());
        amenity.setIcon(amenityRequest.getIcon());
        Amenity savedAmenity = amenityRepository.save(amenity);
        return AmenityResponse.builder()
                .id(savedAmenity.getId())
                .name(savedAmenity.getName())
                .description(savedAmenity.getDescription())
                .icon(savedAmenity.getIcon())
                .rooms(savedAmenity.getRooms().stream()
                        .map(r -> r.getName())
                        .collect(Collectors.toSet()))
                .build();
    }

    public void deleteAmenity(String id) {
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.AMENITY_NOT_FOUND));
        amenityRepository.delete(amenity);
    }
}
