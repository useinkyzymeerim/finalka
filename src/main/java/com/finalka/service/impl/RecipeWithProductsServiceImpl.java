package com.finalka.service.impl;

import com.finalka.entity.RecipesWithProducts;
import com.finalka.repo.RecipesWithProductsRepo;
import com.finalka.service.RecipesWithProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeWithProductsServiceImpl implements RecipesWithProductsService {
    private final RecipesWithProductsRepo recipesWithProductsRepo;
    @Override
    public RecipesWithProducts findById(Long id) {
        return recipesWithProductsRepo.findById(id).orElse(null);
    }
    @Override
    public List<RecipesWithProducts> findAll () {
        return recipesWithProductsRepo.findAll();
    }

}