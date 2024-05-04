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
public class PurchaseDTO {
    private Long id;
    private double totalPrice;
    private Date purchaseDate;
    private Long userId;
    private Long cartId;
}
