package com.finalka.repo;

import com.finalka.entity.RecipesWithProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipesWithProductsRepo extends JpaRepository<RecipesWithProducts,Long> {
    List<RecipesWithProducts> findByRecipe_Menus_Id(Long menuId);
}