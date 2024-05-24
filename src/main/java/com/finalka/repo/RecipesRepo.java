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
    @Query("SELECT new com.finalka.dto.RecipeDetailsDTO( r.nameOfFood, p.productName, rp.quantityOfProduct) " +
            "FROM Recipes r " +
            "JOIN r.recipesWithProducts rp " +
            "JOIN rp.product p " +
            "WHERE r.id = :recipeId " +
            "AND r.deletedAt IS NULL " +
            "AND p.deletedAt IS NULL")
    List<RecipeDetailsDTO> findRecipeDetails(@Param("recipeId") Long recipeId);
    List<Recipes> findAllByDeletedAtIsNull();
    Recipes findByDeletedAtIsNullAndId(Long id);
    @Query("SELECT r FROM Recipes r JOIN r.recipesWithProducts rp JOIN rp.product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :productName, '%'))")
    List<Recipes> findByProductNameContainingIgnoreCase(@Param("productName") String productName);
}

