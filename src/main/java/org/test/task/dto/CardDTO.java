package org.test.task.dto;

import lombok.Data;
import org.test.task.entity.CardStatus;

import java.time.LocalDate;

@Data
public class CardDTO {
    private Long id;
    private String maskedCardNumber;
    private String holderName;
    private LocalDate expirationDate;
    private Double balance;
    private CardStatus status;
    private Long userId;
}
