package com.homestay.mapper;

import com.homestay.dto.response.DiscountResponse;
import com.homestay.model.Discount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DiscountMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "startDate", source = "startDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "endDate", source = "endDate", dateFormat = "yyyy-MM-dd")
    DiscountResponse toDiscountResponse(Discount discount);
}
