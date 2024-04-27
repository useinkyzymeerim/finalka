package com.finalka.service;


import com.finalka.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto save(UserDto user);

    UserDto getById(Long id);

    List<UserDto> getAll();

    UserDto update(UserDto userDto);

}