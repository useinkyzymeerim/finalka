package com.finalka.controller;

import com.finalka.dto.*;
import com.finalka.service.MailService;
import com.finalka.service.impl.ReminderServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.hibernate.query.sqm.tree.SqmNode.log;


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
    public ResponseEntity<String> createReminder(@RequestParam int hour, @RequestParam int minute, @RequestParam String message) {
        reminderService.setReminder(hour, minute, message);
        return ResponseEntity.ok("Напоминание успешно установлено");
    }

    @Operation(summary = "Этот роут возвращает все напоминание")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ReminderDto.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })
    @GetMapping("/all")
    public ResponseEntity<List<ReminderDto>> getAllReminders() {
        List<ReminderDto> reminders = reminderService.getAllReminders();
        return ResponseEntity.ok(reminders);
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
    @DeleteMapping("/cancel/{reminderId}")
    public ResponseEntity<String> cancelReminder(@PathVariable Long reminderId) {
        reminderService.cancelReminder(reminderId);
        return ResponseEntity.ok("Напоминание успешно отменено");
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Напоминание найден и успешно обновлен",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MenuDTO.class))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Напоминание не найден")
    })
    @Operation(summary = "Роут обновляет напоминание, id напоминание передается непосредственно в модели," +
            " по ней и идет поиск, важно, что бы все поля не были пустыми иначе засетит null, но передавать создателя и " +
            "обновляющего с датами не нужно, это делает бэк")
    @PutMapping("/update/{reminderId}")
    public ResponseEntity<Void> updateReminder(@PathVariable Long reminderId, @RequestParam int hour, @RequestParam int minute, @RequestParam String message) {
        CreateReminderDto reminderDto = new CreateReminderDto();
        reminderDto.setHour(hour);
        reminderDto.setMinute(minute);
        reminderDto.setMessage(message);

        try {
            reminderService.updateReminder(reminderId, reminderDto);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Роут удаляет напоминание по id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Напоминание найден и успешно удален",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Напоминание не найден")
    })
    @DeleteMapping("/delete/{reminderId}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long reminderId) {
        reminderService.deleteReminder(reminderId);
        return ResponseEntity.ok().build();
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Получены все напоминание пользователя",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RecipesDto.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Напоминаний нет")
    })
    @Operation(summary = "Роут возвращает все свои напоминание пользователя")
    @GetMapping("/getMyReminders")
    public ResponseEntity<List<ReminderDto>> getUserReminders(@RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String authToken = token.substring(7);

            List<ReminderDto> reminders = reminderService.getUserReminders();

            return ResponseEntity.ok(reminders);
        } catch (Exception e) {
            log.error("Ошибка при получении напоминаний пользователя", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}