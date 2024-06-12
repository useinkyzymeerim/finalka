package com.finalka.service.impl;

import com.finalka.dto.*;
import com.finalka.entity.Cart;
import com.finalka.entity.ProductOfShop;
import com.finalka.entity.User;
import com.finalka.exception.CartNotFoundException;
import com.finalka.exception.OutOfStockException;
import com.finalka.exception.ProductNotFoundException;
import com.finalka.exception.UserNotFoundException;
import com.finalka.repo.CartRepo;
import com.finalka.repo.ProductOfShopRepo;
import com.finalka.service.CartService;
import com.finalka.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartRepo repo;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final ProductOfShopRepo productOfShopRepo;

    public void createCart(CreateCartDto createCartDto) {
        try {
            UserDto userDtoOfShop = userService.getById(createCartDto.getUserId());
            if (userDtoOfShop == null) {
                throw new UserNotFoundException("Пользователь с id " + createCartDto.getUserId() + " не найден");
            }
            User user = modelMapper.map(userDtoOfShop, User.class);

            Cart cart = new Cart();
            cart.setUser(user);

            Cart savedCart = repo.save(cart);

            createCartDto.setId(savedCart.getId());
            createCartDto.setUserId(savedCart.getUser().getId());
        } catch (UserNotFoundException e) {
            log.error("Ошибка при создании корзины: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Произошла неизвестная ошибка при создании корзины: ", e);
            throw new RuntimeException("Произошла неизвестная ошибка при создании корзины", e);
        }
    }

    @Override
    @Transactional
    public void addProductToCart(Long cartId, Long productId) {
        try {
            Cart cart = repo.findById(cartId)
                    .orElseThrow(() -> new IllegalArgumentException("Корзина не найдена"));

            ProductOfShop product = productOfShopRepo.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Продукт не найден"));

            if (product.getQuantityInStock() <= 0) {
                throw new OutOfStockException("Продукт '" + product.getProductName() + "' отсутствует на складе");
            }

            boolean productExistsInCart = cart.getProductOfShops().stream()
                    .anyMatch(p -> p.getId().equals(productId));

            if (productExistsInCart) {
                ProductOfShop existingProduct = cart.getProductOfShops().stream()
                        .filter(p -> p.getId().equals(productId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Продукт не найден в корзине"));
                existingProduct.setQuantity(existingProduct.getQuantity() + 1);
            } else {
                product.setQuantity(1);
                cart.getProductOfShops().add(product);
            }
            recalculateTotalPrice(cart);
            repo.save(cart);

            log.info("Продукт '{}' успешно добавлен в корзину с ID {}", product.getProductName(), cart.getId());
        } catch (IllegalArgumentException | OutOfStockException e) {
            log.error("Ошибка при добавлении товара в корзину: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Произошла неизвестная ошибка при добавлении товара в корзину: ", e);
            throw new RuntimeException("Произошла неизвестная ошибка при добавлении товара в корзину", e);
        }
    }

    public void removeProductFromCart(Long cartId, Long productId) {
        try {
            Cart cart = repo.findById(cartId)
                    .orElseThrow(() -> new IllegalArgumentException("Корзина не найдена"));

            if (cart.getProductOfShops().isEmpty()) {
                throw new IllegalStateException("Корзина пуста, нет продуктов для удаления");
            }

            cart.setProductOfShops(cart.getProductOfShops().stream()
                    .filter(product -> !product.getId().equals(productId))
                    .collect(Collectors.toList()));

            recalculateTotalPrice(cart);
            repo.save(cart);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Ошибка при удалении товара из корзины: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Произошла неизвестная ошибка при удалении товара из корзины: ", e);
            throw new RuntimeException("Произошла неизвестная ошибка при удалении товара из корзины", e);
        }
    }

    private void recalculateTotalPrice(Cart cart) {
        double totalPrice = cart.getProductOfShops().stream()
                .mapToDouble(product -> product.getPrice() * product.getQuantity())
                .sum();
        cart.setTotalPrice(totalPrice);
    }

    public CartDetailDto updateCart(Long cartId, UpdateProductQuantityDto updateProductQuantityDto) {
        try {
            Cart cart = repo.findById(cartId)
                    .orElseThrow(() -> new CartNotFoundException("Корзина не найдена"));

            ProductOfShop productToUpdate = cart.getProductOfShops().stream()
                    .filter(product -> product.getId().equals(updateProductQuantityDto.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new ProductNotFoundException("Продукт не найден в корзине"));

            int newQuantity = updateProductQuantityDto.getQuantity();
            productToUpdate.setQuantity(newQuantity);

            double totalPrice = cart.getProductOfShops().stream()
                    .mapToDouble(p -> p.getPrice() * p.getQuantity())
                    .sum();

            cart.setTotalPrice(totalPrice);

            Cart updatedCart = repo.save(cart);

            return convertToDetailsDto(updatedCart);
        } catch (CartNotFoundException | ProductNotFoundException e) {
            log.error("Ошибка при обновлении корзины: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Произошла неизвестная ошибка при обновлении корзины: ", e);
            throw new RuntimeException("Произошла неизвестная ошибка при обновлении корзины", e);
        }
    }


    public CartDetailDto findCartById(Long cartId) {
        try {
            Optional<Cart> optionalCart = repo.findById(cartId);
            if (optionalCart.isPresent()) {
                Cart cart = optionalCart.get();
                return convertToDetailsDto(cart);
            } else {
                throw new CartNotFoundException("Корзина с id " + cartId + " не найдена");
            }
        } catch (CartNotFoundException e) {
            log.error("Ошибка при поиске корзины: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Произошла неизвестная ошибка при поиске корзины: ", e);
            throw new RuntimeException("Произошла неизвестная ошибка при поиске корзины", e);
        }
    }
    private CartDetailDto convertToDetailsDto(Cart cart) {
        return modelMapper.map(cart, CartDetailDto.class);
    }
}
