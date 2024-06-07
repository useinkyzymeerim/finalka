package com.finalka.service;


import com.finalka.dto.UpdateUserDto;
import com.finalka.dto.UserDto;
import com.finalka.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto save(UserDto user);
    List<UserDto> getAll();
    UserDto getById(Long id);
    UserDto updateUser(UpdateUserDto updateUserDto);
    void generateResetToken(String email);
    void resetPassword(String token, String newPassword);

}