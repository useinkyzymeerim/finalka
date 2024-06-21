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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Tag(name = "Public API", description = "Тут находятся все общие роуты для не авторизованных пользователей")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/all")
public class PermitAllController {

    private final UserServiceImpl service;
    @Operation(summary = "Этот роут для регистрации ")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })
    @PostMapping("/registration")
    public ResponseEntity<Long> save(@Valid @RequestBody UserDto userToSave) {
        try {
            Long cartId = service.save(userToSave);
            return ResponseEntity.status(HttpStatus.CREATED).body(cartId);
        } catch (UsernameAlreadyExistsException e) {
            log.error("Ошибка регистрации: имя пользователя уже существует.", e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (EmailSendingException e) {
            log.error("Ошибка регистрации: не удалось отправить письмо с подтверждением.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (InvalidUserDataException e) {
            log.error("Ошибка регистрации: недопустимые данные пользователя.", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("Неизвестная ошибка при регистрации.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/server-time")
    public String getServerTime() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Текущее время сервера: {}", now);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
}
