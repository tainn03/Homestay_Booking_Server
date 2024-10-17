package com.homestay.service;

import com.homestay.constants.HomestayStatus;
import com.homestay.dto.request.ChangeDiscountValueRequest;
import com.homestay.dto.request.HomestayRequest;
import com.homestay.dto.response.HomestayResponse;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class HomestayService {
    HomestayRepository homestayRepository;
    UserRepository userRepository;
    HomestayMapper homestayMapper;
    DiscountMapper discountMapper;
    TypeHomestayRepository typeHomestayRepository;
    DistrictRepository districtRepository;
    CityRepository cityRepository;
    CloudinaryService cloudinaryService;
    ImageRepository imageRepository;
    AmenityRepository amenityRepository;

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
        request.getAmenityNames().forEach(amenityName -> {
            Amenity amenity = amenityRepository.findById(amenityName)
                    .orElseThrow(() -> new BusinessException(ErrorCode.AMENITY_NOT_FOUND));
            amenities.add(amenity);
        });
        homestay.setAmenities(amenities);

        homestay.setDistrict(districtRepository.findByNameAndCityName(request.getDistrictName(), request.getCity())
                .orElseThrow(() -> new BusinessException(ErrorCode.DISTRICT_NOT_FOUND)));

        homestay.setUser(userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND)));
        homestayRepository.save(homestay);

        HomestayResponse homestayResponse = homestayMapper.toHomestayResponse(homestay);
        return toHomeStayResponseWithRelationship(homestay, homestayResponse);
    }

    @Transactional
    public HomestayResponse updateHomestayImages(List<MultipartFile> images, String id) {
        System.out.println("images: " + images);
        Homestay homestay = homestayRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));

        if (images != null && !images.isEmpty()) {
            List<Image> existingImages = homestay.getImages();
            cloudinaryService.deleteFiles(existingImages.stream().map(Image::getUrl).collect(Collectors.toList()));
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
        if (homestay.getImages() != null) {
            homestayResponse.setUrlImages(homestay.getImages().stream().map(Image::getUrl).collect(Collectors.toSet()));
        }
        if (homestay.getDiscounts() != null) {
            homestayResponse.setDiscounts(homestay.getDiscounts().stream().map(discountMapper::toDiscountResponse).collect(Collectors.toSet()));
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
        if (homestay.getWeekendPrice() != 0.0) {
            homestayResponse.setWeekendPrice(homestay.getWeekendPrice());
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

        // Xóa các ảnh cũ trên Cloudinary và trong cơ sở dữ liệu
        List<Image> existingImages = homestay.getImages();
        cloudinaryService.deleteFiles(existingImages.stream().map(Image::getUrl).collect(Collectors.toList()));
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

    public List<HomestayResponse> searchHomestays(String query) {
        String[] keywords = Arrays.stream(query.split("\\s+"))
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
                    .collect(Collectors.toList());
        }
        if (districts.isEmpty() && !cities.isEmpty()) {
            districts = districtRepository.findByCityIn(cities);
        }
        if (districts.isEmpty() && cities.isEmpty()) {
            districts = districtRepository.findAll().stream()
                    .filter(district -> containsKeywords(district.getName(), keywords))
                    .collect(Collectors.toList());
            cities = cityRepository.findAll().stream()
                    .filter(city -> containsKeywords(city.getName(), keywords))
                    .collect(Collectors.toList());
            List<City> finalCities = cities;
            districts = districts.stream()
                    .filter(district -> finalCities.contains(district.getCity()))
                    .collect(Collectors.toList());
        }

        List<TypeHomestay> typeHomestays = typeHomestayRepository.findAll().stream()
                .filter(typeHomestay -> containsKeywords(typeHomestay.getName(), keywords))
                .toList();

        // Tìm kiếm homestay theo quận, thành phố và loại homestay
        List<Homestay> homestays = new ArrayList<>();
        if (typeHomestays.isEmpty()) {
            homestays = homestayRepository.findByDistrictIn(districts);
        } else {
            for (TypeHomestay typeHomestay : typeHomestays) {
                homestays.addAll(homestayRepository.findByDistrictIdInOrTypeHomestay(
                        districts.stream().map(District::getId).collect(Collectors.toList()),
                        typeHomestay
                ));
            }
        }

        System.out.println("districts: " + districts.stream().map(District::getName).toList());
        System.out.println("cities: " + cities.stream().map(City::getName).toList());
        System.out.println("typeHomestays: " + typeHomestays.stream().map(TypeHomestay::getName).toList());

        return homestays.stream().distinct()
                .map(homestay -> {
                    HomestayResponse response = homestayMapper.toHomestayResponse(homestay);
                    return toHomeStayResponseWithRelationship(homestay, response);
                })
                .collect(Collectors.toList());
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

    public HomestayResponse updateHomestayDiscount(ChangeDiscountValueRequest request, String id) {
        Homestay homestay = homestayRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));
        Discount discount = homestay.getDiscounts().stream()
                .filter(discount1 -> discount1.getId().equals(request.getDiscountId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.DISCOUNT_NOT_FOUND));
        discount.setValue(request.getValue());
        homestayRepository.save(homestay);
        HomestayResponse response = homestayMapper.toHomestayResponse(homestay);
        return toHomeStayResponseWithRelationship(homestay, response);
    }
}