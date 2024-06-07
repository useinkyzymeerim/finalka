package com.finalka.repo;

import com.finalka.entity.Products;
import com.finalka.entity.Recipes;
import com.finalka.entity.RecipesWithProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipesWithProductsRepo extends JpaRepository<RecipesWithProducts,Long> {
    List<RecipesWithProducts> findByRecipe_Menus_Id(Long menuId);
    List<RecipesWithProducts> findByRecipe(Recipes recipe);
    Optional<RecipesWithProducts> findByRecipeAndProduct(Recipes recipe, Products product);

}