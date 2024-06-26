package com.finalka.mapper;

import com.finalka.dto.UpdateUserDto;
import com.finalka.dto.UserDto;
import com.finalka.entity.User;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto userDto);
    List<UserDto> toDtoList(List<User> user);
}

