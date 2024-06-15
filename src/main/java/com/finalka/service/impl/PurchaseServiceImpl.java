package com.finalka.service.impl;

import com.finalka.dto.PurchaseDetailsDto;
import com.finalka.entity.*;
import com.finalka.mapper.PurchaseMapper;
import com.finalka.repo.*;
import com.finalka.service.ProductOfShopService;
import com.finalka.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {
    private final CartRepo cartRepository;
    private final ProductOfShopService productOfShopService;
    private final PurchaseRepo repo;
    private final PurchaseMapper purchaseMapper;
    private final ProductOfShopRepo productOfShopRepo;
    private final CardRepo cardRepository;
    private final BankAccountRepo bankAccountRepository;

    @Override
    @Transactional
    public void purchaseProductsFromCart(Long cartId) {
        try {
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new IllegalArgumentException("Корзина не найдена"));
            User user = cart.getUser();

            if (cart.getProducts().isEmpty()) {
                throw new IllegalStateException("Корзина пуста. Нечего покупать.");
            }

            Card card = cardRepository.findByUserAndActiveTrue(user)
                    .orElseThrow(() -> new IllegalStateException("Нет активной привязанной карты"));

            BankAccount bankAccount = card.getBankAccount();
            if (bankAccount == null) {
                throw new IllegalStateException("Карта не связана с банковским счетом");
            }

            double totalPrice = cart.getTotalPrice();

            if (bankAccount.getBalance().compareTo(BigDecimal.valueOf(totalPrice)) < 0) {
                throw new IllegalStateException("Недостаточно средств на банковском счете");
            }

            for (ProductOfShop cartProduct : cart.getProducts()) {
                if (cartProduct.getQuantity() > cartProduct.getQuantityInStock()) {
                    throw new IllegalStateException("Недостаточное количество продукта \"" + cartProduct.getProductName() + "\" в наличии на складе");
                }
            }

            bankAccount.setBalance(bankAccount.getBalance().subtract(BigDecimal.valueOf(totalPrice)));
            bankAccountRepository.save(bankAccount);

            Purchase purchase = new Purchase();
            purchase.setUser(user);
            purchase.setCart(cart);
            purchase.setPurchaseDate(new Date());
            purchase.setTotalPrice(totalPrice);

            List<ProductOfShop> purchasedProducts = new ArrayList<>();
            for (ProductOfShop cartProduct : cart.getProducts()) {
                productOfShopService.decreaseProductQuantityInStock(cartProduct.getId(), cartProduct.getQuantity());
                purchasedProducts.add(cartProduct);
            }

            purchase.setPurchasedProducts(purchasedProducts);
            purchase = repo.save(purchase);

            for (ProductOfShop product : purchasedProducts) {
                product.getPurchases().add(purchase);
                productOfShopRepo.save(product);
            }

            cart.getProducts().clear();
            cart.setTotalPrice(0.0);
            cartRepository.save(cart);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            throw new ServiceException("Ошибка при выполнении покупки: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new ServiceException("Ошибка при выполнении покупки.", ex);
        }
    }

    @Transactional(readOnly = true)
    public PurchaseDetailsDto getPurchaseWithProducts(Long purchaseId) {
        try {
            Purchase purchase = repo.findByIdWithProducts(purchaseId);
            return purchaseMapper.toDetailsDto(purchase);
        } catch (Exception ex) {
            throw new ServiceException("Ошибка при получении информации о покупке", ex);
        }
    }
}