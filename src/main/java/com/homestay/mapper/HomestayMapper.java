package com.homestay.mapper;

import com.homestay.dto.request.HomestayRequest;
import com.homestay.dto.response.HomestayResponse;
import com.homestay.model.Homestay;
import com.homestay.model.Image;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface HomestayMapper {
    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "district", ignore = true)
    @Mapping(target = "typeHomestay", ignore = true)
    @Mapping(target = "nameHomestay", source = "name")
    Homestay toHomestay(HomestayRequest homestay);

    @Mapping(target = "isFavorite", ignore = true)
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "urlImages", expression = "java(homestay.getImages() != null ? homestay.getImages().stream().map(com.homestay.model.Image::getUrl).collect(java.util.stream.Collectors.toList()) : new java.util.ArrayList<>())")
    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "typeHomestayName", source = "typeHomestay.name")
    @Mapping(target = "districtName", source = "district.name")
    @Mapping(target = "cityName", source = "district.city.name")
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "name", source = "nameHomestay")
    @Mapping(target = "status", source = "status")
    HomestayResponse toHomestayResponse(Homestay homestay);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "district", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "discounts", ignore = true)
    @Mapping(target = "typeHomestay", ignore = true)
    @Mapping(target = "nameHomestay", source = "name")
    void updateToHomestay(@MappingTarget Homestay homestay, HomestayRequest request);

    default List<Image> map(List<String> imageUrls) {
        if (imageUrls == null) {
            return null;
        }
        return imageUrls.stream()
                .map(url -> Image.builder().url(url).build())
                .collect(Collectors.toList());
    }
}
