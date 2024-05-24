package com.finalka.service;

import com.finalka.dto.*;

import java.util.List;

public interface CartService {
    void createCart(CreateCartDto createCartDto);
    void addProductToCart(Long cartId, Long productId) ;
    void removeProductFromCart(Long cartId, Long productId);
    CartDetailDto updateCart(Long cartId, UpdateProductQuantityDto updateProductQuantityDto);
    CartDetailDto findCartById(Long cartId);
}
