package org.test.task.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_cards")
@Data
@EntityListeners(AuditingEntityListener.class)
public class BankCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number_encrypted")
    private String cardNumberEncrypted;

    @Column(name = "masked_card_number")
    private String maskedCardNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String holderName;
    private LocalDate expirationDate;
    private Double balance = 0.0;

    @Enumerated(EnumType.STRING)
    private CardStatus status = CardStatus.ACTIVE;

    @CreatedDate
    private LocalDateTime createdDate;
}
