package com.finalka.controller;


import com.finalka.dto.*;
import com.finalka.exception.InvalidUserDataException;
import com.finalka.exception.UnauthorizedException;
import com.finalka.service.MenuService;
import com.finalka.service.RecipesService;
import com.finalka.service.ReviewService;
import com.finalka.service.UserService;
import com.finalka.service.impl.ReminderServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Tag(name = "User API", description = "Тут находятся все роуты для пользователей")
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
    public RecipeDetailsDTO getRecipeById(@PathVariable Long recipeId) {
        RecipeDetailsDTO recipeDetailsDTO = recipeService.getRecipeWithProductsById(recipeId);
        return recipeDetailsDTO;
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
    public List<RecipeWithProductDTO> findRecipesByProducts(@RequestParam String userProductsString) {
        return recipeService.findRecipesByProducts(userProductsString);
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
    public List<Map<String, Object>> getRequiredProductsForMenu(@PathVariable Long menuId) {
        return menuService.calculateRequiredProductsForMenu(menuId);
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
    public ReviewDTO createReview(@Valid @RequestBody ReviewDTO reviewDTO) {
        return reviewService.createReview(reviewDTO);
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
    public Long createReminder(@RequestParam int hour, @RequestParam int minute, @RequestParam String message) {
        try {
            return reminderService.setReminder(hour, minute, message);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось установить напоминание", e);
        }
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
    public List<ReminderDto> getAllReminders() {
        return reminderService.getAllReminders();
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
    public String cancelReminder(@PathVariable Long reminderId) {
        reminderService.cancelReminder(reminderId);
        return "Напоминание успешно отменено";
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
    public void updateReminder(@PathVariable Long reminderId, @RequestParam int hour, @RequestParam int minute, @RequestParam String message) {
        CreateReminderDto reminderDto = new CreateReminderDto();
        reminderDto.setHour(hour);
        reminderDto.setMinute(minute);
        reminderDto.setMessage(message);
        reminderService.updateReminder(reminderId, reminderDto);
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
    public void deleteReminder(@PathVariable Long reminderId) {
        reminderService.deleteReminder(reminderId);
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
    public List<ReminderDto> getUserReminders(@RequestHeader("Authorization") String token) throws UnauthorizedException {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new UnauthorizedException("Пользаватель не найден");
        }
        return reminderService.getAllReminders();
    }
}

