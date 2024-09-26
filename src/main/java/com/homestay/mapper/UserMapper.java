package com.homestay.mapper;

import com.homestay.dto.request.UpdateUserRequest;
import com.homestay.dto.request.UserRequest;
import com.homestay.dto.response.UserResponse;
import com.homestay.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", ignore = true)
    User toUser(UserRequest request);

    @Mapping(target = "homestays", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "role", source = "role.roleName")
    @Mapping(target = "urlAvatar", source = "avatar.url")
    UserResponse toUserResponse(User user);

    void updateToUser(@MappingTarget User user, UpdateUserRequest request);

}
