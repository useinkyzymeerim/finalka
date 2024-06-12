package com.finalka.controller;

import com.finalka.dto.MenuWithRecipeDTO;
import com.finalka.dto.UserDto;
import com.finalka.exception.EmailSendingException;
import com.finalka.exception.InvalidUserDataException;
import com.finalka.exception.UsernameAlreadyExistsException;
import com.finalka.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Public API", description = "Тут находятся все общие роуты для не авторизованных пользователей")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/all")
public class PermitAllController {

    private final UserServiceImpl service;
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
    @PostMapping("/registration")
    public String save(@Valid @RequestBody UserDto userToSave) {
        try {
            service.save(userToSave);
            return "Регистрация пользователя прошла успешно.";
        } catch (UsernameAlreadyExistsException | EmailSendingException | InvalidUserDataException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Не удалось зарегистрировать пользователя. Пожалуйста, попробуйте еще раз.";
        }
    }
}