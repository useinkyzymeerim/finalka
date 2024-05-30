package com.finalka.dto;

import com.finalka.entity.Menu;
import com.finalka.entity.User;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

import jakarta.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeWithProductDTO {
    private Long id;

    @NotBlank(message = "Name of food must not be blank")
    private String nameOfFood;

    @NotBlank(message = "Description must not be blank")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private byte[] imageBase64;

    private String linkOfVideo;

    @NotNull(message = "Quantity of product must not be null")
    @Min(value = 1, message = "Quantity of product must be at least 1")
    private Integer quantityOfProduct;

    @NotNull(message = "Cooking time must not be null")
    @Min(value = 1, message = "Cooking time must be at least 1 minute")
    private Integer cookingTime;
    private List<ProductDTO> products;

    private String createdBy;

    private Timestamp createdAt;

    private String lastUpdatedBy;
    private Timestamp lastUpdatedAt;
    private String deletedBy;
    private Timestamp deletedAt;
}