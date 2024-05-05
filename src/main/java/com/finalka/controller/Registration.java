package com.finalka.controller;

import com.finalka.dto.UserDto;
import com.finalka.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/registration")
public class Registration {

    private final UserServiceImpl service;

    @PostMapping("")
    public ResponseEntity<UserDto> save(@RequestBody UserDto userToSave) {
        try {
            return new ResponseEntity<>(service.save(userToSave), HttpStatus.CREATED);
        } catch (RuntimeException runtimeException) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
