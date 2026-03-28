package com.patryksnk2.pipeline.resilientdatapipeline.api.dto;

import jakarta.validation.constraints.NotNull;

public record IngestResponse(@NotNull Long jobId) {
}
