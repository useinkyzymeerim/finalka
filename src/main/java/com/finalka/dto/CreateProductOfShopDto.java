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
    public class CreateProductOfShopDto {
        private Long id;
        private String productName;
        private Double price;
        private Integer quantity;
        private Units2 units2Enum;
        private String type;
        private Integer quantityInStock;
    }
