package com.finalka.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

    @Entity
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Cart {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_seq_generator")
        @SequenceGenerator(name = "cart_seq_generator", sequenceName = "cart_seq", allocationSize = 1)
        private Long id;

        private Double totalPrice;

        @OneToMany(cascade = CascadeType.ALL)
        @JoinColumn(name = "cart_id")
        private List<ProductOfShop> productOfShops;

        @OneToOne
        @JoinColumn(name = "user_id")
        private User user;

        @OneToMany(mappedBy = "cart")
        private List<Purchase> purchases;
    }
