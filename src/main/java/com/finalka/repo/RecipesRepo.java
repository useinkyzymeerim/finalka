package com.finalka.repo;

import com.finalka.dto.RecipeDetailsDTO;
import com.finalka.entity.Products;

import com.finalka.entity.Recipes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @Query("SELECT r FROM Recipes r JOIN r.recipesWithProducts rp JOIN rp.product p " +
            "WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :productName, '%')) " +
            "AND (r.deletedAt IS NULL OR r.deletedAt > CURRENT_TIMESTAMP)")
    List<Recipes> findByProductNameContainingIgnoreCaseAndDeletedAtIsNull(@Param("productName") String productName);
    List<Recipes> findByCreatedByAndDeletedAtIsNull(String username);

    Page<Recipes> findByNameOfFoodContainingIgnoreCase(String name, Pageable pageable);

}

