package com.finalka.controller;

import com.finalka.dto.CreateProductOfShopDto;
import com.finalka.dto.MenuWithRecipeDTO;
import com.finalka.service.ProductOfShopService;
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
    public ResponseEntity<String> saveProduct(@Valid @RequestBody CreateProductOfShopDto createProductOfShopDto){
        try {
            productOfShopService.createProduct(createProductOfShopDto);
            return new ResponseEntity<>("Продукт успешно создана", HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>("Не удалось создать продукт", HttpStatus.BAD_REQUEST);
        }
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
    public ResponseEntity<CreateProductOfShopDto> updateProduct(@PathVariable Long productId, @RequestBody CreateProductOfShopDto productOfShopDto) {
        CreateProductOfShopDto updatedProduct = productOfShopService.updateProduct(productId, productOfShopDto);
        return ResponseEntity.ok(updatedProduct);
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
            return new ResponseEntity<>("Товар успешно удален", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Не удалось удалить товар", HttpStatus.BAD_REQUEST);
        }
    }
}
