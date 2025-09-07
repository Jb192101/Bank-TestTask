package org.test.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Schema(description = "Запрос на перевод средств")
@Data
public class TransactionRequest {

    @Schema(description = "ID карты отправителя", example = "1", required = true)
    @NotNull
    private Long fromCardId;

    @Schema(description = "ID карты получателя", example = "2", required = true)
    @NotNull
    private Long toCardId;

    @Schema(description = "Сумма перевода", example = "100.0", required = true)
    @NotNull
    @Positive
    private Double amount;

    @Schema(description = "Описание перевода", example = "Перевод между счетами")
    private String description;
}
