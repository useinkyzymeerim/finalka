package com.finalka.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_seq_generator")
    @SequenceGenerator(name = "card_seq_generator", sequenceName = "card_seq", allocationSize = 1)
    private Long id;

    @NotBlank(message = "Номер карты не должен быть пустым")
    @Size(min = 16, max = 16, message = "Номер карты должен содержать 16 цифр")
    private String cardNumber;

    @NotBlank(message = "Имя владельца карты не должно быть пустым")
    @Size(min = 1, max = 255, message = "Имя владельца карты должно содержать от 1 до 255 символов")
    private String cardHolderName;

    @NotBlank(message = "Дата истечения срока действия карты не должна быть пустой")
    private String expiryDate;

    @NotBlank(message = "CVV не должен быть пустым")
    @Pattern(regexp = "\\d{3}", message = "CVV должен содержать 3 цифры")
    private String cvv;

    private boolean active;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "bank_account_id")
    private BankAccount bankAccount;
}