package com.homestay.service;

import com.homestay.dto.request.HomestayRequest;
import com.homestay.dto.response.HomestayResponse;
import com.homestay.exception.BusinessException;
import com.homestay.exception.ErrorCode;
import com.homestay.model.Homestay;
import com.homestay.model.User;
import com.homestay.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class HomestayService {
    @Autowired
    HomestayRepository homestayRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DiscountRepository discountRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    public HomestayResponse createHomestay(@Valid HomestayRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Homestay homestay = Homestay.builder()
                .name(request.getName())
                .email(request.getEmail())
                .standardCheckIn(request.getStandardCheckIn())
                .standardCheckOut(request.getStandardCheckOut())
                .price(request.getPrice())
                .phone(request.getPhone())
                .status("ACTIVE")
                .user(user)
                .description(request.getDescription())
                .type(request.getType())
                .address(request.getAddress())
                .longitude(request.getLongitude())
                .latitude(request.getLatitude())
                .addressDetail(request.getAddressDetail())
                .guests(request.getGuests())
                .bedrooms(request.getBedrooms())
                .bathrooms(request.getBathrooms())
                .build();

        Homestay savedHomestay = homestayRepository.save(homestay);

        return HomestayResponse.builder()
                .id(savedHomestay.getId())
                .name(savedHomestay.getName())
                .email(savedHomestay.getEmail())
                .standardCheckIn(savedHomestay.getStandardCheckIn())
                .standardCheckOut(savedHomestay.getStandardCheckOut())
                .price(savedHomestay.getPrice())
                .phone(savedHomestay.getPhone())
                .description(savedHomestay.getDescription())
                .type(savedHomestay.getType())
                .status(savedHomestay.getStatus())
                .user(savedHomestay.getUser().getUsername())
                .discounts(null)
                .address(savedHomestay.getAddress())
                .longitude(savedHomestay.getLongitude())
                .latitude(savedHomestay.getLatitude())
                .addressDetail(savedHomestay.getAddressDetail())
                .guests(savedHomestay.getGuests())
                .bedrooms(savedHomestay.getBedrooms())
                .bathrooms(savedHomestay.getBathrooms())
                .rooms(null)
                .reviews(null)
                .build();
    }

    public List<HomestayResponse> getAllHomestays() {
        return homestayRepository.findAll().stream()
                .map(savedHomestay -> HomestayResponse.builder()
                        .id(savedHomestay.getId())
                        .name(savedHomestay.getName())
                        .email(savedHomestay.getEmail())
                        .standardCheckIn(savedHomestay.getStandardCheckIn())
                        .standardCheckOut(savedHomestay.getStandardCheckOut())
                        .price(savedHomestay.getPrice())
                        .phone(savedHomestay.getPhone())
                        .description(savedHomestay.getDescription())
                        .type(savedHomestay.getType())
                        .status(savedHomestay.getStatus())
                        .user(savedHomestay.getUser().getUsername())
                        .discounts(savedHomestay.getDiscounts().stream().map(discount -> discount.getId()).collect(Collectors.toSet()))
                        .address(savedHomestay.getAddress())
                        .longitude(savedHomestay.getLongitude())
                        .latitude(savedHomestay.getLatitude())
                        .addressDetail(savedHomestay.getAddressDetail())
                        .guests(savedHomestay.getGuests())
                        .bedrooms(savedHomestay.getBedrooms())
                        .bathrooms(savedHomestay.getBathrooms())
                        .rooms(savedHomestay.getRooms().stream().map(room -> room.getId()).collect(Collectors.toSet()))
                        .reviews(savedHomestay.getReviews().stream().map(review -> review.getId()).collect(Collectors.toSet()))
                        .build()
                ).collect(Collectors.toList());
    }

    public HomestayResponse getHomestayById(String id) {
        Homestay savedHomestay = homestayRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));

        return HomestayResponse.builder()
                .id(savedHomestay.getId())
                .name(savedHomestay.getName())
                .email(savedHomestay.getEmail())
                .standardCheckIn(savedHomestay.getStandardCheckIn())
                .standardCheckOut(savedHomestay.getStandardCheckOut())
                .price(savedHomestay.getPrice())
                .phone(savedHomestay.getPhone())
                .description(savedHomestay.getDescription())
                .type(savedHomestay.getType())
                .status(savedHomestay.getStatus())
                .user(savedHomestay.getUser().getUsername())
                .discounts(savedHomestay.getDiscounts().stream().map(discount -> discount.getId()).collect(Collectors.toSet()))
                .address(savedHomestay.getAddress())
                .longitude(savedHomestay.getLongitude())
                .latitude(savedHomestay.getLatitude())
                .addressDetail(savedHomestay.getAddressDetail())
                .guests(savedHomestay.getGuests())
                .bedrooms(savedHomestay.getBedrooms())
                .bathrooms(savedHomestay.getBathrooms())
                .rooms(savedHomestay.getRooms().stream().map(room -> room.getId()).collect(Collectors.toSet()))
                .reviews(savedHomestay.getReviews().stream().map(review -> review.getId()).collect(Collectors.toSet()))
                .build();
    }

    public HomestayResponse updateHomestay(String id, @Valid HomestayRequest request) {
        Homestay savedHomestay = homestayRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        savedHomestay.setName(request.getName());
        savedHomestay.setEmail(request.getEmail());
        savedHomestay.setStandardCheckIn(request.getStandardCheckIn());
        savedHomestay.setStandardCheckOut(request.getStandardCheckOut());
        savedHomestay.setPrice(request.getPrice());
        savedHomestay.setPhone(request.getPhone());
        savedHomestay.setDescription(request.getDescription());
        savedHomestay.setType(request.getType());
        savedHomestay.setAddress(request.getAddress());
        savedHomestay.setLongitude(request.getLongitude());
        savedHomestay.setLatitude(request.getLatitude());
        savedHomestay.setAddressDetail(request.getAddressDetail());
        savedHomestay.setGuests(request.getGuests());
        savedHomestay.setBedrooms(request.getBedrooms());
        savedHomestay.setBathrooms(request.getBathrooms());
        savedHomestay.setStatus(request.getStatus());
        savedHomestay.setUser(user);


        if (request.getDiscounts() != null) {
            savedHomestay.setDiscounts(request.getDiscounts().stream()
                    .map(discountId -> discountRepository.findById(discountId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.DISCOUNT_NOT_FOUND)))
                    .collect(Collectors.toSet()));
        }
        if (request.getRooms() != null) {
            savedHomestay.setRooms(request.getRooms().stream()
                    .map(roomId -> roomRepository.findById(roomId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND)))
                    .collect(Collectors.toSet()));
        }
        if (request.getReviews() != null) {
            savedHomestay.setReviews(request.getReviews().stream()
                    .map(reviewId -> reviewRepository.findById(reviewId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND)))
                    .collect(Collectors.toSet()));
        }

        Homestay updatedHomestay = homestayRepository.save(savedHomestay);

        return HomestayResponse.builder()
                .id(updatedHomestay.getId())
                .name(updatedHomestay.getName())
                .email(updatedHomestay.getEmail())
                .standardCheckIn(updatedHomestay.getStandardCheckIn())
                .standardCheckOut(updatedHomestay.getStandardCheckOut())
                .price(updatedHomestay.getPrice())
                .phone(updatedHomestay.getPhone())
                .description(updatedHomestay.getDescription())
                .type(updatedHomestay.getType())
                .status(updatedHomestay.getStatus())
                .user(updatedHomestay.getUser().getUsername())
                .discounts(updatedHomestay.getDiscounts().stream().map(discount -> discount.getId()).collect(Collectors.toSet()))
                .address(updatedHomestay.getAddress())
                .longitude(updatedHomestay.getLongitude())
                .latitude(updatedHomestay.getLatitude())
                .addressDetail(updatedHomestay.getAddressDetail())
                .guests(updatedHomestay.getGuests())
                .bedrooms(updatedHomestay.getBedrooms())
                .bathrooms(updatedHomestay.getBathrooms())
                .rooms(updatedHomestay.getRooms().stream().map(room -> room.getId()).collect(Collectors.toSet()))
                .reviews(updatedHomestay.getReviews().stream().map(review -> review.getId()).collect(Collectors.toSet()))
                .build();
    }

    public String deleteHomestay(String id) {
        Homestay savedHomestay = homestayRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));

        homestayRepository.delete(savedHomestay);

        return "Homestay deleted successfully";
    }
}