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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.hibernate.query.sqm.tree.SqmNode.log;

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
    public ResponseEntity<String> createRecipeWithProducts(@RequestBody RecipeWithProductDTO recipeDto) {
        try {
            recipeService.createRecipeWithProducts(recipeDto);
            return new ResponseEntity<>("Рецепт успешно создан", HttpStatus.CREATED);
        } catch (Exception e){
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
}
