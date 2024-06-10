package com.finalka.service.impl;

import com.finalka.dto.ProductOfShopDto;
import com.finalka.dto.PurchaseDTO;
import com.finalka.dto.PurchaseDetailsDto;
import com.finalka.entity.Cart;
import com.finalka.entity.ProductOfShop;
import com.finalka.entity.Purchase;
import com.finalka.entity.User;
import com.finalka.mapper.PurchaseMapper;
import com.finalka.repo.CartRepo;
import com.finalka.repo.ProductOfShopRepo;
import com.finalka.repo.PurchaseRepo;
import com.finalka.repo.UserRepo;
import com.finalka.service.ProductOfShopService;
import com.finalka.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
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
    private final PurchaseRepo repo;
    private final PurchaseMapper purchaseMapper;
    private final ProductOfShopRepo productOfShopRepo;

    @Override
    @Transactional
    public void purchaseProductsFromCart(Long cartId) {
        try {
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

            Purchase purchase = new Purchase();
            purchase.setUser(user);
            purchase.setCart(cart);
            purchase.setPurchaseDate(new Date());

            double totalPrice = cart.getTotalPrice();
            purchase.setTotalPrice(totalPrice);

            List<ProductOfShop> purchasedProducts = new ArrayList<>();
            for (ProductOfShop cartProduct : cart.getProductOfShops()) {

                productOfShopService.decreaseProductQuantityInStock(cartProduct.getId(), cartProduct.getQuantity());
                purchasedProducts.add(cartProduct);
            }

            purchase.setPurchasedProducts(purchasedProducts);
            purchase = repo.save(purchase);

            for (ProductOfShop product : purchasedProducts) {
                product.getPurchases().add(purchase);
                productOfShopRepo.save(product);
            }

            cart.getProductOfShops().clear();
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