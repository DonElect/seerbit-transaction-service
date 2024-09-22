package com.seerbit.transact_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    @NotBlank(message = "Amount is required.")
    @Pattern(regexp = "^\\d+(\\.\\d+)?$", message = "Amount should be a valid number")
    private String amount;
    @NotNull(message = "Timestamp is required.")
    private Instant timestamp;
}
