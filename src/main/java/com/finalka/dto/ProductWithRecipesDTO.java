package com.finalka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductWithRecipesDTO {
    private Long id;
    private String productName;
    private List<String> recipeNames;
}