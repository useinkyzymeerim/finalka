package com.finalka.service;


import com.finalka.dto.*;
import com.finalka.exception.RecipeNotFoundException;
import jakarta.transaction.Transactional;

import java.util.List;

public interface RecipesService {
    RecipeDetailsDTO getRecipeWithProductsById(Long recipeId);
    List<RecipeWithProductDTO> findRecipesByProducts(String products);
    void createRecipeWithProducts(RecipeWithProductDTO recipeDTO);
    String delete(Long id);
    RecipesDto findById(Long id);
    List<RecipesDto> findAll () throws RecipeNotFoundException;
    @Transactional
    String addRecipeToMenu(RecipeAddProductDto menuRecipeRequestDto);
    void updateRecipe(RecipeUpdateDTO recipeUpdateDTO) ;
    @Transactional
    List<RecipesDto> findAllByChef(String chefUsername);
    void removeProductFromRecipe(Long recipeId, Long productId);
}