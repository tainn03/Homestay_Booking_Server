package com.homestay.service;

import com.homestay.constants.DiscountType;
import com.homestay.constants.HomestayStatus;
import com.homestay.constants.RoomStatus;
import com.homestay.dto.request.ChangeDiscountValueRequest;
import com.homestay.dto.request.CustomPriceRequest;
import com.homestay.dto.request.DiscountRequest;
import com.homestay.dto.request.HomestayRequest;
import com.homestay.dto.response.AmenityResponse;
import com.homestay.dto.response.HomestayResponse;
import com.homestay.dto.response.RoomResponse;
import com.homestay.dto.response.UserResponse;
import com.homestay.exception.BusinessException;
import com.homestay.exception.ErrorCode;
import com.homestay.mapper.DiscountMapper;
import com.homestay.mapper.HomestayMapper;
import com.homestay.model.*;
import com.homestay.repository.*;
import com.homestay.service.external.CloudinaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class HomestayService {
    HomestayMapper homestayMapper;
    DiscountMapper discountMapper;
    CloudinaryService cloudinaryService;
    HomestayRepository homestayRepository;
    UserRepository userRepository;
    TypeHomestayRepository typeHomestayRepository;
    DistrictRepository districtRepository;
    DiscountRepository discountRepository;
    CityRepository cityRepository;
    ImageRepository imageRepository;
    AmenityRepository amenityRepository;
    PriceCalendarRepository priceCalendarRepository;

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
                    .homestay(homestay)
                    .build();
            rooms.add(room);
        });
        homestay.setRooms(rooms);

        Set<TypeHomestay> typeHomestays = new HashSet<>();
        request.getTypeHomestays().forEach(typeHomestay -> {
            TypeHomestay typeHomestay1 = typeHomestayRepository.findByName(typeHomestay)
                    .orElseThrow(() -> new BusinessException(ErrorCode.TYPE_HOMESTAY_NOT_FOUND));
            typeHomestays.add(typeHomestay1);
        });
        homestay.setTypeHomestays(typeHomestays);

        Set<Discount> discounts = new HashSet<>();
        request.getDiscounts().forEach(discountRequest -> {
            Discount discount = Discount.builder()
                    .value(discountRequest.getValue())
                    .description(discountRequest.getDescription())
                    .type(discountRequest.getType())
                    .homestay(homestay)
                    .build();
            discounts.add(discount);
        });
        homestay.setDiscounts(discounts);

        Set<Amenity> amenities = new HashSet<>();
        request.getAmenities().forEach(amenityRequest -> {
            Amenity amenity = amenityRepository.findById(amenityRequest.getName())
                    .orElseGet(() -> {
                        Amenity newAmenity = Amenity.builder()
                                .name(amenityRequest.getName())
                                .type(amenityRequest.getType())
                                .build();
                        return amenityRepository.save(newAmenity);
                    });
            amenities.add(amenity);
        });
        homestay.setAmenities(amenities);

        homestay.setDistrict(districtRepository.findByNameAndCityName(request.getDistrictName(), request.getCityName())
                .orElseThrow(() -> new BusinessException(ErrorCode.DISTRICT_NOT_FOUND)));

        homestay.setUser(userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND)));

        homestayRepository.save(homestay);

        HomestayResponse homestayResponse = homestayMapper.toHomestayResponse(homestay);
        return toHomeStayResponseWithRelationship(homestay, homestayResponse);
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
        if (homestay.getDiscounts() != null) {
            homestayResponse.setDiscounts(homestay.getDiscounts().stream().map(discountMapper::toDiscountResponse).collect(Collectors.toSet()));
        }
        if (homestay.getRooms() != null && homestay.getRooms().getFirst().getImages() != null) {
            homestayResponse.setRooms(homestay.getRooms().stream()
                    .filter(room -> !Objects.equals(room.getStatus(), RoomStatus.DELETED.name()))
                    .map(room -> RoomResponse.builder()
                            .id(room.getId())
                            .name(room.getName())
                            .size(room.getSize())
                            .images(room.getImages().stream().map(Image::getUrl).collect(toList()))
                            .bookings(room.getBookings().stream().map(Booking::getId).collect(Collectors.toSet()))
                            .build())
                    .collect(toList()));
        }
        if (homestay.getReviews() != null) {
            homestayResponse.setReviewIds(homestay.getReviews().stream().map(Review::getId).collect(Collectors.toSet()));
        }
        if (homestay.getTypeHomestays() != null) {
            homestayResponse.setTypeHomestayNames(homestay.getTypeHomestays().stream().map(TypeHomestay::getName).collect(Collectors.toSet()));
        }
        if (homestay.getWeekendPrice() != 0.0) {
            homestayResponse.setWeekendPrice(homestay.getWeekendPrice());
        }
        if (homestay.getPriceCalendars() != null) {
            homestayResponse.setPriceCalendars(homestay.getPriceCalendars());
        }
        if (homestay.getAmenities() != null) {
            homestayResponse.setAmenities(homestay.getAmenities().stream().map(amenity -> AmenityResponse.builder()
                    .name(amenity.getName())
                    .type(amenity.getType())
                    .build()).collect(Collectors.toSet()));
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
                .collect(toList());
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
                .collect(toList());
        return responses;
    }

    @Transactional
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

        if (Objects.equals(request.getStatus(), HomestayStatus.ACTIVE.name())
                || Objects.equals(request.getStatus(), HomestayStatus.INACTIVE.name())) {
            homestay.setStatus(request.getStatus());
        }

        homestay.setDistrict(districtRepository.findByNameAndCityName(request.getDistrictName(), request.getCityName())
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
                    .stream().sorted(Comparator.comparing(Homestay::getCreatedAt).reversed()).toList();
        } else {
            for (TypeHomestay typeHomestay : typeHomestays) {
                homestays.addAll(homestayRepository.findByDistrictIdInOrTypeHomestay(
                        districts.stream().map(District::getId).collect(toList()),
                        typeHomestay
                ).stream().sorted(Comparator.comparing(Homestay::getCreatedAt).reversed()).toList());
            }
        }

        System.out.println("districts: " + districts.stream().map(District::getName).toList());
        System.out.println("cities: " + cities.stream().map(City::getName).toList());
        System.out.println("typeHomestays: " + typeHomestays.stream().map(TypeHomestay::getName).toList());

        List<HomestayResponse> responses = new ArrayList<>(homestays.stream().distinct()
                .map(homestay -> {
                    HomestayResponse response = homestayMapper.toHomestayResponse(homestay);
                    return toHomeStayResponseWithRelationship(homestay, response);
                })
                .toList());
        if (filter != null) {
            responses.sort((o1, o2) -> {
                if (filter.equals("price")) {
                    return Double.compare(o1.getPrice(), o2.getPrice());
                } else if (filter.equals("rating")) {
                    return Double.compare(o1.getRating(), o2.getRating());
                } else {
                    return 0;
                }
            });
        }
        return responses;
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

    public HomestayResponse updateHomestayPrice(double price, String id) {
        Homestay homestay = homestayRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));
        homestay.setPrice(price);
        homestayRepository.save(homestay);
        HomestayResponse response = homestayMapper.toHomestayResponse(homestay);
        return toHomeStayResponseWithRelationship(homestay, response);
    }

    public HomestayResponse updateHomestayWeekendPrice(double weekendPrice, String id) {
        Homestay homestay = homestayRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));
        homestay.setWeekendPrice(weekendPrice);
        homestayRepository.save(homestay);
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

    public HomestayResponse updateHomestayPriceCalendar(List<CustomPriceRequest> requests, String id) {
        Homestay homestay = homestayRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));

        if (requests == null || requests.isEmpty()) {
            return null;
        }

        priceCalendarRepository.deleteAllByHomestay(homestay);
        homestay.getPriceCalendars().clear();

        Set<PriceCalendar> priceCalendars = new HashSet<>();
        for (CustomPriceRequest request : requests) {
            PriceCalendar priceCalendar = PriceCalendar.builder()
                    .date(request.getDate())
                    .price(request.getPrice())
                    .homestay(homestay)
                    .build();
            priceCalendars.add(priceCalendar);
        }

        homestay.setPriceCalendars(priceCalendars);
        homestayRepository.save(homestay);

        HomestayResponse homestayResponse = homestayMapper.toHomestayResponse(homestay);
        return toHomeStayResponseWithRelationship(homestay, homestayResponse);
    }

    public Discount addHomestayDiscountCustom(DiscountRequest request, String id) {
        Homestay homestay = homestayRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));

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
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole().getRoleName())
                .urlAvatar(user.getAvatar() != null ? user.getAvatar().getUrl() : null)
                .build();
    }
}