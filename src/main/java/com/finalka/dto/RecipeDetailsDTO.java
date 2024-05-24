package com.finalka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class RecipeDetailsDTO {
    private String recipeName;
    private List<ProductDetailsDto> productDetailsDtos;
    private Integer quantityOfProduct;
}