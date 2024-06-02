package com.finalka.controller;

import com.finalka.dto.MenuWithRecipeDTO;
import com.finalka.service.MailService;
import com.finalka.service.impl.ReminderServiceImpl;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderServiceImpl reminderService;

    @Operation(summary = "Этот роут для установки напоминание")
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
    @PostMapping("/set")
    @Transactional
    public ResponseEntity<String> setReminder(@Valid @RequestParam Long userId,
                                              @RequestParam int hour,
                                              @RequestParam int minute,
                                              @RequestParam String message) {
        try {
            reminderService.setReminder(userId, hour, minute, message);
            return ResponseEntity.ok("Напоминание установлено успешно.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Не удалось установить напоминание.");
        }
    }
    @Operation(summary = "Этот роут отменяет  напоминание по айди пользователя ")
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

    @DeleteMapping("/cancel/{userId}")
    @Transactional
    public ResponseEntity<String> cancelReminder(@PathVariable Long userId) {
        try {
            reminderService.cancelReminder(userId);
            return ResponseEntity.ok("Напоминание успешно отменено.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Не удалось отменить напоминание.");
        }
    }
}