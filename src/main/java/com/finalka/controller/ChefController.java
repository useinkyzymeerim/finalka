package com.finalka.controller;

import com.finalka.dto.*;
import com.finalka.service.RecipesService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.hibernate.query.sqm.tree.SqmNode.log;
@Tag(name = "MagicMenu", description = "Тут находятся все роуты для поваров")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chefs")
public class ChefController {

    private final RecipesService recipeService;
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Рецепт создан успешно ",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDTO.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Рецепт не был добавлен в базу")
    })
    @Operation(summary = "Роут для создание рецепта")
    @PostMapping

    public ResponseEntity<?> createRecipeWithProducts(@Valid @RequestBody RecipeWithProductDTO recipeDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            bindingResult.getAllErrors().forEach(error ->
                    errorMsg.append(error.getDefaultMessage()).append("; "));
            return new ResponseEntity<>("Ошибки валидации: " + errorMsg.toString(), HttpStatus.BAD_REQUEST);
        }
        try {
            recipeService.createRecipeWithProducts(recipeDto);
            return new ResponseEntity<>("Рецепт успешно создан", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Не удалось создать рецепт", HttpStatus.BAD_REQUEST);
        }
    }
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Рецепт найден и успешно удален",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Рецепт не найден")
    })
    @Operation(summary = "Роут удаляет рецепт по id")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable Long id) {
        try {
            String response = recipeService.delete(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (SecurityException e) {
            log.error("Ошибка доступа при попытке удалить рецепт с id {}", id, e);
            return new ResponseEntity<>("Вы не имеете прав на удаление этого рецепта", HttpStatus.FORBIDDEN);
        } catch (NullPointerException e) {
            log.error("Ошибка при попытке удалить рецепт с id {}", id, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Ошибка при попытке удалить рецепт с id {}", id, e);
            return new ResponseEntity<>("Не удалось удалить рецепт", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "В базе есть доступные рецепты",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RecipesDto.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Рецепта нет")
    })
    @Operation(summary = "Роут возвращает все свои рецепты повара")

    @GetMapping("/allByChef")
    public ResponseEntity<List<RecipesDto>> getAllRecipesForCurrentUser(@RequestHeader("Authorization") String token) {
        try {

            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String authToken = token.substring(7);


            List<RecipesDto> recipes = recipeService.findAllByChef(authToken);

            return ResponseEntity.ok(recipes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @Operation(summary = "Этот роут добовляет рецепты по айди  в меню айди ")
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

    @PostMapping("/add-to-menu")
    public ResponseEntity<String> addRecipeToMenu(@RequestBody RecipeAddProductDto menuRecipeRequestDto) {
        try {
            recipeService.addRecipeToMenu(menuRecipeRequestDto.getMenuId(), menuRecipeRequestDto.getRecipeId());
            return ResponseEntity.ok("Рецепт успешно добавлен в меню");
        } catch (Exception e) {
            log.error("Ошибка при добавлении рецепта в меню", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Не удалось добавить рецепт в меню");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<RecipeUpdateDTO> updateRecipe(@RequestBody RecipeUpdateDTO recipeUpdateDTO) {
        RecipeUpdateDTO updatedRecipe = recipeService.updateRecipe(recipeUpdateDTO);
        return ResponseEntity.ok(updatedRecipe);
    }

    @DeleteMapping("/{recipeId}/products/{productId}")
    public ResponseEntity<String> removeProductFromRecipe(@PathVariable Long recipeId, @PathVariable Long productId) {
        try {
            recipeService.removeProductFromRecipe(recipeId, productId);
            return ResponseEntity.ok("Связь продукта с рецептом успешно удалена");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
