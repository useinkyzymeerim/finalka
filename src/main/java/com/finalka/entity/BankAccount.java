package com.finalka.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bank_account_seq_generator")
    @SequenceGenerator(name = "bank_account_seq_generator", sequenceName = "bank_account_seq", allocationSize = 1)
    private Long id;

    @NotNull(message = "Номер банковского счета не должен быть пустым")
    @Size(min = 1, max = 255, message = "Номер банковского счета должен содержать от 1 до 255 символов")
    private String accountNumber;

    @NotNull(message = "Баланс не должен быть пустым")
    @DecimalMin(value = "0.0", message = "Баланс не может быть отрицательным")
    private BigDecimal balance;

    @OneToMany(mappedBy = "bankAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Card> cards = new HashSet<>();
}