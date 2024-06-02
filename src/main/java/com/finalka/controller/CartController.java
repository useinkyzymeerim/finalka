package com.finalka.controller;

import com.finalka.dto.*;
import com.finalka.entity.Cart;
import com.finalka.service.CartService;
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
@RequestMapping("/carts")
public class CartController {
    private final CartService cartService;

    @Operation(summary = "Этот роут для создания корзины в магазине ")
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
    @PostMapping("/")
    public ResponseEntity<String> save(@RequestBody CreateCartDto createCartDto){
        try {
            cartService.createCart(createCartDto);
            return new ResponseEntity<>("Корзина успешно создана", HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>("Не удалось создать корзину", HttpStatus.BAD_REQUEST);
        }
    }
    @Operation(summary = "Этот роут для обновления количество продуктов в корзине по айди корзины")
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

    @PutMapping("/{cartId}/update-product-quantity")
    public ResponseEntity<CartDetailDto> updateProductQuantityInCart(@PathVariable Long cartId,
                                                                     @RequestBody UpdateProductQuantityDto updateProductQuantityDto) {
        try {
            CartDetailDto updatedCart = cartService.updateCart(cartId, updateProductQuantityDto);
            if (updatedCart != null) {
                return ResponseEntity.ok(updatedCart);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @Operation(summary = "Этот роут возвращает корзину по айди")
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
    @GetMapping("/{cartId}")
    public ResponseEntity<CartDetailDto> getCartById(@PathVariable Long cartId) {
        CartDetailDto cartDto = cartService.findCartById(cartId);
        if (cartDto != null) {
            return new ResponseEntity<>(cartDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
    @Operation(summary = "Этот роут для добовления продукта по айди в корзину ")
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
    @PostMapping("/{cartId}/addProduct/{productId}")
    public ResponseEntity<String> addProductToCart(@PathVariable Long cartId, @PathVariable Long productId) {
        try {
            cartService.addProductToCart(cartId, productId);
            return new ResponseEntity<>("Товар успешно добавлен в корзину", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Не удалось добавить товар в корзину", HttpStatus.BAD_REQUEST);
        }
    }
    @Operation(summary = "Этот роут для удаления продукта по айди из корзины ")
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

    @DeleteMapping("/{cartId}/removeProduct/{productId}")
    public ResponseEntity<String> removeProductFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
        try {
            cartService.removeProductFromCart(cartId, productId);
            return new ResponseEntity<>("Товар успешно удален из корзины", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Не удалось удалить товар из корзины", HttpStatus.BAD_REQUEST);
        }
    }

}