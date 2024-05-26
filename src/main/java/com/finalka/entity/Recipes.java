package com.finalka.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bouncycastle.bcpg.S2K;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Recipes {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recipes_seq_generator")
    @SequenceGenerator(name = "recipes_seq_generator", sequenceName = "recipes_seq", allocationSize = 1)
    private Long id;
    private String nameOfFood;
    private String description;
    @Basic(fetch=FetchType.EAGER)
    @Lob
    private byte[] imageBase64;
    private String linkOfVideo;
    private Integer quantityOfProduct;
    private Integer cookingTime;

    @OneToMany(mappedBy = "recipe",fetch = FetchType.LAZY)
    private List<RecipesWithProducts> recipesWithProducts;

    @ManyToMany(mappedBy = "recipes")
    private List<Menu> menus;

    @ManyToOne
    private User user;

    private String createdBy;
    private Timestamp createdAt;

    private String lastUpdatedBy;
    private Timestamp lastUpdatedAt;

    private String deletedBy;
    private Timestamp deletedAt;
}
