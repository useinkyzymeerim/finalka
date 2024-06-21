package com.finalka.dto;

import com.finalka.enums.Units2;
import jakarta.persistence.ElementCollection;
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
public class ProductOfShopDto {
    private Long id;
    private String productName;
    private Double price;
    private Integer quantity;
    private Units2 units2Enum;
    private Integer quantityInStock;
    private String type;
    private byte[] imageBase64;
}
