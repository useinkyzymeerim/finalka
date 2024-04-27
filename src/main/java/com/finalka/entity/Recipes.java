package com.finalka.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;
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
    private Date removeDate;

    @OneToMany(mappedBy = "recipe")
    private Set<RecipesWithProducts> recipesWithProducts;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String createdBy;
    private Timestamp createdAt;

    private String lastUpdatedBy;
    private Timestamp lastUpdatedAt;

    private String deletedBy;
    private Timestamp deletedAt;
}
