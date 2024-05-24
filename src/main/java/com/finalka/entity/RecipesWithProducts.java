package com.finalka.entity;

import com.finalka.enums.Units;
import jakarta.persistence.*;
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
    private Recipes recipe;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Products product;

    private Integer quantityOfProduct;
    @Enumerated(value = EnumType.STRING)
    private Units unitsEnum;


}

