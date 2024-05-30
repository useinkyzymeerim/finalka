package com.finalka.controller;

import com.finalka.dto.UserDto;
import com.finalka.repo.UserRepo;
import com.finalka.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/registration")
public class RegistrationController {

    private final UserServiceImpl service;
    private final UserRepo repo;
    @PostMapping()
    public ResponseEntity<String> save(@Valid @RequestBody UserDto userToSave, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            bindingResult.getAllErrors().forEach(error ->
                    errorMsg.append(error.getDefaultMessage()).append("; "));
            return new ResponseEntity<>("Ошибки валидации: " + errorMsg.toString(), HttpStatus.BAD_REQUEST);
        }

        try {
            if (repo.existsByUsername(userToSave.getUsername())) {
                return new ResponseEntity<>("Имя пользователя уже существует", HttpStatus.BAD_REQUEST);
            }

            service.save(userToSave);
            return new ResponseEntity<>("Регистрация пользователя прошла успешно.", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Не удалось зарегистрировать пользователя. Пожалуйста, попробуйте еще раз.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
