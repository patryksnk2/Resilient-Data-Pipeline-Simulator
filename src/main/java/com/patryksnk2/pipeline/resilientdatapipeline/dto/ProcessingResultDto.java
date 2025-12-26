package com.patryksnk2.pipeline.resilientdatapipeline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ProcessingResultDto(
        @NotBlank(message = "stage name cannot be blank")
        String stageName,
        @NotNull
        boolean success,
        String output,
        String error,
        Long durationMs,
        LocalDateTime executedAt
) {
}
