package com.homestay.service;

import com.homestay.constants.RoomStatus;
import com.homestay.dto.request.ChangeDiscountValueRequest;
import com.homestay.dto.request.CustomPriceRequest;
import com.homestay.dto.request.DiscountRequest;
import com.homestay.dto.request.RoomRequest;
import com.homestay.dto.response.RoomResponse;
import com.homestay.exception.BusinessException;
import com.homestay.exception.ErrorCode;
import com.homestay.mapper.RoomMapper;
import com.homestay.model.*;
import com.homestay.repository.*;
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
    PriceCalendarRepository priceCalendarRepository;
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
            Discount discount = finalRoom.getDiscounts().stream()
                    .filter(d -> discountRequest.getId() != null && discountRequest.getId().equals(d.getId()))
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

        List<Image> images = room.getImages();
        List<String> imageUrls = images.stream().map(Image::getUrl).toList();
        cloudinaryService.deleteFiles(imageUrls);

        roomRepository.delete(room);
        return "Room deleted successfully";
    }

    public RoomResponse updateRoomPrice(double price, String id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));
        room.setPrice(price);
        roomRepository.save(room);
        return roomMapper.toRoomResponse(room);
    }

    public RoomResponse updateRoomWeekendPrice(double weekendPrice, String id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));
        room.setWeekendPrice(weekendPrice);
        roomRepository.save(room);
        return roomMapper.toRoomResponse(room);
    }

    public RoomResponse updateRoomPriceCalendar(List<CustomPriceRequest> requests, String id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        room.getPriceCalendars().clear();
        requests.forEach(request -> {
            PriceCalendar priceCalendar = PriceCalendar.builder()
                    .date(request.getDate())
                    .price(request.getPrice())
                    .room(room)
                    .build();
            priceCalendarRepository.save(priceCalendar);
        });
        roomRepository.save(room);
        return roomMapper.toRoomResponse(room);
    }

    public RoomResponse updateRoomSystemDiscount(ChangeDiscountValueRequest request, String id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));
        room.getDiscounts().forEach(discount -> {
            if (discount.getType().equals(request.getType())) {
                discount.setValue(request.getValue());
            } else {
                Discount newDiscount = Discount.builder()
                        .type(request.getType())
                        .value(request.getValue())
                        .build();
                room.getDiscounts().add(newDiscount);
            }
        });
        roomRepository.save(room);
        return roomMapper.toRoomResponse(room);
    }

    public Discount addRoomCustomDiscount(DiscountRequest request, String id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        if (room.getDiscounts().stream()
                .anyMatch(discount -> discount.getValue() == request.getValue() &&
                        discount.getStartDate().equals(request.getStartDate()) &&
                        discount.getEndDate().equals(request.getEndDate()))) {
            throw new BusinessException(ErrorCode.DISCOUNT_ALREADY_EXIST);
        }
        Discount discount = Discount.builder()
                .type(request.getType())
                .value(request.getValue())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .room(room)
                .build();
        room.getDiscounts().add(discount);
        roomRepository.save(room);
        return discount;
    }

    public Discount updateRoomCustomDiscount(DiscountRequest request, String id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));
        Discount discount = room.getDiscounts().stream()
                .filter(d -> d.getId().equals(request.getId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.DISCOUNT_NOT_FOUND));
        discount.setType(request.getType());
        discount.setValue(request.getValue());
        discount.setDescription(request.getDescription());
        discount.setStartDate(request.getStartDate());
        discount.setEndDate(request.getEndDate());
        roomRepository.save(room);
        return discount;
    }

    public String deleteRoomCustomDiscount(String id, String discountId) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));
        Discount discount = room.getDiscounts().stream()
                .filter(d -> d.getId().equals(discountId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.DISCOUNT_NOT_FOUND));
        room.getDiscounts().remove(discount);
        roomRepository.save(room);
        return "Discount deleted successfully";
    }
}
