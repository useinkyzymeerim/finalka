package com.finalka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseDetailsDto {
    private double totalPrice;
    private Date purchaseDate;
    private Long userId;
    private List<ProductOfShopDetailsDto> product;
    private int quantityPurchased;

}
