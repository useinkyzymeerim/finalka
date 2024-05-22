package com.finalka.controller;


import com.finalka.dto.*;
import com.finalka.entity.Products;
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
import lombok.RequiredArgsConstructor;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserServiceImpl service;
    private final RecipesService recipeService;
    private final MenuService menuService;
    private final ReviewService reviewService;

    @PutMapping()
    public ResponseEntity<UserDto> update(@RequestBody UserDto userDto) {
        try {
            return new ResponseEntity<>(service.update(userDto), HttpStatus.OK);
        } catch (RuntimeException runtimeException) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

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
    @GetMapping("/recipeDetails/{recipeId}")
    public ResponseEntity<List<RecipeDetailsDTO>> getRecipeDetails(@PathVariable Long recipeId) {
        List<RecipeDetailsDTO> recipeDetails = recipeService.findRecipeDetails(recipeId);
        return ResponseEntity.ok(recipeDetails);
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "записи получены успешно",
                    content = {@Content(mediaType = "application/json")})
    })
    @Operation(summary = "Этот роут возвращает Рецепты с названием")
    @GetMapping("/recipes-by-name")
    public ResponseEntity<List<RecipesDto>> getRecipesByNameIgnoreCase(@RequestParam String recipeName) {
        List<RecipesDto> recipeDTOList = recipeService.findByRecipeNameIgnoreCase(recipeName);
        return ResponseEntity.ok(recipeDTOList);
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

    @PostMapping("/search")
    public ResponseEntity<List<RecipeWithProductDTO>> searchRecipesByProducts(@RequestBody List<String> userProducts) {
        List<RecipeWithProductDTO> recipes = recipeService.findRecipesByProducts(userProducts);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/{menuId}/requiredProducts")
    public ResponseEntity<?> getRequiredProductsForMenu(@PathVariable Long menuId) {
        try {
            Map<Products, Integer> productQuantityMap = menuService.calculateRequiredProductsForMenu(menuId);


            Map<String, Integer> productQuantityStringMap = new HashMap<>();
            for (Map.Entry<Products, Integer> entry : productQuantityMap.entrySet()) {
                productQuantityStringMap.put(entry.getKey().getProductName(), entry.getValue());
            }

            return new ResponseEntity<>(productQuantityStringMap, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Failed to calculate required products: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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

    @PostMapping("/review")
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO reviewDTO) {
        try {
            ReviewDTO createdReview = reviewService.createReview(reviewDTO);
            return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByRecipeId(@PathVariable Long recipeId) {
        try {
            List<ReviewDTO> reviews = reviewService.getReviewsByRecipeId(recipeId);
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}