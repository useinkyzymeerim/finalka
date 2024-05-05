package com.finalka.service;


import com.finalka.dto.RecipeDetailsDTO;
import com.finalka.dto.RecipeWithProductDTO;
import com.finalka.dto.RecipesDto;

import java.util.List;

public interface RecipesService {
    List<RecipeDetailsDTO> findRecipeDetails(Long recipeId);
    List<RecipesDto> findByProduct(String productName);
    List<RecipesDto> findByRecipeNameIgnoreCase(String recipeName);
    RecipeWithProductDTO createRecipeWithProducts(RecipeWithProductDTO recipeDTO);
    String delete(Long id);
    RecipesDto findById(Long id);
    List<RecipesDto> findAll () throws Exception;
    RecipesDto update(RecipesDto recipesDto);

}

