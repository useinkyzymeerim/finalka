package com.finalka.dto;

import lombok.Data;

@Data
public class CardDto {
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private boolean active;

    public CardDto(String cardNumber, String cardHolderName, String expiryDate, boolean active) {
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
        this.active = active;
    }
}