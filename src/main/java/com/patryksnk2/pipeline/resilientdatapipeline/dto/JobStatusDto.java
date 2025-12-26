package com.patryksnk2.pipeline.resilientdatapipeline.dto;

import com.patryksnk2.pipeline.resilientdatapipeline.domain.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record JobStatusDto(
        @NotNull
        Long jobId,
        @NotBlank(message = "status cannot be blank")
        Status status,
        @PositiveOrZero
        int attempts,
        String lastError
) {
}
