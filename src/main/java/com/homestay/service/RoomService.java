package com.homestay.service;

import com.homestay.exception.BusinessException;
import com.homestay.exception.ErrorCode;
import com.homestay.model.Image;
import com.homestay.model.Room;
import com.homestay.repository.ImageRepository;
import com.homestay.repository.RoomRepository;
import com.homestay.service.external.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class RoomService {
    RoomRepository roomRepository;
    CloudinaryService cloudinaryService;
    ImageRepository imageRepository;

    public String createRoomImages(String nameRoom, List<MultipartFile> images, String homestayId) {
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

        return "Upload images successfully";
    }
}
