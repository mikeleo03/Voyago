package com.group4.user.mapper;

import com.group4.user.data.model.User;
import com.group4.user.dto.SignupRequest;
import com.group4.user.dto.UserDTO;
import com.group4.user.dto.UserSaveDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // User - UserDTO
    UserDTO toUserDTO(User user);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toUser(UserDTO userDTO);

    // User - UserSaveDTO
    UserSaveDTO toUserSaveDTO(User user);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "picture", ignore = true)
    @Mapping(target = "status", ignore = true)
    User toUser(UserSaveDTO userSaveDTO);

    // UserSaveDTO - SignupRequest
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    SignupRequest toSignupRequest(UserSaveDTO userSaveDTO);
}
