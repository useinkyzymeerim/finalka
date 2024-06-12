package com.finalka.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CardLinkRequest {
    @NotBlank(message = "Номер карты не должен быть пустым")
    @Size(min = 16, max = 16, message = "Номер карты должен содержать 16 цифр")
    private String cardNumber;
    @NotBlank(message = "Имя держателя карты не должно быть пустым")
    private String cardHolderName;
    private String expiryDate;
    @NotBlank(message = "CVV не должен быть пустым")
    @Pattern(regexp = "\\d{3}", message = "CVV должен содержать 3 цифры")
    private String cvv;
}
