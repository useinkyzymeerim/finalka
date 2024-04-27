package com.finalka.dto;

import lombok.Data;

import java.util.List;

@Data
public class MenuWithRecipeDTO {
    private Long menuId;
    private String nameOfMenu;
    private List<RecipesDto> recipes;
}
