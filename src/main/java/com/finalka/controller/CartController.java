package com.finalka.controller;

import com.finalka.dto.*;
import com.finalka.entity.Cart;
import com.finalka.service.CartService;
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

    @PostMapping("/create")
    public ResponseEntity<String> save(@RequestBody CreateCartDto createCartDto){
        try {
            cartService.createCart(createCartDto);
            return new ResponseEntity<>("Корзина успешно создана", HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>("Не удалось создать корзину", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{cartId}/update-product-quantity")
    public ResponseEntity<CartDto> updateProductQuantityInCart(@PathVariable Long cartId,
                                                               @RequestBody UpdateProductQuantityDto updateProductQuantityDto) {
        try {
            CartDto updatedCart = cartService.updateCart(cartId, updateProductQuantityDto);
            if (updatedCart != null) {
                return ResponseEntity.ok(updatedCart);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/{cartId}")
    public ResponseEntity<CartDto> getCartById(@PathVariable Long cartId) {
        CartDto cartDto = cartService.findCartById(cartId);
        if (cartDto != null) {
            return new ResponseEntity<>(cartDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
    @PostMapping("/{cartId}/addProduct/{productId}")
    public ResponseEntity<String> addProductToCart(@PathVariable Long cartId, @PathVariable Long productId) {
        try {
            cartService.addProductToCart(cartId, productId);
                return new ResponseEntity<>("Товар успешно добавлен в корзину", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Не удалось добавить товар в корзину", HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/{cartId}/products")
    public ResponseEntity<List<CreateProductOfShopDto>> getAllProductsInCart(@PathVariable Long cartId) {
        List<CreateProductOfShopDto> productsInCart = cartService.getAllProductsInCart(cartId);
        return ResponseEntity.ok(productsInCart);
    }

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