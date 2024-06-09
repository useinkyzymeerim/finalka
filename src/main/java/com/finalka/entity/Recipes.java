package com.finalka.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Recipes {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY, generator = "recipes_seq_generator")
    @SequenceGenerator(name = "recipes_seq_generator", sequenceName = "recipes_seq", allocationSize = 1)
    private Long id;

    @NotNull(message = "Название блюда не должно быть пустым")
    @Size(min = 1, max = 255, message = "Название блюда должно содержать от 1 до 255 символов")
    private String nameOfFood;

    @NotNull(message = "Описание не должно быть пустым")
    @Size(min = 1, message = "Описание должно содержать хотя бы один символ")
    @Column(length = 2000)
    private String description;
    @NotNull(message = "Поле порции не должно быть пустым" )
    private Integer portion;
    @Basic(fetch = FetchType.EAGER)
    @Lob
    private byte[] imageBase64;

    @URL(message = "Ссылка на видео должна быть корректной URL")
    private String linkOfVideo;

    @NotNull(message = "Количество продукта не должно быть пустым")
    @Min(value = 1, message = "Количество продукта должно быть не менее 1")
    private Integer quantityOfProduct;

    @NotNull(message = "Время приготовления не должно быть пустым")
    @Min(value = 1, message = "Время приготовления должно быть не менее 1 минуты")
    private Integer cookingTime;

    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY)
    private Set<RecipesWithProducts> recipesWithProducts;

    @ManyToMany(mappedBy = "recipes")
    private List<Menu> menus;

    @ManyToOne
    private User user;

    @Size(max = 255, message = "Создатель не должен содержать больше 255 символов")
    private String createdBy;
    private Timestamp createdAt;

    @Size(max = 255, message = "Обновляющий не должен содержать больше 255 символов")
    private String lastUpdatedBy;
    private Timestamp lastUpdatedAt;

    @Size(max = 255, message = "Удаляющий не должен содержать больше 255 символов")
    private String deletedBy;
    private Timestamp deletedAt;
}

