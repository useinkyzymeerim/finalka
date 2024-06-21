package com.finalka.dto;

import com.finalka.enums.Categories;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class RecipeDetailsDTO {
    private String recipeName;
    private String description;
    private Integer portion;
    @Enumerated(EnumType.STRING)
    private Categories categories;
    private String linkOfVideo;
    private Integer cookingTime;
    private List<ProductDetailsDto> productDetailsDtos;
    private Integer quantityOfProduct;
}