package com.finalka.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "menu_seq_generator")
    @SequenceGenerator(name = "menu_seq_generator", sequenceName = "menu_seq", allocationSize = 1)
    private Long id;

    @NotNull(message = "Название меню не должно быть пустым")
    @Size(min = 1, max = 255, message = "Название меню должно содержать от 1 до 255 символов")
    private String nameOfMenu;

    @ManyToMany
    @JoinTable(
            name = "menu_recipe",
            joinColumns = @JoinColumn(name = "menu_id"),
            inverseJoinColumns = @JoinColumn(name = "recipe_id")
    )
    private List<Recipes> recipes;

    @NotNull(message = "Создатель не должен быть пустым")
    @Size(min = 1, max = 255, message = "Создатель должен содержать от 1 до 255 символов")
    private String createdBy;

    @NotNull(message = "Дата создания не должна быть пустой")
    private Timestamp createdAt;

    @Size(max = 255, message = "Обновивший пользователь должен содержать не более 255 символов")
    private String lastUpdatedBy;

    private Timestamp lastUpdatedAt;

    @Size(max = 255, message = "Удаливший пользователь должен содержать не более 255 символов")
    private String deletedBy;

    private Timestamp deletedAt;
}
