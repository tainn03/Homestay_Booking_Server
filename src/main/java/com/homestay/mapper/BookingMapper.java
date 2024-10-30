package com.homestay.mapper;

import com.homestay.dto.response.BookingResponse;
import com.homestay.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(target = "user", source = "user.email")
    @Mapping(target = "nights", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "id", source = "id")
    BookingResponse toBookingResponse(Booking booking);
}
