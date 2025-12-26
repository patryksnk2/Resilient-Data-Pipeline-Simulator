package com.patryksnk2.pipeline.resilientdatapipeline.dto;

import jakarta.validation.constraints.NotNull;

public record IngestResponse(@NotNull Long jobId) {
}
