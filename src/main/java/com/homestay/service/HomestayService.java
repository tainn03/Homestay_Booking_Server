package com.homestay.service;

import com.homestay.constants.DiscountType;
import com.homestay.constants.HomestayStatus;
import com.homestay.constants.RoomStatus;
import com.homestay.dto.request.ChangeDiscountValueRequest;
import com.homestay.dto.request.DiscountRequest;
import com.homestay.dto.request.HomestayRequest;
import com.homestay.dto.response.*;
import com.homestay.exception.BusinessException;
import com.homestay.exception.ErrorCode;
import com.homestay.mapper.DiscountMapper;
import com.homestay.mapper.HomestayMapper;
import com.homestay.mapper.UserMapper;
import com.homestay.model.*;
import com.homestay.repository.*;
import com.homestay.service.external.CloudinaryService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class HomestayService {
    HomestayMapper homestayMapper;
    DiscountMapper discountMapper;
    UserMapper userMapper;

    HomestayRepository homestayRepository;
    TypeHomestayRepository typeHomestayRepository;
    DistrictRepository districtRepository;
    UserRepository userRepository;
    AmenityRepository amenityRepository;
    ImageRepository imageRepository;
    DiscountRepository discountRepository;
    CityRepository cityRepository;

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

        List<Room> rooms = new ArrayList<>();
        request.getRooms().forEach(roomRequest -> {
            Room room = Room.builder()
                    .name(roomRequest.getName())
                    .size(roomRequest.getSize())
                    .status(RoomStatus.ACTIVE.name())
                    .price(roomRequest.getPrice())
                    .weekendPrice(roomRequest.getWeekendPrice())
                    .homestay(homestay)
                    .build();
            Set<Amenity> amenities = new HashSet<>();
            if (roomRequest.getAmenities() != null) {
                roomRequest.getAmenities().forEach(amenityRequest -> {
                    Amenity amenity = amenityRepository.findById(amenityRequest.getName())
                            .orElse(Amenity.builder()
                                    .name(amenityRequest.getName())
                                    .type(amenityRequest.getType())
                                    .build());
                    amenities.add(amenity);
                });
            }
            Set<Discount> discounts = new HashSet<>();
            if (roomRequest.getDiscounts() != null) {
                roomRequest.getDiscounts().forEach(discountRequest -> {
                    Discount discount = Discount.builder()
                            .value(discountRequest.getValue())
                            .description(discountRequest.getDescription())
                            .type(discountRequest.getType())
                            .startDate(discountRequest.getStartDate())
                            .endDate(discountRequest.getEndDate())
                            .room(room)
                            .build();
                    discounts.add(discount);
                });
            }
            room.setDiscounts(discounts);
            room.setAmenities(amenities);
            rooms.add(room);
        });
        homestay.setRooms(rooms);

        homestay.setTypeHomestay(typeHomestayRepository.findByName(request.getTypeHomestay())
                .orElseThrow(() -> new BusinessException(ErrorCode.TYPE_HOMESTAY_NOT_FOUND)));
        homestay.setDistrict(districtRepository.findByNameAndCityName(request.getDistrictName(), request.getCityName())
                .orElseThrow(() -> new BusinessException(ErrorCode.DISTRICT_NOT_FOUND)));
        homestay.setUser(userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND)));

        Homestay savedHomestay = homestayRepository.save(homestay);

        HomestayResponse homestayResponse = homestayMapper.toHomestayResponse(savedHomestay);
        return toHomeStayResponseWithRelationship(savedHomestay, homestayResponse);
    }

    @Transactional
    public HomestayResponse updateHomestayImages(List<MultipartFile> images, String id) {
        Homestay homestay = homestayRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));

        if (images != null && !images.isEmpty()) {
            List<Image> existingImages = homestay.getImages();
            cloudinaryService.deleteFiles(existingImages.stream().map(Image::getUrl).collect(toList()));
            existingImages.clear();
            homestay.getImages().clear();
            imageRepository.deleteAllByHomestay(homestay);

            List<String> photoUrls = cloudinaryService.uploadFiles(images);
            for (String photoUrl : photoUrls) {
                Image image = Image.builder()
                        .url(photoUrl)
                        .homestay(homestay)
                        .build();
                existingImages.add(image);
            }

            homestay.setImages(existingImages);
            homestayRepository.save(homestay);
        } else {
            throw new BusinessException(ErrorCode.NO_IMAGES_PROVIDED);
        }

        HomestayResponse homestayResponse = homestayMapper.toHomestayResponse(homestay);
        return toHomeStayResponseWithRelationship(homestay, homestayResponse);
    }

    private HomestayResponse toHomeStayResponseWithRelationship(Homestay homestay, HomestayResponse homestayResponse) {
        User user = SecurityContextHolder.getContext().getAuthentication() == null ? null
                : userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElse(null);
        if (user != null) {
            homestayResponse.setFavorite(user.getFavoriteHomestays().contains(homestay));
        }
        if (homestay.getImages() != null) {
            homestayResponse.setUrlImages(homestay.getImages().stream().map(Image::getUrl).collect(toList()));
        }
        if (homestay.getRooms() != null && homestay.getRooms().getFirst().getImages() != null) {
            double price = homestay.getRooms().stream()
                    .filter(room -> !Objects.equals(room.getStatus(), RoomStatus.DELETED.name()))
                    .mapToDouble(Room::getPrice)
                    .min()
                    .orElse(0);
            homestayResponse.setPrice(price);
            homestayResponse.setRooms(homestay.getRooms().stream()
                    .filter(room -> !Objects.equals(room.getStatus(), RoomStatus.DELETED.name()))
                    .map(room -> RoomResponse.builder()
                            .id(room.getId())
                            .name(room.getName())
                            .size(room.getSize())
                            .price(room.getPrice())
                            .weekendPrice(room.getWeekendPrice())
                            .status(room.getStatus())
                            .images(room.getImages().stream().map(Image::getUrl).collect(toList()))
                            .bookings(room.getBookings().stream().map(Booking::getId).collect(toSet()))
                            .amenities(room.getAmenities().stream().map(amenity -> AmenityResponse.builder()
                                    .name(amenity.getName())
                                    .type(amenity.getType())
                                    .build()).collect(toSet()))
                            .discounts(room.getDiscounts().stream().map(discountMapper::toDiscountResponse).collect(toSet()))
                            .priceCalendars(room.getPriceCalendars())
                            .build())
                    .sorted(Comparator.comparing(RoomResponse::getName))
                    .collect(toList()));
        }
        if (homestay.getReviews() != null) {
            double rating = homestay.getReviews().stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0);
            homestayResponse.setRating(rating);
            homestayResponse.setReviews(homestay.getReviews().stream().map(review -> ReviewResponse.builder()
                            .id(review.getId())
                            .rating(review.getRating())
                            .comment(review.getComment())
                            .userName(review.getUser().getFullName())
                            .avatar(review.getUser().getAvatar().getUrl())
                            .date(review.getCreatedAt())
                            .homestayId(review.getHomestay().getId())
                            .build())
                    .sorted(Comparator.comparing(ReviewResponse::getDate).reversed())
                    .collect(toList()));
        }
        return homestayResponse;
    }

    public List<HomestayResponse> getAllHomestays() {
        List<Homestay> homestays = homestayRepository.findAll();
        return homestays.stream()
                .map(homestay -> {
                    HomestayResponse response = homestayMapper.toHomestayResponse(homestay);
                    return toHomeStayResponseWithRelationship(homestay, response);
                })
                .collect(toList());
    }

    public List<HomestayResponse> getHomestayByOwner() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        List<Homestay> homestays = homestayRepository.findByUserAndStatus(user, HomestayStatus.ACTIVE.name());
        return homestays.stream()
                .map(homestay -> {
                    HomestayResponse response = homestayMapper.toHomestayResponse(homestay);
                    return toHomeStayResponseWithRelationship(homestay, response);
                })
                .collect(toList());
    }

    @Transactional // Lỗi chưa cập nhật được homestay
    public HomestayResponse updateHomestay(HomestayRequest request) {
        Homestay homestay = homestayRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        homestayMapper.updateToHomestay(homestay, request);

        if (Objects.equals(request.getStatus(), HomestayStatus.ACTIVE.name())
                || Objects.equals(request.getStatus(), HomestayStatus.INACTIVE.name())) {
            homestay.setStatus(request.getStatus());
        }

        if (!Objects.equals(request.getDistrictName(), homestay.getDistrict().getName())) {
            District district = districtRepository.findByNameAndCityName(request.getDistrictName(), request.getCityName())
                    .orElseThrow(() -> new BusinessException(ErrorCode.DISTRICT_NOT_FOUND));
            homestay.setDistrict(district);
        }

        if (!Objects.equals(request.getTypeHomestay(), homestay.getTypeHomestay().getName())) {
            TypeHomestay typeHomestay = typeHomestayRepository.findByName(request.getTypeHomestay())
                    .orElseThrow(() -> new BusinessException(ErrorCode.TYPE_HOMESTAY_NOT_FOUND));
            homestay.setTypeHomestay(typeHomestay);
        }

        if (!Objects.equals(request.getEmail(), homestay.getEmail())) {
            if (homestayRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new BusinessException(ErrorCode.HOMESTAY_ALREADY_EXIST);
            }
            homestay.setEmail(request.getEmail());
        }

        // Ensure discounts are managed by the current session
        Set<Discount> managedDiscounts = new HashSet<>();
        for (Discount discount : homestay.getDiscounts()) {
            managedDiscounts.add(discountRepository.findById(discount.getId())
                    .orElseGet(() -> discountRepository.save(discount)));
        }
        homestay.setDiscounts(managedDiscounts);

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

        // Xóa các ảnh cũ trên Cloudinary và trong cơ sở dữ liệu
        List<Image> existingImages = homestay.getImages();
        cloudinaryService.deleteFiles(existingImages.stream().map(Image::getUrl).collect(toList()));
        existingImages.clear();
        homestay.getImages().clear();
        imageRepository.deleteAllByHomestay(homestay);

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

    public List<HomestayResponse> searchHomestays(String query, String filter) {
        String[] keywords = Arrays.stream(query.split("[\\s,.]+"))
                .map(keyword -> keyword.substring(0, 1).toUpperCase() + keyword.substring(1).toLowerCase())
                .toArray(String[]::new);

        List<District> districts = new ArrayList<>();
        List<City> cities = new ArrayList<>();

        for (int i = 0; i < keywords.length - 1; i++) {
            if (keywords[i].equals("Quận") || keywords[i].equals("Huyện") || keywords[i].equals("Xã") || keywords[i].equals("Đảo")) {
                String districtName = keywords[i + 1] + (i + 2 < keywords.length ? " " + keywords[i + 2] : "");
                districtRepository.findByName(districtName).ifPresent(districts::add);
                if (i + 3 < keywords.length) {
                    i += 2;
                } else {
                    break;
                }
            }
            if (keywords[i].equals("Tỉnh") || keywords[i + 1].equals("Phố")) {
                String cityName = keywords[i + 1] + (i + 2 < keywords.length ? " " + keywords[i + 2] : "");
                cityRepository.findByName(cityName).ifPresent(cities::add);
                if (i + 3 < keywords.length) {
                    i += 2;
                } else {
                    break;
                }
            }
            if (i + 1 < keywords.length) {
                String potentialDistrictName = keywords[i] + " " + keywords[i + 1];
                districtRepository.findByName(potentialDistrictName).ifPresent(districts::add);
            }
            if (i + 1 < keywords.length) {
                String potentialCityName = keywords[i] + " " + keywords[i + 1];
                cityRepository.findByName(potentialCityName).ifPresent(cities::add);
            }
        }

        if (!districts.isEmpty() && !cities.isEmpty()) {
            List<City> finalCities = cities;
            districts = districts.stream()
                    .filter(district -> finalCities.contains(district.getCity()))
                    .collect(toList());
        }
        if (districts.isEmpty() && !cities.isEmpty()) {
            districts = districtRepository.findByCityIn(cities);
        }
        if (districts.isEmpty() && cities.isEmpty()) {
            districts = districtRepository.findAll().stream()
                    .filter(district -> containsKeywords(district.getName(), keywords))
                    .collect(toList());
            cities = cityRepository.findAll().stream()
                    .filter(city -> containsKeywords(city.getName(), keywords))
                    .collect(toList());
            List<City> finalCities = cities;
            districts = districts.stream()
                    .filter(district -> finalCities.contains(district.getCity()))
                    .collect(toList());
        }

        List<TypeHomestay> typeHomestays = typeHomestayRepository.findAll().stream()
                .filter(typeHomestay -> containsKeywords(typeHomestay.getName(), keywords))
                .toList();

        // Tìm kiếm homestay theo quận, thành phố và loại homestay
        List<Homestay> homestays = new ArrayList<>();
        if (typeHomestays.isEmpty()) {
            homestays = homestayRepository.findByDistrictIn(districts)
                    .stream().toList();
        } else {
            for (TypeHomestay typeHomestay : typeHomestays) {
                homestays.addAll(homestayRepository.findByDistrictIdInOrTypeHomestay(
                        districts.stream().map(District::getId).collect(toList()),
                        typeHomestay
                ).stream().toList());
            }
        }

        System.out.println("districts: " + districts.stream().map(District::getName).toList());
        System.out.println("cities: " + cities.stream().map(City::getName).toList());
        System.out.println("typeHomestays: " + typeHomestays.stream().map(TypeHomestay::getName).toList());

        return new ArrayList<>(homestays.stream().distinct()
                .map(homestay -> {
                    HomestayResponse response = homestayMapper.toHomestayResponse(homestay);
                    return toHomeStayResponseWithRelationship(homestay, response);
                })
                .sorted(Comparator.comparing(HomestayResponse::getCreatedAt).reversed())
                .toList());
    }

    private boolean containsKeywords(String name, String[] keywords) {
        for (String keyword : keywords) {
            if (name.toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public HomestayResponse getHomestayById(String id) {
        Homestay homestay = homestayRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));
        HomestayResponse response = homestayMapper.toHomestayResponse(homestay);
        return toHomeStayResponseWithRelationship(homestay, response);
    }

    public HomestayResponse updateHomestaySystemDiscount(ChangeDiscountValueRequest request, String id) {
        Homestay homestay = homestayRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));

        Discount discount = homestay.getDiscounts().stream()
                .filter(d -> d.getType().equals(request.getType()))
                .findFirst()
                .orElseGet(() -> Discount.builder()
                        .type(request.getType())
                        .homestay(homestay)
                        .build());

        discount.setValue(request.getValue());
        homestay.getDiscounts().add(discount);
        homestayRepository.save(homestay);

        HomestayResponse response = homestayMapper.toHomestayResponse(homestay);
        return toHomeStayResponseWithRelationship(homestay, response);
    }

    public Discount addHomestayDiscountCustom(DiscountRequest request, String id) {
        Homestay homestay = homestayRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));
        boolean isDiscountExist = homestay.getDiscounts().stream()
                .anyMatch(discount ->
                        Objects.equals(discount.getValue(), request.getValue()) &&
                                Objects.equals(discount.getStartDate(), request.getStartDate()) &&
                                Objects.equals(discount.getEndDate(), request.getEndDate())
                );
        if (isDiscountExist) {
            throw new BusinessException(ErrorCode.DISCOUNT_ALREADY_EXIST);
        }

        Discount discount = Discount.builder()
                .value(request.getValue())
                .description(request.getDescription())
                .type(DiscountType.CUSTOM.name())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .homestay(homestay)
                .build();

        homestay.getDiscounts().add(discount);
        homestayRepository.save(homestay);

        return discountRepository.save(discount);
    }

    public Discount updateHomestayDiscountCustom(DiscountRequest request, String id) {
        Homestay homestay = homestayRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));
        Discount discount = homestay.getDiscounts().stream()
                .filter(d -> d.getId().equals(request.getId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.DISCOUNT_NOT_FOUND));

        discount.setValue(request.getValue());
        discount.setDescription(request.getDescription());
        discount.setStartDate(request.getStartDate());
        discount.setEndDate(request.getEndDate());
        homestay.getDiscounts().add(discount);

        homestayRepository.save(homestay);
        return discount;
    }

    public String deleteHomestayDiscountCustom(String id, String discountId) {
        Homestay homestay = homestayRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));
        Discount discount = homestay.getDiscounts().stream()
                .filter(d -> d.getId().equals(discountId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.DISCOUNT_NOT_FOUND));
        homestay.getDiscounts().remove(discount);
        discountRepository.delete(discount);
        homestayRepository.save(homestay);
        return "Delete discount successfully";
    }

    public HomestayResponse addNewHomestayImages(List<MultipartFile> images, String id) {
        Homestay homestay = homestayRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));

        List<Image> existingImages = homestay.getImages();
        List<String> photoUrls = cloudinaryService.uploadFiles(images);
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

    @Transactional
    public HomestayResponse deleteHomestayImages(List<String> images, String id) {
        Homestay homestay = homestayRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));

        List<Image> existingImages = homestay.getImages();
        if (existingImages == null) {
            existingImages = new ArrayList<>();
        }

        cloudinaryService.deleteFiles(images);

        existingImages.removeIf(image -> images.contains(image.getUrl()));
        imageRepository.deleteByUrlIn(images);
        homestay.setImages(existingImages);
        homestayRepository.save(homestay);

        HomestayResponse homestayResponse = homestayMapper.toHomestayResponse(homestay);
        return toHomeStayResponseWithRelationship(homestay, homestayResponse);
    }

    public List<HomestayResponse> getFavoriteHomestay() {
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Set<Homestay> homestays = user.getFavoriteHomestays();
        return homestays.stream()
                .map(homestay -> {
                    HomestayResponse response = homestayMapper.toHomestayResponse(homestay);
                    return toHomeStayResponseWithRelationship(homestay, response);
                })
                .collect(toList());
    }

    public UserResponse getOwnerByHomestay(String id) {
        Homestay homestay = homestayRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));
        User user = homestay.getUser();

        UserResponse response = userMapper.toUserResponse(user);
        response.setHomestays(user.getHomestays().stream().map(
                homestay1 -> {
                    HomestayResponse homestayResponse1 = homestayMapper.toHomestayResponse(homestay1);
                    return toHomeStayResponseWithRelationship(homestay1, homestayResponse1);
                }).collect(toSet()));
        return response;
    }
}