package com.homestay.mapper;

import com.homestay.dto.response.DiscountResponse;
import com.homestay.model.Discount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DiscountMapper {
    DiscountResponse toDiscountResponse(Discount discount);
}
