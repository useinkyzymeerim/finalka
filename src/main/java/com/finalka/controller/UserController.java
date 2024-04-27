package com.finalka.controller;


import com.finalka.dto.UserDto;
import com.finalka.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserServiceImpl service;

    @PostMapping
    public ResponseEntity<UserDto> save(@RequestBody UserDto userToSave) {
        try {
            return new ResponseEntity<>(service.save(userToSave), HttpStatus.CREATED);
        } catch (RuntimeException runtimeException) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(service.getById(id), HttpStatus.OK);
        } catch (RuntimeException runtimeException) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAll()    {
        return new ResponseEntity<>(service.getAll(), HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<UserDto> update(@RequestBody UserDto userDto) {
        try {
            return new ResponseEntity<>(service.update(userDto), HttpStatus.OK);
        } catch (RuntimeException runtimeException) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}