package com.finalka.entity;

import com.finalka.enums.Units;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "productForRecipes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_seq_generator")
    @SequenceGenerator(name = "products_seq_generator", sequenceName = "products_seq", allocationSize = 1)
    private Long id;

    @NotNull(message = "Название продукта не должно быть пустым")
    @Size(min = 1, max = 255, message = "Название продукта должно содержать от 1 до 255 символов")
    private String productName;

    @NotNull(message = "Количество не должно быть пустым")
    @Min(value = 1, message = "Количество должно быть не менее 1")
    private Integer quantity;

    @Enumerated(value = EnumType.STRING)
    private Units unitsEnum;

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
