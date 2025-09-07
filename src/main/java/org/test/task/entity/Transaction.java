package org.test.task.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_card_id")
    private BankCard fromCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_card_id")
    private BankCard toCard;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private String description;

    @CreatedDate
    private LocalDateTime transactionDate;
}