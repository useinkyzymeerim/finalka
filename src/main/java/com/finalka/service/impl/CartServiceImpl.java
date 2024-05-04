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

    // Обновленный метод сервиса для создания корзины
    public CreateCartDto createCart(CreateCartDto createCartDto) {
        // Получение пользователя по его идентификатору из базы данных
        UserDto userDtoOfShop = userService.getById(createCartDto.getUserId());
        User user = modelMapper.map(userDtoOfShop, User.class);

        // Создание пустой корзины для пользователя
        Cart cart = new Cart();
        cart.setUser(user); // Установка пользователя для корзины

        // Сохранение корзины в базу данных
        Cart savedCart = cartRepo.save(cart);

        // Возвращение данных о созданной корзине
        return CreateCartDto.builder()
                .id(savedCart.getId())
                .userId(savedCart.getUser().getId())
                .build();
    }
    @Override
    public void addProductToCart(Long cartId, Long productId) {
        // Получаем корзину по её ID
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Корзина не найдена"));

        // Получаем продукт по его ID
        ProductOfShop product = productOfShopRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Продукт не найден"));

        // Проверяем, есть ли уже такой продукт в корзине
        boolean productExistsInCart = cart.getProductOfShops().stream()
                .anyMatch(p -> p.getId().equals(productId));

        // Если продукт уже есть в корзине, увеличиваем его количество
        if (productExistsInCart) {
            ProductOfShop existingProduct = cart.getProductOfShops().stream()
                    .filter(p -> p.getId().equals(productId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Продукт не найден в корзине"));
            existingProduct.setQuantity(existingProduct.getQuantity() + 1);
        } else {
            // Если продукта нет в корзине, добавляем его
            cart.getProductOfShops().add(product);
        }

        // Пересчитываем общую стоимость корзины
        recalculateTotalPrice(cart);

        // Сохраняем изменения в корзине
        cartRepo.save(cart);
    }
    private void recalculateTotalPrice(Cart cart) {
        double totalPrice = cart.getProductOfShops().stream()
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();
        cart.setTotalPrice(totalPrice);
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
    public void removeProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Корзина не найдена"));

        if (cart.getProductOfShops().isEmpty()) {
            throw new IllegalStateException("Корзина пуста, нет продуктов для удаления");
        }

        cart.setProductOfShops(cart.getProductOfShops().stream()
                .filter(product -> !product.getId().equals(productId))
                .collect(Collectors.toList()));

        recalculateTotalPriceAfterRemove(cart);
        cartRepo.save(cart);
    }
    private void recalculateTotalPriceAfterRemove(Cart cart) {
        double totalPrice = cart.getProductOfShops().stream()
                .mapToDouble(product -> product.getPrice() * product.getQuantity())
                .sum();
        cart.setTotalPrice(totalPrice);
    }
    public CartDto updateCart(Long cartId, UpdateProductQuantityDto updateProductQuantityDto) {
        // Проверяем существование корзины
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Корзина не найдена"));

        // Находим продукт по его идентификатору в корзине
        ProductOfShop productToUpdate = cart.getProductOfShops().stream()
                .filter(product -> product.getId().equals(updateProductQuantityDto.getProductId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Продукт не найден в корзине"));

        // Обновляем количество продукта
        int oldQuantity = productToUpdate.getQuantity(); // Старое количество продукта
        int newQuantity = updateProductQuantityDto.getQuantity(); // Новое количество продукта
        productToUpdate.setQuantity(newQuantity);

        // Пересчитываем общую сумму продуктов в корзине
        double totalPrice = cart.getProductOfShops().stream()
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();

        // Устанавливаем новую общую сумму продуктов
        cart.setTotalPrice(totalPrice);

        // Сохраняем обновленную корзину в базе данных
        Cart updatedCart = cartRepo.save(cart);

        // Конвертируем и возвращаем обновленную корзину в виде DTO
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
