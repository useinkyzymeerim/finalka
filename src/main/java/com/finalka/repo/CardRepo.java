package com.finalka.repo;

import com.finalka.entity.Card;
import com.finalka.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepo extends JpaRepository<Card, Long> {
    Optional<Card> findByCardNumberAndCardHolderNameAndExpiryDateAndCvv(String cardNumber, String cardHolderName, String expiryDate, String cvv);
    Optional<Card> findByUserAndActiveTrue(User user);
    List<Card> findByUser(User user);
    Optional<Card> findByIdAndUser(Long id, User user);
}
