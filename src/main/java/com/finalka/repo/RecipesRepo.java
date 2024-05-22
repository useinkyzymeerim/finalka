package com.finalka.repo;

import com.finalka.dto.RecipeDetailsDTO;
import com.finalka.entity.Products;
import com.finalka.entity.Recipes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipesRepo extends JpaRepository<Recipes,Long> {
    Recipes findByNameOfFood(String nameOfFood);
    @Query("SELECT new com.finalka.dto.RecipeDetailsDTO( r.nameOfFood,  p.productName, rp.quantityOfProduct) " +
            "FROM Recipes r " +
            "JOIN r.recipesWithProducts rp " +
            "JOIN rp.product p " +
            "WHERE r.id = :recipeId")
    List<RecipeDetailsDTO> findRecipeDetails(@Param("recipeId") Long recipeId);
    List<Recipes> findByNameOfFoodIgnoreCase(String recipeName);

    List<Recipes> findAllByDeletedAtIsNull();
    Recipes findByDeletedAtIsNullAndId(Long id);
    List<Recipes> findByRecipesWithProducts_Product_ProductNameIn(List<String> productNames);
    List<Recipes> findByMenu_Id(Long menuId);
}

