package com.finalka.dto;

import lombok.Data;

@Data
public class RecipeWithProductDTO {
    private Long recipeId;
    private String recipeName;
    private Integer quantityOfProduct;
}