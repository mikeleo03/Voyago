package com.group4.authentication.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.group4.authentication.data.model.User;
import com.group4.authentication.dto.SignupRequest;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // Mapping SignupRequest to User entity
    @Mapping(target = "role", ignore = true)
    User toUser(SignupRequest signupRequest);
}
