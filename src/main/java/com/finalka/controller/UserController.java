package com.finalka.controller;


import com.finalka.dto.RecipeDetailsDTO;
import com.finalka.dto.RecipesDto;
import com.finalka.dto.UserDto;
import com.finalka.service.RecipesService;
import com.finalka.service.impl.UserServiceImpl;
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


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserServiceImpl service;
    private final RecipesService recipeService;






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