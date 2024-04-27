package com.finalka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecipeDetailsDTO {

    private String recipeName;
    private String productName;
    private Integer quantityOfProduct;
}