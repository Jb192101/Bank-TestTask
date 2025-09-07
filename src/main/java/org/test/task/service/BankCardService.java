package org.test.task.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.task.entity.BankCard;
import org.test.task.entity.CardStatus;
import org.test.task.entity.User;
import org.test.task.repository.BankCardRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BankCardService {
    private final BankCardRepository bankCardRepository;
    private final UserService userService;
    private final EncryptionService encryptionService;

    public BankCard createCard(BankCard card, Long userId) {
        User user = userService.getUserById(userId);
        card.setUser(user);
        card.setMaskedCardNumber(generateMaskedCardNumber(card.getCardNumberEncrypted()));
        card.setCardNumberEncrypted(encryptionService.encrypt(card.getCardNumberEncrypted()));

        if (card.getExpirationDate().isBefore(LocalDate.now())) {
            card.setStatus(CardStatus.EXPIRED);
        }

        return bankCardRepository.save(card);
    }

    public BankCard getCardById(Long id) {
        return bankCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found with id: " + id));
    }

    public BankCard getUsersCardById(Long cardId) {
        User user = userService.getCurrentUser();
        return bankCardRepository.findByIdAndUserId(cardId, user.getId())
                .orElseThrow(() -> new RuntimeException("Card not found or access denied"));
    }

    public Page<BankCard> getUserCards(Pageable pageable) {
        User user = userService.getCurrentUser();
        return bankCardRepository.findByUserId(user.getId(), pageable);
    }

    public Page<BankCard> getUserCardsWithSearch(String search, Pageable pageable) {
        User user = userService.getCurrentUser();
        return bankCardRepository.findByUserIdAndSearch(user.getId(), search, pageable);
    }

    public Page<BankCard> getAllCards(Pageable pageable) {
        return bankCardRepository.findAll(pageable);
    }

    @Transactional
    public BankCard updateCardStatus(Long cardId, CardStatus status) {
        BankCard card = getCardById(cardId);
        card.setStatus(status);
        return bankCardRepository.save(card);
    }

    @Transactional
    public BankCard blockUsersCard(Long cardId) {
        User user = userService.getCurrentUser();
        BankCard card = bankCardRepository.findByIdAndUserId(cardId, user.getId())
                .orElseThrow(() -> new RuntimeException("Card not found or access denied"));

        card.setStatus(CardStatus.BLOCKED);
        return bankCardRepository.save(card);
    }

    public void deleteCard(Long cardId) {
        BankCard card = getCardById(cardId);
        bankCardRepository.delete(card);
    }

    public Double getUserTotalBalance() {
        User user = userService.getCurrentUser();
        return bankCardRepository.getTotalBalanceByUserId(user.getId());
    }

    private String generateMaskedCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "**** **** **** ****";
        }
        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + lastFour;
    }

    public void checkAndExpireCards() {
        List<BankCard> expiredCards = bankCardRepository.findByExpirationDateBeforeAndStatusNot(
                LocalDate.now(), CardStatus.EXPIRED);

        expiredCards.forEach(card -> card.setStatus(CardStatus.EXPIRED));
        bankCardRepository.saveAll(expiredCards);
    }
}
