package com.finalka.service;


import com.finalka.dto.UpdateUserDto;
import com.finalka.dto.UserDto;
import com.finalka.exception.InvalidUserDataException;
import com.finalka.exception.UnauthorizedException;

import java.util.List;

public interface UserService {
    UserDto save(UserDto user) throws InvalidUserDataException;
    List<UserDto> getAll();
    UserDto getById(Long id);
    UserDto updateUser(UpdateUserDto updateUserDto) throws UnauthorizedException, InvalidUserDataException;
    void generateResetToken(String email);
    void resetPassword(String token, String newPassword);
}