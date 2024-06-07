package com.finalka.controller;


import com.finalka.dto.*;
import com.finalka.entity.Products;
import com.finalka.enums.Units;
import com.finalka.service.MenuService;
import com.finalka.service.RecipesService;
import com.finalka.service.ReviewService;
import com.finalka.service.UserService;
import com.finalka.service.impl.ReminderServiceImpl;
import com.finalka.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Tag(name = "MagicMenu", description = "Тут находятся все роуты для пользователей")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final RecipesService recipeService;
    private final MenuService menuService;
    private final ReviewService reviewService;
    private final UserService service;
    private final ReminderServiceImpl reminderService;


    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Все записи получены успешно",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RecipeDetailsDTO.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Рецепта с продуктами не найдены")

    })
    @Operation(summary = "Этот роут возвращает Рецепты с продуктами по ID")
    @GetMapping("/{recipeId}/products")
    public ResponseEntity<RecipeDetailsDTO> getRecipeById(@PathVariable Long recipeId) {
        try {
            RecipeDetailsDTO recipeDetailsDTO = recipeService.getRecipeWithProductsById(recipeId);
            return ResponseEntity.ok(recipeDetailsDTO);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Рецепт найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RecipesDto.class))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Рецепт не найден")
    })
    @Operation(summary = "Роут для поиска рецепт по id")
    @GetMapping("/{id}")
    public ResponseEntity<RecipesDto> findById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(recipeService.findById(id), HttpStatus.OK);
        } catch (NullPointerException nullPointerException) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Этот роут для поиска рецепта по нескольким продуктам")
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

    @PostMapping("/search")
    public ResponseEntity<List<RecipeWithProductDTO>> searchRecipesByProducts(@RequestBody List<String> userProducts) {
        List<RecipeWithProductDTO> recipes = recipeService.findRecipesByProducts(userProducts);
        return ResponseEntity.ok(recipes);
    }

    @Operation(summary = "Этот роут возвращает количество всех продуктов в одном меню по айди")
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

    @GetMapping("/{menuId}/requiredProducts")
    public ResponseEntity<?> getRequiredProductsForMenu(@PathVariable Long menuId) {
        try {
            Map<Products, Map.Entry<Integer, Units>> productQuantityMap = menuService.calculateRequiredProductsForMenu(menuId);

            List<Map<String, Object>> productQuantityList = new ArrayList<>();
            for (Map.Entry<Products, Map.Entry<Integer, Units>> entry : productQuantityMap.entrySet()) {
                Map<String, Object> productInfo = new HashMap<>();
                productInfo.put("productName", entry.getKey().getProductName());
                productInfo.put("quantity", entry.getValue().getKey());
                productInfo.put("unit", entry.getValue().getValue().toString());
                productQuantityList.add(productInfo);
            }

            return new ResponseEntity<>(productQuantityList, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Failed to calculate required products: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Operation(summary = "Этот роут для создание отзыва")
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

    @PostMapping("/review")
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewDTO reviewDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            bindingResult.getAllErrors().forEach(error ->
                    errorMsg.append(error.getDefaultMessage()).append("; "));
            return new ResponseEntity<>("Ошибки валидации: " + errorMsg.toString(), HttpStatus.BAD_REQUEST);
        }

        try {
            ReviewDTO createdReview = reviewService.createReview(reviewDTO);
            return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>("Resource not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
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
    @PostMapping("/setReminder")
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
    @GetMapping("/getAllReminders")
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

            List<ReminderDto> reminders = reminderService.getUserReminders();

            return ResponseEntity.ok(reminders);
        } catch (Exception e) {
            log.error("Ошибка при получении напоминаний пользователя", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<UserDto> updateUser(@RequestBody UpdateUserDto updateUserDto) {
        UserDto updatedUser = service.updateUser(updateUserDto);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/password-reset-request")
    public ResponseEntity<?> requestPasswordReset(@RequestBody ResetPasswordRequest request) {
        service.generateResetToken(request.getEmail());
        return ResponseEntity.ok("Запрос на сброс пароля успешно обработан.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        try {
            service.resetPassword(resetPasswordDto.getToken(), resetPasswordDto.getNewPassword());
            return ResponseEntity.ok("Пароль успешно обновлен.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

