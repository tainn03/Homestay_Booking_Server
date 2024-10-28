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
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "discounts", ignore = true)
    @Mapping(target = "typeHomestays", ignore = true)
    @Mapping(target = "district", ignore = true)
    Homestay toHomestay(HomestayRequest homestay);

    @Mapping(target = "isFavorite", ignore = true)
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "urlImages", ignore = true)
    @Mapping(target = "discounts", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "reviewIds", ignore = true)
    @Mapping(target = "typeHomestayNames", ignore = true)
    @Mapping(target = "districtName", source = "district.name")
    @Mapping(target = "cityName", source = "district.city.name")
    HomestayResponse toHomestayResponse(Homestay homestay);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "discounts", ignore = true)
    @Mapping(target = "typeHomestays", ignore = true)
    @Mapping(target = "district", ignore = true)
    @Mapping(target = "user", ignore = true)
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
