package com.finalka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeAddProductDto {
    private Long recipeId;
    private Long menuId;
}
