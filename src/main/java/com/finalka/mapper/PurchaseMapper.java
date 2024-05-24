package com.finalka.mapper;

import com.finalka.dto.ProductOfShopDetailsDto;
import com.finalka.dto.PurchaseDetailsDto;
import com.finalka.entity.ProductOfShop;
import com.finalka.entity.Purchase;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class PurchaseMapper {
    public PurchaseDetailsDto toDetailsDto(Purchase purchase) {
        if (purchase == null) {
            return null;
        }

        List<ProductOfShopDetailsDto> productDTOs = purchase.getPurchasedProducts().stream()
                .map(this::toProductDetailsDto)
                .collect(Collectors.toList());

        return PurchaseDetailsDto.builder()
                .totalPrice(purchase.getTotalPrice())
                .purchaseDate(purchase.getPurchaseDate())
                .userId(purchase.getUser().getId())
                .product(productDTOs)
                .build();
    }

    private ProductOfShopDetailsDto toProductDetailsDto(ProductOfShop product) {
        return ProductOfShopDetailsDto.builder()
                .productName(product.getProductName())
                .quantity(product.getQuantity())
                .units2Enum(product.getUnits2Enum())
                .price(product.getPrice())
                .build();
    }
}
