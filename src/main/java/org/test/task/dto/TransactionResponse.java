package org.test.task.dto;

import lombok.Data;
import org.test.task.entity.TransactionStatus;

import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private Long id;
    private Long fromCardId;
    private Long toCardId;
    private Double amount;
    private TransactionStatus status;
    private String description;
    private LocalDateTime transactionDate;
}
