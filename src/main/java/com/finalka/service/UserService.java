package com.finalka.service;


import com.finalka.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto save(UserDto user);
    List<UserDto> getAll();
    UserDto getById(Long id);

    UserDto update(UserDto userDto);

}