package com.finalka.service.impl;

import com.finalka.dto.*;
import com.finalka.entity.Cart;
import com.finalka.entity.ProductOfShop;
import com.finalka.entity.User;
import com.finalka.exception.CartNotFoundException;
import com.finalka.mapper.ProductMapper;
import com.finalka.mapper.UserMapper;
import com.finalka.repo.CartRepo;
import com.finalka.repo.ProductOfShopRepo;
import com.finalka.service.CartService;
import com.finalka.service.ProductOfShopService;
import com.finalka.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartRepo cartRepo;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final ProductOfShopRepo productOfShopRepo;

    public CreateCartDto createCart(CreateCartDto createCartDto) {
        UserDto userDtoOfShop = userService.getById(createCartDto.getUserId());
        User user = modelMapper.map(userDtoOfShop, User.class);

        Cart cart = new Cart();
        cart.setUser(user);

        Cart savedCart = cartRepo.save(cart);

        return CreateCartDto.builder()
                .id(savedCart.getId())
                .userId(savedCart.getUser().getId())
                .build();
    }
    public List<CreateProductOfShopDto> getAllProductsInCart(Long cartId) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Корзина не найдена"));

        List<CreateProductOfShopDto> productDTOs = cart.getProductOfShops().stream()
                .filter(product -> product.getCart().getId().equals(cartId)) // Проверка, что продукт принадлежит указанной корзине
                .map(product -> modelMapper.map(product, CreateProductOfShopDto.class))
                .collect(Collectors.toList());

        return productDTOs;
    }
    @Override
    public void addProductToCart(Long cartId, Long productId) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Корзина не найдена"));

        ProductOfShop product = productOfShopRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Продукт не найден"));

        boolean productExistsInCart = cart.getProductOfShops().stream()
                .anyMatch(p -> p.getId().equals(productId));

        if (productExistsInCart) {
            ProductOfShop existingProduct = cart.getProductOfShops().stream()
                    .filter(p -> p.getId().equals(productId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Продукт не найден в корзине"));
            existingProduct.setQuantity(existingProduct.getQuantity() + 1);
        } else {
            cart.getProductOfShops().add(product);
        }

        recalculateTotalPrice(cart);
        cartRepo.save(cart);
    }

    public void removeProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Корзина не найдена"));

        if (cart.getProductOfShops().isEmpty()) {
            throw new IllegalStateException("Корзина пуста, нет продуктов для удаления");
        }

        cart.setProductOfShops(cart.getProductOfShops().stream()
                .filter(product -> !product.getId().equals(productId))
                .collect(Collectors.toList()));

        recalculateTotalPrice(cart);
        cartRepo.save(cart);
    }
    private void recalculateTotalPrice(Cart cart) {
        double totalPrice = cart.getProductOfShops().stream()
                .mapToDouble(product -> product.getPrice() * product.getQuantity())
                .sum();
        cart.setTotalPrice(totalPrice);
    }
    public CartDto updateCart(Long cartId, UpdateProductQuantityDto updateProductQuantityDto) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Корзина не найдена"));

        ProductOfShop productToUpdate = cart.getProductOfShops().stream()
                .filter(product -> product.getId().equals(updateProductQuantityDto.getProductId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Продукт не найден в корзине"));

        int oldQuantity = productToUpdate.getQuantity();
        int newQuantity = updateProductQuantityDto.getQuantity();
        productToUpdate.setQuantity(newQuantity);

        double totalPrice = cart.getProductOfShops().stream()
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();

        cart.setTotalPrice(totalPrice);

        Cart updatedCart = cartRepo.save(cart);

        return convertToDto(updatedCart);
    }
    public CartDto findCartById(Long cartId) {
        Optional<Cart> optionalCart = cartRepo.findById(cartId);
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            return convertToDto(cart);
        } else {
            throw new CartNotFoundException("Корзина с id " + cartId + " не найдена");
        }
    }

    private CartDto convertToDto(Cart cart) {
        return modelMapper.map(cart, CartDto.class);
    }
    private Cart convertToEntity(CartDto cartDto) {
        return modelMapper.map(cartDto, Cart.class);
    }
    
}
