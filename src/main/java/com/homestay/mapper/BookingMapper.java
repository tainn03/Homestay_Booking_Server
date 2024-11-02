package com.homestay.mapper;

import com.homestay.dto.response.BookingResponse;
import com.homestay.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(target = "user", source = "user.email")
    @Mapping(target = "nights", expression = "java((int) java.time.temporal.ChronoUnit.DAYS.between(booking.getCheckIn(), booking.getCheckOut()))")
    @Mapping(target = "numOfWeekend", expression = "java((int) booking.getCheckIn().datesUntil(booking.getCheckOut()).filter(date -> date.getDayOfWeek() == java.time.DayOfWeek.SATURDAY || date.getDayOfWeek() == java.time.DayOfWeek.SUNDAY).count())")
    @Mapping(target = "numOfWeekday", expression = "java((int) booking.getCheckIn().datesUntil(booking.getCheckOut()).filter(date -> date.getDayOfWeek() != java.time.DayOfWeek.SATURDAY && date.getDayOfWeek() != java.time.DayOfWeek.SUNDAY).count())")
    @Mapping(target = "discounts", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    BookingResponse toBookingResponse(Booking booking);
}
