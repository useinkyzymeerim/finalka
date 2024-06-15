package com.finalka.service.impl;

import com.finalka.dto.CardDto;
import com.finalka.entity.Card;
import com.finalka.entity.User;
import com.finalka.repo.CardRepo;
import com.finalka.repo.UserRepo;
import com.finalka.service.CardService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final CardRepo cardRepo;
    private final UserRepo userRepo;

    @Transactional
    public String linkCardToUser(String cardNumber, String cardHolderName, String expiryDate, String cvv) {
        cardHolderName = cardHolderName.trim();
        expiryDate = expiryDate.trim();
        cvv = cvv.trim();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        Card card = cardRepo.findByCardNumberAndCardHolderNameAndExpiryDateAndCvv(cardNumber, cardHolderName, expiryDate, cvv)
                .orElseThrow(() -> new RuntimeException("Карта с указанными данными не найдена"));

        card.setUser(currentUser);
        card.setActive(true);
        cardRepo.save(card);

        return "Карта успешно привязана к пользователю";
    }

    public List<CardDto> getLinkedCards() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        List<Card> cards = cardRepo.findByUser(currentUser);

        return cards.stream()
                .map(card -> new CardDto(card.getCardNumber(), card.getCardHolderName(), card.getExpiryDate(), card.isActive()))
                .collect(Collectors.toList());
    }

    @Transactional
    public String unlinkCard(Long cardId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        Card card = cardRepo.findByIdAndUser(cardId, currentUser)
                .orElseThrow(() -> new RuntimeException("Карта не найдена или не принадлежит текущему пользователю"));

        card.setUser(null);
        card.setActive(false);
        cardRepo.save(card);

        return "Карта успешно отвязана от пользователя";
    }
}