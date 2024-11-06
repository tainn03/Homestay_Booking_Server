package com.homestay.mapper;

import com.homestay.dto.response.BookingResponse;
import com.homestay.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "user", source = "user.email")
    @Mapping(target = "nights", expression = "java((int) java.time.temporal.ChronoUnit.DAYS.between(booking.getCheckIn(), booking.getCheckOut()))")
    @Mapping(target = "numOfWeekend", expression = "java((int) booking.getCheckIn().datesUntil(booking.getCheckOut()).filter(date -> date.getDayOfWeek() == java.time.DayOfWeek.SATURDAY || date.getDayOfWeek() == java.time.DayOfWeek.SUNDAY).count())")
    @Mapping(target = "numOfWeekday", expression = "java((int) booking.getCheckIn().datesUntil(booking.getCheckOut()).filter(date -> date.getDayOfWeek() != java.time.DayOfWeek.SATURDAY && date.getDayOfWeek() != java.time.DayOfWeek.SUNDAY).count())")
    @Mapping(target = "discounts", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "payment", expression = "java(booking.getPayment() != null ? booking.getPayment() : null)")
    @Mapping(target = "price", expression = "java(booking.getRooms().stream().mapToDouble(com.homestay.model.Room::getPrice).sum())")
    @Mapping(target = "weekendPrice", expression = "java(booking.getRooms().stream().mapToDouble(com.homestay.model.Room::getWeekendPrice).sum())")
    BookingResponse toBookingResponse(Booking booking);
}
