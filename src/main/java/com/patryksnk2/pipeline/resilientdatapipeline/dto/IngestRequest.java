package com.patryksnk2.pipeline.resilientdatapipeline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record IngestRequest(
        @NotBlank(message = "source cannot be empty")
        @Size(max = 4000)
        String source,
        @NotNull
        Map<String, Object> payload
) {
}
