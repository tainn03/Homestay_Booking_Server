package com.homestay.mapper;

import com.homestay.dto.request.HomestayRequest;
import com.homestay.dto.response.HomestayResponse;
import com.homestay.model.Homestay;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HomestayMapper {
    @Mapping(target = "typeHomestays", ignore = true)
    @Mapping(target = "district", ignore = true)
    Homestay toHomestay(HomestayRequest homestay);

    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "urlImages", ignore = true)
    @Mapping(target = "discountIds", ignore = true)
    @Mapping(target = "roomNames", ignore = true)
    @Mapping(target = "reviewIds", ignore = true)
    @Mapping(target = "typeHomestayNames", ignore = true)
    @Mapping(target = "districtName", ignore = true)
    HomestayResponse toHomestayResponse(Homestay homestay);

    @Mapping(target = "typeHomestays", ignore = true)
    @Mapping(target = "district", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateToHomestay(@MappingTarget Homestay homestay, HomestayRequest request);
}
