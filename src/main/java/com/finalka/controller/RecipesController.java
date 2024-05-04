package com.finalka.controller;


import com.finalka.dto.ProductDTO;
import com.finalka.dto.RecipeDetailsDTO;
import com.finalka.dto.RecipeWithProductDTO;
import com.finalka.dto.RecipesDto;
import com.finalka.service.RecipesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/recipes")
public class RecipesController {

    private final RecipesService recipeService;
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
    @Operation(summary = "Роуд возвращает все не удаленные рецепты")
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
                    description = "Все записи получены успешно",
                    content = {@Content(mediaType = "application/json")})
    })
    @Operation(summary = "Этот роут возвращает Продукт с названием")
    @GetMapping("/getByProductName")
    public ResponseEntity<List<RecipesDto>> getRecipesByProduct(@RequestParam String productName) {
        List<RecipesDto> recipeDTOList = recipeService.findByProduct(productName);
        return ResponseEntity.ok(recipeDTOList);
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
    public ResponseEntity<RecipeWithProductDTO> createRecipeWithProducts(@RequestBody RecipeWithProductDTO recipeDto) {
        RecipeWithProductDTO createdRecipe = recipeService.createRecipeWithProducts(recipeDto);
        return ResponseEntity.ok(createdRecipe);
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
    @Operation(summary = "Роуд удаляет рецепт по id")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        try {
            return new ResponseEntity<>(recipeService.delete(id), HttpStatus.OK);
        } catch (NullPointerException nullPointerException){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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

    public ResponseEntity<RecipesDto> findById(@PathVariable Long id){
        try {
            return new ResponseEntity<>(recipeService.findById(id), HttpStatus.OK);
        } catch (NullPointerException nullPointerException){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
