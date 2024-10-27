package com.homestay.service;

import com.homestay.dto.request.RoomRequest;
import com.homestay.dto.response.RoomResponse;
import com.homestay.exception.BusinessException;
import com.homestay.exception.ErrorCode;
import com.homestay.model.Homestay;
import com.homestay.model.Image;
import com.homestay.model.Room;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomService {
    RoomRepository roomRepository;
    HomestayRepository homestayRepository;
    CloudinaryService cloudinaryService;
    ImageRepository imageRepository;

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

        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .size(room.getSize())
                .images(room.getImages().stream().map(Image::getUrl).toList())
                .build();
    }

    public RoomResponse createRooms(String homestayId, RoomRequest request) {
        Homestay homestay = homestayRepository.findById(homestayId)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));
        Room room = Room.builder()
                .name(request.getName())
                .size(request.getSize())
                .homestay(homestay)
                .build();
        roomRepository.save(room);

        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .size(room.getSize())
                .build();
    }

    public RoomResponse updateRoom(String id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        room.setName(request.getName());
        room.setSize(request.getSize());

        // Delete old images
        List<String> existingImageUrls = room.getImages().stream().map(Image::getUrl).toList();
        List<String> newImageUrls = request.getImages();

        List<String> urlsToDelete = existingImageUrls.stream()
                .filter(url -> !newImageUrls.contains(url))
                .toList();
        cloudinaryService.deleteFiles(urlsToDelete);
        imageRepository.deleteByUrlIn(urlsToDelete);

        roomRepository.save(room);

        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .size(room.getSize())
                .images(room.getImages().stream().map(Image::getUrl).toList())
                .build();
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
