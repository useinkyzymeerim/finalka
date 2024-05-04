package com.finalka.entity;

import com.finalka.enums.Units2;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

    @Entity
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class ProductOfShop {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_of_shop_seq_generator")
        @SequenceGenerator(name = "product_of_shop_seq_generator", sequenceName = "product_of_shop_seq", allocationSize = 1)
        private Long id;
        private String productName;
        private Double price;
        private Integer quantity;
        private Units2 units2Enum;
        private Integer quantityInStock;
        private String type;

        private boolean deleted; // Добавленное поле для обозначения удаленных продуктов
        private LocalDateTime deletionTime; // Время удаления продукта

        @ManyToOne
        @JoinColumn(name = "user_id")
        private User user;

        @ManyToOne
        @JoinColumn(name = "cart_id")
        private Cart cart;

        private boolean purchased; // Показывает, был ли этот продукт куплен пользователем
        private int quantityPurchased; // Количество этого продукта, купленного пользователем
        private Date purchaseDate; // Дата покупки продукта

        private boolean inStock; // Дополнительное поле для отслеживания наличия на складе



    }
