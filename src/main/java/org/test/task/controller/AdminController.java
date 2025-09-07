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
import org.test.task.dto.CreateCardRequest;
import org.test.task.entity.BankCard;
import org.test.task.entity.CardStatus;
import org.test.task.entity.User;
import org.test.task.service.BankCardService;
import org.test.task.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "API для административных операций")
public class AdminController {
    private final UserService userService;
    private final BankCardService bankCardService;

    @Operation(
            summary = "Получить всех пользователей",
            description = "Получение списка всех пользователей с пагинацией (только для ADMIN)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Список пользователей"
    )
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<User> users = Page.empty();

        Map<String, Object> response = new HashMap<>();
        response.put("users", userService.getAllUsers());
        response.put("currentPage", page);
        response.put("totalItems", users.getTotalElements());
        response.put("totalPages", users.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long userId, @RequestParam boolean active) {
        User user = userService.updateUserStatus(userId, active);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().body("User deleted successfully");
    }

    @Operation(
            summary = "Создать карту",
            description = "Создание новой банковской карты для пользователя (только для ADMIN)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Карта успешно создана",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BankCard.class)
            )
    )
    @PostMapping("/cards")
    public ResponseEntity<?> createCard(@Valid @RequestBody CreateCardRequest request,
                                        @RequestParam Long userId) {
        BankCard card = new BankCard();
        card.setCardNumberEncrypted(request.getCardNumber());
        card.setHolderName(request.getHolderName());
        card.setExpirationDate(request.getExpirationDate());
        card.setBalance(request.getInitialBalance());

        BankCard createdCard = bankCardService.createCard(card, userId);
        return ResponseEntity.ok(createdCard);
    }

    @GetMapping("/cards")
    public ResponseEntity<?> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BankCard> cards;

        if (search != null && !search.trim().isEmpty()) {
            cards = bankCardService.getAllCards(pageable);
        } else {
            cards = bankCardService.getAllCards(pageable);
        }

        return ResponseEntity.ok(cards);
    }

    @PatchMapping("/cards/{cardId}/status")
    public ResponseEntity<?> updateCardStatus(@PathVariable Long cardId, @RequestParam CardStatus status) {
        BankCard card = bankCardService.updateCardStatus(cardId, status);
        return ResponseEntity.ok(card);
    }

    @DeleteMapping("/cards/{cardId}")
    public ResponseEntity<?> deleteCard(@PathVariable Long cardId) {
        bankCardService.deleteCard(cardId);
        return ResponseEntity.ok().body("Card deleted successfully");
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        return ResponseEntity.ok(stats);
    }
}
