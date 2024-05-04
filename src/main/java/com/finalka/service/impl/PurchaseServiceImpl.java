package com.finalka.service.impl;

import com.finalka.dto.ProductOfShopDto;
import com.finalka.dto.PurchaseDTO;
import com.finalka.entity.Cart;
import com.finalka.entity.ProductOfShop;
import com.finalka.entity.Purchase;
import com.finalka.entity.User;
import com.finalka.repo.CartRepo;
import com.finalka.repo.ProductOfShopRepo;
import com.finalka.repo.PurchaseRepo;
import com.finalka.repo.UserRepo;
import com.finalka.service.ProductOfShopService;
import com.finalka.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class PurchaseServiceImpl implements PurchaseService {
    private final CartRepo cartRepository;
    private final ProductOfShopService productOfShopService;
    private final PurchaseRepo purchaseRepository;

    @Override
    @Transactional
        public void purchaseProductsFromCart(Long cartId) {

            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new IllegalArgumentException("Корзина не найдена"));
            User user = cart.getUser();

            if (cart.getProductOfShops().isEmpty()) {
                throw new IllegalStateException("Корзина пуста. Нечего покупать.");
            }
            for (ProductOfShop cartProduct : cart.getProductOfShops()) {
                if (cartProduct.getQuantity() > cartProduct.getQuantityInStock()) {
                    throw new IllegalStateException("Недостаточное количество продукта \"" + cartProduct.getProductName() + "\" в наличии на складе");
                }
            }

            // Создаем новую запись о покупке
            Purchase purchase = new Purchase();
            purchase.setUser(user);
            purchase.setCart(cart);
            purchase.setPurchaseDate(new Date());

            // Вычисляем общую стоимость покупки
            double totalPrice = cart.getTotalPrice();
            purchase.setTotalPrice(totalPrice);

            // Сохраняем запись о покупке в базе данных
            purchase = purchaseRepository.save(purchase);

            // Уменьшаем количество товара в складе и обновляем флаги приобретения
            for (ProductOfShop purchasedProduct : cart.getProductOfShops()) {
                productOfShopService.decreaseProductQuantityInStock(purchasedProduct.getId(), purchasedProduct.getQuantity());
                purchasedProduct.setPurchased(true);
                purchasedProduct.setQuantityPurchased(purchasedProduct.getQuantity());
                purchasedProduct.setPurchaseDate(new Date());
            }

            // Очищаем корзину после покупки
            cart.getProductOfShops().clear();
            cart.setTotalPrice(0.0);
            cartRepository.save(cart);
        }

}