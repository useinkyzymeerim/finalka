package com.finalka.service;


import com.finalka.dto.ProductDTO;
import com.finalka.dto.RecipeDetailsDTO;
import com.finalka.dto.RecipeWithProductDTO;
import com.finalka.dto.RecipesDto;
import com.finalka.entity.Products;
import com.finalka.entity.Recipes;

import java.util.List;

public interface RecipesService {
    List<RecipeDetailsDTO> findRecipeDetails(Long recipeId);

    List<RecipeWithProductDTO> findRecipesByProducts(List<String> userProducts);

    List<RecipesDto> findByRecipeNameIgnoreCase(String recipeName);
    RecipeWithProductDTO createRecipeWithProducts(RecipeWithProductDTO recipeDTO);
    String delete(Long id);
    RecipesDto findById(Long id);
    List<RecipesDto> findAll () throws Exception;
    RecipesDto update(RecipesDto recipesDto);

}

