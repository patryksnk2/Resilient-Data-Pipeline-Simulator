package com.patryksnk2.pipeline.resilientdatapipeline.api.dto;

import com.patryksnk2.pipeline.resilientdatapipeline.domain.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public record JobStatusDto(
        @NotNull
        Long jobId,
        @NotBlank(message = "status cannot be blank")
        Status status,
        @PositiveOrZero
        int attempts,
        String lastError,
        List<ProcessingResultDto> failedStages
) {
}
