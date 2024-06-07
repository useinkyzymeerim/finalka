package com.finalka.service;


import com.finalka.dto.*;
import com.finalka.entity.Products;
import com.finalka.entity.Recipes;
import jakarta.transaction.Transactional;

import java.util.List;

public interface RecipesService {
    RecipeDetailsDTO getRecipeWithProductsById(Long recipeId);
    List<RecipeWithProductDTO> findRecipesByProducts(List<String> userProducts);
    void createRecipeWithProducts(RecipeWithProductDTO recipeDTO);
    String delete(Long id);
    RecipesDto findById(Long id);
    List<RecipesDto> findAll () throws Exception;
    RecipeUpdateDTO updateRecipe(RecipeUpdateDTO recipeUpdateDTO) ;

    @Transactional
    List<RecipesDto> findAllByChef(String chefUsername);

    void addRecipeToMenu(Long menuId, Long recipeId);
    void removeProductFromRecipe(Long recipeId, Long productId);
}

