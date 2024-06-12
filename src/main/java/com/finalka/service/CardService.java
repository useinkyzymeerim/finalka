package com.finalka.service;

public interface CardService {
    String linkCardToUser(String cardNumber, String cardHolderName, String expiryDate, String cvv);
}
