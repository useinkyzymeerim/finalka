package com.finalka.controller;

import com.finalka.dto.CreateProductOfShopDto;
import com.finalka.dto.MenuWithRecipeDTO;
import com.finalka.exception.InvalidProductDataException;
import com.finalka.exception.ProductAlreadyExistsException;
import com.finalka.exception.ProductNotFoundException;
import com.finalka.service.ProductOfShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MagicMenu", description = "Тут находятся все роуты для работы админа в магазине")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admins")
public class AdminController {
    private final ProductOfShopService productOfShopService;

    @Operation(summary = "Этот роут для создания(сохранения) продукта в магазине ")
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
    @PostMapping()
    public String saveProduct(@Validated @RequestBody CreateProductOfShopDto createProductOfShopDto) throws ProductAlreadyExistsException, InvalidProductDataException {
        productOfShopService.createProduct(createProductOfShopDto);
        return "Продукт успешно создан";
    }
    @Operation(summary = "Этот роут для обновления продукта в магазине")
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

    @PutMapping("/update/{productId}")
    public String updateProduct(@PathVariable Long productId, @RequestBody CreateProductOfShopDto productOfShopDto) throws InvalidProductDataException {
        CreateProductOfShopDto updatedProduct = productOfShopService.updateProduct(productId, productOfShopDto);
        return "Продукт успешно обновлен: " + updatedProduct;
    }

    @Operation(summary = "Этот роут удаляет продукт в магазине по айди")
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

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        try {
            productOfShopService.deleteProduct(productId);
            return ResponseEntity.ok("Товар успешно удален");
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Не удалось удалить продукт: " + e.getMessage());
        }
    }
}
