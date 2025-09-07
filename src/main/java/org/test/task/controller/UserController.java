package org.test.task.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.test.task.dto.TransactionRequest;
import org.test.task.entity.BankCard;
import org.test.task.entity.Transaction;
import org.test.task.service.BankCardService;
import org.test.task.service.TransactionService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
@Tag(name = "User", description = "API для пользовательских операций")
public class UserController {
    private final BankCardService bankCardService;
    private final TransactionService transactionService;

    @Operation(
            summary = "Получить карты пользователя",
            description = "Получение списка карт текущего пользователя с пагинацией и поиском"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Список карт пользователя"
    )
    @GetMapping("/cards")
    public ResponseEntity<?> getUserCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BankCard> cards;

        if (search != null && !search.trim().isEmpty()) {
            cards = bankCardService.getUserCardsWithSearch(search, pageable);
        } else {
            cards = bankCardService.getUserCards(pageable);
        }

        return ResponseEntity.ok(cards);
    }

    @GetMapping("/cards/{cardId}")
    public ResponseEntity<?> getCardDetails(@PathVariable Long cardId) {
        BankCard card = bankCardService.getUsersCardById(cardId);
        return ResponseEntity.ok(card);
    }

    @PostMapping("/cards/{cardId}/block")
    public ResponseEntity<?> blockCard(@PathVariable Long cardId) {
        BankCard card = bankCardService.blockUsersCard(cardId);
        return ResponseEntity.ok(card);
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getTotalBalance() {
        Double totalBalance = bankCardService.getUserTotalBalance();
        Map<String, Double> response = new HashMap<>();
        response.put("totalBalance", totalBalance);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Перевод между картами",
            description = "Выполнение перевода между картами текущего пользователя"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Перевод выполнен успешно",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Transaction.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Ошибка перевода (недостаточно средств, карта заблокирована и т.д.)"
    )
    @PostMapping("/transfers")
    public ResponseEntity<?> transferBetweenCards(@Valid @RequestBody TransactionRequest request) {
        Transaction transaction = transactionService.transferBetweenOwnCards(
                request.getFromCardId(),
                request.getToCardId(),
                request.getAmount(),
                request.getDescription()
        );
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getUserTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<Transaction> transactions = transactionService.getUserTransactions(pageable);

        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<?> getTransactionDetails(@PathVariable Long transactionId) {
        Transaction transaction = transactionService.getTransactionById(transactionId);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getUserDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        Double totalBalance = bankCardService.getUserTotalBalance();
        Page<BankCard> recentCards = bankCardService.getUserCards(PageRequest.of(0, 5));
        Page<Transaction> recentTransactions = transactionService.getUserTransactions(PageRequest.of(0, 5));

        dashboard.put("totalBalance", totalBalance);
        dashboard.put("totalCards", recentCards.getTotalElements());
        dashboard.put("recentCards", recentCards.getContent());
        dashboard.put("recentTransactions", recentTransactions.getContent());

        return ResponseEntity.ok(dashboard);
    }
}
