package com.finalka.mapper.impl;

import com.finalka.dto.UserDto;
import com.finalka.entity.User;
import com.finalka.mapper.UserMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapperImpl implements UserMapper {
    @Override
    public UserDto toDto(User user) {
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .username(user.getUsername())
                .roles(user.getRoles())
                .lastAuthentication(user.getLastAuthentication())
                .phoneNumber(user.getPhoneNumber())
                .build();
        return userDto;
    }

    @Override
    public User toEntity(UserDto userDto) {
        User user = User.builder()
                .name(userDto.getName())
                .surname(userDto.getSurname())
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .roles(userDto.getRoles())
                .lastAuthentication(userDto.getLastAuthentication())
                .phoneNumber(userDto.getPhoneNumber())
                .build();
        return user;
    }

    @Override
    public List<UserDto> toDtoList(List<User> users) {
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            userDtos.add(toDto(user));
        }
        return userDtos;
    }
}

