package com.homestay.service;

import com.homestay.constants.RoomStatus;
import com.homestay.dto.request.RoomRequest;
import com.homestay.dto.response.RoomResponse;
import com.homestay.exception.BusinessException;
import com.homestay.exception.ErrorCode;
import com.homestay.mapper.RoomMapper;
import com.homestay.model.*;
import com.homestay.repository.AmenityRepository;
import com.homestay.repository.HomestayRepository;
import com.homestay.repository.ImageRepository;
import com.homestay.repository.RoomRepository;
import com.homestay.service.external.CloudinaryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomService {
    AmenityRepository amenityRepository;
    RoomRepository roomRepository;
    HomestayRepository homestayRepository;
    CloudinaryService cloudinaryService;
    ImageRepository imageRepository;
    RoomMapper roomMapper;

    public RoomResponse addRoomImages(String nameRoom, List<MultipartFile> images, String homestayId) {
        Room room = roomRepository.findByNameAndHomestayId(nameRoom, homestayId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        List<Image> existingImages = room.getImages();
        List<String> photoUrls = cloudinaryService.uploadFiles(images);
        for (String photoUrl : photoUrls) {
            Image image = Image.builder()
                    .url(photoUrl)
                    .room(room)
                    .build();
            existingImages.add(image);
        }

        room.setImages(existingImages);
        roomRepository.save(room);

        return roomMapper.toRoomResponse(room);
    }

    public RoomResponse createRooms(String homestayId, RoomRequest request) {
        Homestay homestay = homestayRepository.findById(homestayId)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));
        Room room = Room.builder()
                .name(request.getName())
                .size(request.getSize())
                .price(request.getPrice())
                .weekendPrice(request.getWeekendPrice())
                .status(Objects.equals(request.getStatus(), "") ? RoomStatus.ACTIVE.name() : request.getStatus())
                .amenities(request.getAmenities().stream().map(amenityRequest -> amenityRepository.findById(amenityRequest.getName())
                                .orElse(Amenity.builder()
                                        .name(amenityRequest.getName())
                                        .type(amenityRequest.getType())
                                        .build()))
                        .collect(toSet()))
                .discounts(request.getDiscounts().stream().map(discountRequest -> Discount.builder()
                                .value(discountRequest.getValue())
                                .type(discountRequest.getType())
                                .description(discountRequest.getDescription())
                                .startDate(discountRequest.getStartDate())
                                .endDate(discountRequest.getEndDate())
                                .build())
                        .collect(toSet()))
                .homestay(homestay)
                .build();
        roomRepository.save(room);

        return roomMapper.toRoomResponse(room);
    }

    public RoomResponse updateRoom(String id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        room = roomMapper.toRoom(room, request);

        room.getAmenities().clear();
        room.getAmenities().addAll(request.getAmenities().stream().map(amenityRequest ->
                        amenityRepository.findById(amenityRequest.getName())
                                .orElse(Amenity.builder()
                                        .name(amenityRequest.getName())
                                        .type(amenityRequest.getType())
                                        .build()))
                .collect(toSet()));

        Room finalRoom = room;
        request.getDiscounts().forEach(discountRequest -> {
            boolean isDuplicate = finalRoom.getDiscounts().stream()
                    .anyMatch(d -> d.getValue() == discountRequest.getValue() &&
                            d.getStartDate().equals(discountRequest.getStartDate()) &&
                            d.getEndDate().equals(discountRequest.getEndDate()));
            if (isDuplicate) {
                throw new BusinessException(ErrorCode.DISCOUNT_ALREADY_EXIST);
            }
            Discount discount = finalRoom.getDiscounts().stream()
                    .filter(d -> d.getId().equals(discountRequest.getId()))
                    .findFirst()
                    .orElse(Discount.builder()
                            .value(discountRequest.getValue())
                            .type(discountRequest.getType())
                            .description(discountRequest.getDescription())
                            .startDate(discountRequest.getStartDate())
                            .endDate(discountRequest.getEndDate())
                            .build());
            discount.setValue(discountRequest.getValue());
            discount.setType(discountRequest.getType());
            discount.setDescription(discountRequest.getDescription());
            discount.setStartDate(discountRequest.getStartDate());
            discount.setEndDate(discountRequest.getEndDate());
            finalRoom.getDiscounts().add(discount);
        });

        // Delete old images
        List<String> existingImageUrls = room.getImages().stream().map(Image::getUrl).toList();
        List<String> newImageUrls = request.getImages();
        List<String> urlsToDelete = existingImageUrls.stream()
                .filter(url -> !newImageUrls.contains(url))
                .toList();
        cloudinaryService.deleteFiles(urlsToDelete);
        imageRepository.deleteByUrlIn(urlsToDelete);

        roomRepository.save(room);

        return roomMapper.toRoomResponse(room);
    }

    @Transactional
    public String deleteRoom(String id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        List<String> imageUrls = room.getImages().stream().map(Image::getUrl).toList();
        cloudinaryService.deleteFiles(imageUrls);
        imageRepository.deleteByUrlIn(imageUrls);

        roomRepository.delete(room);
        return "Room deleted successfully";
    }
}
