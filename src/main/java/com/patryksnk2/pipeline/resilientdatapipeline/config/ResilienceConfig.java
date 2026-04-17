package com.patryksnk2.pipeline.resilientdatapipeline.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties(prefix = "resilience")
@Validated
public record ResilienceConfig(
        @Min(1)
        int maxRetries,
        @NotNull
        Duration retryDelay,
        @NotNull
        Duration timeoutDuration,
        @Min(1)
        int circuitBreakerThreshold
) {
}
