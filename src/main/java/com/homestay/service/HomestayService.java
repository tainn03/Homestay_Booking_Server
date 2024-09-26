package com.homestay.service;

import com.homestay.constants.HomestayStatus;
import com.homestay.dto.request.HomestayRequest;
import com.homestay.dto.response.HomestayResponse;
import com.homestay.exception.BusinessException;
import com.homestay.exception.ErrorCode;
import com.homestay.mapper.HomestayMapper;
import com.homestay.model.*;
import com.homestay.repository.DistrictRepository;
import com.homestay.repository.HomestayRepository;
import com.homestay.repository.TypeHomestayRepository;
import com.homestay.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class HomestayService {
    HomestayRepository homestayRepository;
    UserRepository userRepository;
    HomestayMapper homestayMapper;
    TypeHomestayRepository typeHomestayRepository;
    DistrictRepository districtRepository;
    CloudinaryService cloudinaryService;


    public HomestayResponse createHomestay(@Valid HomestayRequest request) {
        if (homestayRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.HOMESTAY_ALREADY_EXIST);
        }
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Homestay homestay = homestayMapper.toHomestay(request);
        homestay.setStatus(HomestayStatus.ACTIVE.name());
        Set<TypeHomestay> typeHomestays = new HashSet<>();
        request.getTypeHomestays().forEach(typeHomestay -> {
            TypeHomestay typeHomestay1 = typeHomestayRepository.findByName(typeHomestay)
                    .orElseThrow(() -> new BusinessException(ErrorCode.TYPE_HOMESTAY_NOT_FOUND));
            typeHomestays.add(typeHomestay1);
        });
        homestay.setTypeHomestays(typeHomestays);
        homestay.setDistrict(districtRepository.findByName(request.getDistrictName())
                .orElseThrow(() -> new BusinessException(ErrorCode.DISTRICT_NOT_FOUND)));
        homestay.setUser(userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND)));
        homestayRepository.save(homestay);

        HomestayResponse homestayResponse = homestayMapper.toHomestayResponse(homestay);
        return toHomeStayResponseWithRelationship(homestay, homestayResponse);
    }

    private HomestayResponse toHomeStayResponseWithRelationship(Homestay homestay, HomestayResponse homestayResponse) {
        if (homestay.getImages() != null) {
            homestayResponse.setUrlImages(homestay.getImages().stream().map(Image::getUrl).collect(Collectors.toSet()));
        }
        if (homestay.getDiscounts() != null) {
            homestayResponse.setDiscountIds(homestay.getDiscounts().stream().map(Discount::getId).collect(Collectors.toSet()));
        }
        if (homestay.getRooms() != null) {
            homestayResponse.setRoomNames(homestay.getRooms().stream().map(Room::getName).collect(Collectors.toSet()));
        }
        if (homestay.getReviews() != null) {
            homestayResponse.setReviewIds(homestay.getReviews().stream().map(Review::getId).collect(Collectors.toSet()));
        }
        if (homestay.getTypeHomestays() != null) {
            homestayResponse.setTypeHomestayNames(homestay.getTypeHomestays().stream().map(TypeHomestay::getName).collect(Collectors.toSet()));
        }
        if (homestay.getDistrict() != null) {
            homestayResponse.setDistrictName(homestay.getDistrict().getName());
        }
        return homestayResponse;
    }

    public List<HomestayResponse> getAllHomestays() {
        List<Homestay> homestays = homestayRepository.findAll();
        List<HomestayResponse> responses = homestays.stream()
                .map(homestay -> {
                    HomestayResponse response = homestayMapper.toHomestayResponse(homestay);
                    return toHomeStayResponseWithRelationship(homestay, response);
                })
                .collect(Collectors.toList());
        return responses;
    }

    public List<HomestayResponse> getHomestayByOwner() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        List<Homestay> homestays = homestayRepository.findByUserAndStatus(user, HomestayStatus.ACTIVE.name());
        List<HomestayResponse> responses = homestays.stream()
                .map(homestay -> {
                    HomestayResponse response = homestayMapper.toHomestayResponse(homestay);
                    return toHomeStayResponseWithRelationship(homestay, response);
                })
                .collect(Collectors.toList());
        return responses;
    }

    public HomestayResponse updateHomestay(HomestayRequest request) {
        Homestay homestay = homestayRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        homestayMapper.updateToHomestay(homestay, request);

        Set<TypeHomestay> typeHomestays = new HashSet<>();
        request.getTypeHomestays().forEach(typeHomestay -> {
            TypeHomestay typeHomestay1 = typeHomestayRepository.findByName(typeHomestay)
                    .orElseThrow(() -> new BusinessException(ErrorCode.TYPE_HOMESTAY_NOT_FOUND));
            typeHomestays.add(typeHomestay1);
        });
        homestay.setTypeHomestays(typeHomestays);

        homestay.setDistrict(districtRepository.findByName(request.getDistrictName())
                .orElseThrow(() -> new BusinessException(ErrorCode.DISTRICT_NOT_FOUND)));

        homestay.setUser(userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND)));

        homestayRepository.save(homestay);

        HomestayResponse homestayResponse = homestayMapper.toHomestayResponse(homestay);
        return toHomeStayResponseWithRelationship(homestay, homestayResponse);
    }

    public String deleteHomestay(String id) {
        Homestay homestay = homestayRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));
        homestay.setStatus(HomestayStatus.DELETED.name());
        homestayRepository.save(homestay);
        return "Delete homestay successfully";
    }

    @Transactional
    public HomestayResponse updateHomestayPhoto(List<MultipartFile> photos, String id) {
        Homestay homestay = homestayRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));

        // Xóa các ảnh cũ trên Cloudinary
        List<Image> existingImages = homestay.getImages();
        cloudinaryService.deleteFiles(existingImages.stream().map(Image::getUrl).collect(Collectors.toList()));
        existingImages.clear();

        // Upload các ảnh mới và lưu URL vào cơ sở dữ liệu
        List<String> photoUrls = cloudinaryService.uploadFiles(photos);
        for (String photoUrl : photoUrls) {
            Image image = Image.builder()
                    .url(photoUrl)
                    .homestay(homestay)
                    .build();
            existingImages.add(image);
        }

        homestay.setImages(existingImages);
        homestayRepository.save(homestay);

        HomestayResponse homestayResponse = homestayMapper.toHomestayResponse(homestay);
        return toHomeStayResponseWithRelationship(homestay, homestayResponse);
    }

}