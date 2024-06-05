package com.finalka.controller;


import com.finalka.dto.*;
import com.finalka.entity.Products;
import com.finalka.enums.Units;
import com.finalka.service.MenuService;
import com.finalka.service.RecipesService;
import com.finalka.service.ReviewService;
import com.finalka.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final RecipesService recipeService;
    private final MenuService menuService;
    private final ReviewService reviewService;


    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Все записи получены успешно",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RecipesDto.class)))}),
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

    @Operation(summary = "Этот роут возвращает отзыв к рецепту по его айди")
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

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByRecipeId(@PathVariable Long recipeId) {
        try {
            List<ReviewDTO> reviews = reviewService.getReviewsByRecipeId(recipeId);
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Operation(summary = "Этот роут для добовление рецептов в меню")
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
    @Operation(summary = "Роут возвращает все не удаленные рецепты")
    @GetMapping("/all")
    public ResponseEntity<List<RecipesDto>> findAll() {
        try {
            return new ResponseEntity<>(recipeService.findAll(), HttpStatus.OK);
        } catch (NullPointerException nullPointerException) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}