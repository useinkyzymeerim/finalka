package com.finalka.dto;

import com.finalka.enums.Units2;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductOfShopDetailsDto {
    private String productName;
    private Integer quantity;
    private Units2 units2Enum;
    private Double price;
}
