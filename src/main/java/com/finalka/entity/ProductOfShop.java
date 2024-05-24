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

    @Enumerated(value = EnumType.STRING)
    private Units2 units2Enum;
    private Integer quantityInStock;
    private String type;
    private boolean deleted;
    private LocalDateTime deletionTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToMany(mappedBy = "purchasedProducts")
    private List<Purchase> purchases;

    private boolean inStock;
    public void updateInStock() {
        this.inStock = this.quantityInStock > 0;
    }

    @PrePersist
    @PreUpdate
    public void preSave() {
        updateInStock();
    }
}
