package com.finalka.repo;

import com.finalka.dto.RecipeDetailsDTO;
import com.finalka.entity.Products;

import com.finalka.entity.Recipes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipesRepo extends JpaRepository<Recipes,Long> {
    List<Recipes> findAllByDeletedAtIsNull();
    Optional<Recipes> findByDeletedAtIsNullAndId(Long id);
    @Query("SELECT r FROM Recipes r JOIN r.recipesWithProducts rp JOIN rp.product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :productName, '%'))")
    List<Recipes> findByProductNameContainingIgnoreCase(@Param("productName") String productName);
    List<Recipes> findByCreatedByAndDeletedAtIsNull(String username);
}

