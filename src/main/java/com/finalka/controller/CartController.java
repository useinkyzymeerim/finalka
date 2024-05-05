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
    public ResponseEntity<CreateCartDto> save(@RequestBody CreateCartDto createCartDto){
        try {
            return new ResponseEntity<>(cartService.createCart(createCartDto), HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<Void> addProductToCart(@PathVariable Long cartId, @PathVariable  Long productId) {
        cartService.addProductToCart(cartId, productId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{cartId}/products")
    public ResponseEntity<List<CreateProductOfShopDto>> getAllProductsInCart(@PathVariable Long cartId) {
        List<CreateProductOfShopDto> productsInCart = cartService.getAllProductsInCart(cartId);
        return ResponseEntity.ok(productsInCart);
    }

    @DeleteMapping("/{cartId}/removeProduct/{productId}")
    public ResponseEntity<Void> removeProductFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
        cartService.removeProductFromCart(cartId, productId);
        return ResponseEntity.noContent().build();
    }
}