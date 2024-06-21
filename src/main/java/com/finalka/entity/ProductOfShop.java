package com.finalka.entity;

import com.finalka.enums.Units2;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "Название продукта не должно быть пустым")
    @Size(min = 1, max = 255, message = "Название продукта должно содержать от 1 до 255 символов")
    private String productName;

    @NotNull(message = "Цена не должна быть пустой")
    @DecimalMin(value = "0.0", inclusive = false, message = "Цена должна быть больше нуля")
    private Double price;

    @NotNull(message = "Количество не должно быть пустым")
    @Min(value = 0, message = "Количество должно быть не менее 0")
    private Integer quantity;

    @Enumerated(value = EnumType.STRING)
    private Units2 units2Enum;

    @NotNull(message = "Количество на складе не должно быть пустым")
    @Min(value = 0, message = "Количество на складе должно быть не менее 0")
    private Integer quantityInStock;

    @NotNull(message = "Тип не должен быть пустым")
    @Size(min = 1, max = 255, message = "Тип должен содержать от 1 до 255 символов")
    private String type;

    private boolean deleted;

    private LocalDateTime deletionTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(mappedBy = "products")
    private List<Cart> carts;

    @ManyToMany(mappedBy = "purchasedProducts")
    private List<Purchase> purchases;

    private boolean inStock;

    @Basic(fetch = FetchType.EAGER)
    @Lob
    private byte[] imageBase64;

    public void updateInStock() {
        this.inStock = this.quantityInStock > 0;
    }

    @PrePersist
    @PreUpdate
    public void preSave() {
        updateInStock();
    }
}