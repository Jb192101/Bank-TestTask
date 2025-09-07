package org.test.task.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.task.entity.*;
import org.test.task.repository.BankCardRepository;
import org.test.task.repository.TransactionRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final BankCardRepository bankCardRepository;
    private final UserService userService;

    @Transactional
    public Transaction transferBetweenOwnCards(Long fromCardId, Long toCardId, Double amount, String description) {
        User user = userService.getCurrentUser();

        BankCard fromCard = bankCardRepository.findByIdAndUserId(fromCardId, user.getId())
                .orElseThrow(() -> new RuntimeException("Source card not found or access denied"));

        BankCard toCard = bankCardRepository.findByIdAndUserId(toCardId, user.getId())
                .orElseThrow(() -> new RuntimeException("Destination card not found or access denied"));

        validateTransfer(fromCard, toCard, amount);

        Transaction transaction = new Transaction();
        transaction.setFromCard(fromCard);
        transaction.setToCard(toCard);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setStatus(TransactionStatus.PENDING);

        try {
            fromCard.setBalance(fromCard.getBalance() - amount);
            toCard.setBalance(toCard.getBalance() + amount);

            bankCardRepository.save(fromCard);
            bankCardRepository.save(toCard);

            transaction.setStatus(TransactionStatus.COMPLETED);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            throw new RuntimeException("Transfer failed: " + e.getMessage());
        } finally {
            transaction = transactionRepository.save(transaction);
        }

        return transaction;
    }

    public Page<Transaction> getUserTransactions(Pageable pageable) {
        User user = userService.getCurrentUser();
        return transactionRepository.findByUserId(user.getId(), pageable);
    }

    public Transaction getTransactionById(Long transactionId) {
        User user = userService.getCurrentUser();
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getFromCard().getUser().getId().equals(user.getId()) &&
                !transaction.getToCard().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied to this transaction");
        }

        return transaction;
    }

    private void validateTransfer(BankCard fromCard, BankCard toCard, Double amount) {
        if (fromCard.getStatus() != CardStatus.ACTIVE) {
            throw new RuntimeException("Source card is not active");
        }

        if (toCard.getStatus() != CardStatus.ACTIVE) {
            throw new RuntimeException("Destination card is not active");
        }

        if (fromCard.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds");
        }

        if (amount <= 0) {
            throw new RuntimeException("Amount must be positive");
        }

        if (fromCard.getId().equals(toCard.getId())) {
            throw new RuntimeException("Cannot transfer to the same card");
        }
    }
}
