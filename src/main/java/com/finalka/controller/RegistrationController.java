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
public class RegistrationController {

    private final UserServiceImpl service;
    @PostMapping()
    public ResponseEntity<String> save(@RequestBody UserDto userToSave) {
        try {
            service.save(userToSave);
            return new ResponseEntity<>("Регистрация пользователя прошла успешно.", HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>("Не удалось зарегистрировать пользователя.", HttpStatus.BAD_REQUEST);
        }
    }

}
