package com.homestay.mapper;

import com.homestay.dto.request.RoomRequest;
import com.homestay.dto.response.RoomResponse;
import com.homestay.model.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    @Mapping(target = "amenities", expression = "java(room.getAmenities().stream().map(amenity -> com.homestay.dto.response.AmenityResponse.builder().name(amenity.getName()).type(amenity.getType()).build()).collect(java.util.stream.Collectors.toSet()))")
    @Mapping(target = "discounts", expression = "java(room.getDiscounts().stream().map(discount -> com.homestay.dto.response.DiscountResponse.builder().id(discount.getId()).value(discount.getValue()).type(discount.getType()).description(discount.getDescription()).startDate(String.valueOf(discount.getStartDate())).endDate(String.valueOf(discount.getEndDate())).build()).collect(java.util.stream.Collectors.toSet()))")
    @Mapping(target = "images", expression = "java(room.getImages().stream().map(com.homestay.model.Image::getUrl).collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "bookings", expression = "java(room.getBookings().stream().map(com.homestay.model.Booking::getId).collect(java.util.stream.Collectors.toSet()))")
    RoomResponse toRoomResponse(Room room);

    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "discounts", ignore = true)
    @Mapping(target = "images", ignore = true)
    Room toRoom(@MappingTarget Room room, RoomRequest request);
}
