package com.finalka.entity;

import com.finalka.enums.Units;
import jakarta.persistence.*;
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
    private String productName;

    private Integer quantity;
    @Enumerated(value = EnumType.STRING)
    private Units unitsEnum;


    private String createdBy;
    private Timestamp createdAt;

    private String lastUpdatedBy;
    private Timestamp lastUpdatedAt;

    private String deletedBy;
    private Timestamp deletedAt;
}
