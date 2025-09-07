package org.test.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "Запрос на создание карты")
@Data
public class CreateCardRequest {

    @Schema(description = "Номер карты (16-19 цифр)", example = "4111111111111111", required = true)
    @NotBlank
    @Size(min = 16, max = 19, message = "Card number must be between 16 and 19 digits")
    private String cardNumber;

    @Schema(description = "Имя владельца карты", example = "JOHN DOE", required = true)
    @NotBlank
    private String holderName;

    @Schema(description = "Дата окончания действия", example = "2025-12-31", required = true)
    @NotNull
    private LocalDate expirationDate;

    @Schema(description = "Начальный баланс", example = "1000.0")
    private Double initialBalance = 0.0;
}
