package com.finalka.controller;

import com.finalka.dto.*;
import com.finalka.exception.*;
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
import org.springframework.dao.DataAccessException;
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
    public ResponseEntity<?> createRecipeWithProducts(@Valid @RequestBody RecipeWithProductDTO recipeDto) {
        try {
            recipeService.createRecipeWithProducts(recipeDto);
            return new ResponseEntity<>("Рецепт успешно создан", HttpStatus.CREATED);
        } catch (RecipeCreationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
        String response = recipeService.delete(id);
        return ResponseEntity.ok(response);
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
        List<RecipesDto> recipes = recipeService.findAllByChef(token);
        return ResponseEntity.ok(recipes);
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
    public String addRecipeToMenu(@RequestBody RecipeAddProductDto menuRecipeRequestDto) {
        return recipeService.addRecipeToMenu(menuRecipeRequestDto);
    }

    @PutMapping("/update")
    public String updateRecipe(@RequestBody RecipeUpdateDTO recipeUpdateDTO) {
        try {
            recipeService.updateRecipe(recipeUpdateDTO);
            return "Рецепт успешно обновлен";
        } catch (RecipeNotFoundException | RecipeDeletedException | ProductNotFoundException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Не удалось обновить рецепт с продуктами";
        }
    }

    @DeleteMapping("/{recipeId}/products/{productId}")
    public ResponseEntity<String> removeProductFromRecipe(@PathVariable Long recipeId, @PathVariable Long productId) {
        try {
            recipeService.removeProductFromRecipe(recipeId, productId);
            return ResponseEntity.ok("Связь продукта с рецептом успешно удалена");
        } catch (RecipeNotFoundException | ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RecipeDeletedException | ProductDeletedException e) {
            return ResponseEntity.status(HttpStatus.GONE).body(e.getMessage());
        } catch (RecipeProductLinkNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RecipeProductLinkRemovalException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unexpected error occurred: " + e.getMessage());
        }
    }
}
