package com.finalka.dto;

import com.finalka.enums.Units;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDetailsDto {
    private String productName;
    private Integer quantity;
    private Units unitsEnum;
}
