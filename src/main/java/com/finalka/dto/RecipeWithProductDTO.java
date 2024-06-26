package com.finalka.dto;

import com.finalka.entity.Menu;
import com.finalka.entity.User;
import com.finalka.enums.Categories;
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

    @NotBlank(message = "Название блюда не должно быть пустым")
    private String nameOfFood;

    @NotBlank(message = "Описание блюда не должно быть пустым")
    @Size(max = 2000, message = "Описание не должно превышать 2000 символов")
    private String description;
    @NotNull(message = "Поле порции не должно быть пустым" )
    private Integer portion;

    @NotNull(message = "Категория не должна быть пустой")
    private Categories categories;

    private byte[] imageBase64;

    private String linkOfVideo;

    @NotNull(message = "Количество продуктов не должно быть пустым")
    @Min(value = 1, message = "Количество продукта должно быть не менее 1")
    private Integer quantityOfProduct;

    @NotNull(message = "Время приготовления не должно быть пустым")
    @Min(value = 1, message = "Время приготовления должно составлять не менее 1 минуты")
    private Integer cookingTime;
    private List<ProductDTO> products;

    private String createdBy;

    private Timestamp createdAt;

    private String lastUpdatedBy;
    private Timestamp lastUpdatedAt;
    private String deletedBy;
    private Timestamp deletedAt;
}