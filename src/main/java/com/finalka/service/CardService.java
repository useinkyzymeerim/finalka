package com.finalka.service;

import com.finalka.dto.CardDto;

import java.util.List;

public interface CardService {
    String linkCardToUser(String cardNumber, String cardHolderName, String expiryDate, String cvv);
    List<CardDto> getLinkedCards();
    String unlinkCard(Long cardId);
}
