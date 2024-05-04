package com.finalka.service;

import com.finalka.dto.CartDto;
import com.finalka.dto.CreateCartDto;
import com.finalka.dto.CreateProductOfShopDto;
import com.finalka.dto.UpdateProductQuantityDto;

import java.util.List;

public interface CartService {
    CreateCartDto createCart(CreateCartDto createCartDto);
    void addProductToCart(Long cartId, Long productId) ;
    List<CreateProductOfShopDto> getAllProductsInCart(Long cartId);
    void removeProductFromCart(Long cartId, Long productId);
    CartDto updateCart(Long cartId, UpdateProductQuantityDto updateProductQuantityDto);
    CartDto findCartById(Long cartId);
}
