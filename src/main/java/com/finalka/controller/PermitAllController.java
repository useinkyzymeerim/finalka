package com.finalka.controller;

import com.finalka.dto.MenuWithRecipeDTO;
import com.finalka.dto.RecipesDto;
import com.finalka.dto.UserDto;
import com.finalka.repo.UserRepo;
import com.finalka.service.RecipesService;
import com.finalka.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/all")
public class PermitAllController {

    private final UserServiceImpl service;
    private final UserRepo repo;
    private final RecipesService recipesService;
    @Operation(summary = "Этот роут для регистрации ")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = MenuWithRecipeDTO.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })
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
    @Operation(summary = "Этот роут возвращает все опубликованные рецепты")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = MenuWithRecipeDTO.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })
    @GetMapping("/allRecipe")
    public ResponseEntity<List<RecipesDto>> findAll() {
        try {
            return new ResponseEntity<>(recipesService.findAll(), HttpStatus.OK);
        } catch (NullPointerException nullPointerException) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
