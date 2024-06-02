package com.finalka.entity;

import com.finalka.enums.Units;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipesWithProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recipes_with_products_seq_generator")
    @SequenceGenerator(name = "recipes_with_products_seq_generator", sequenceName = "recipes_with_products_seq", allocationSize = 1)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "recipe_id")
    @NotNull(message = "Рецепт не должен быть пустым")
    private Recipes recipe;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @NotNull(message = "Продукт не должен быть пустым")
    private Products product;

    @NotNull(message = "Количество продукта не должно быть пустым")
    @Min(value = 1, message = "Количество продукта должно быть не менее 1")
    @Max(value = 1000, message = "Количество продукта должно быть не более 1000")
    private Integer quantityOfProduct;

    @Enumerated(value = EnumType.STRING)
    @NotNull(message = "Единица измерения не должна быть пустой")
    private Units unitsEnum;
}

