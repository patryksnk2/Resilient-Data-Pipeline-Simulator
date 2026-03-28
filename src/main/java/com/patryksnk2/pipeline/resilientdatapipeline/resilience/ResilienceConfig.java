package com.patryksnk2.pipeline.resilientdatapipeline.resilience;

import java.time.Duration;

/**
 * Provider for resilience strategy parameters.
 * Centralizes configuration for retries, timeouts, circuit breakers, and rate limits.
 */
public interface ResilienceConfig {

    /**
     * @return The maximum number of retry attempts allowed for a stage.
     */
    int getMaxRetries();

    /**
     * @return The delay duration between consecutive retry attempts.
     */
    Duration getRetryDelay();

    /**
     * @return The maximum time allowed for a single stage execution.
     */
    Duration getTimeoutDuration();

    /**
     * @return The threshold of failures before the Circuit Breaker opens.
     */
    int getCircuitBreakerThreshold();
}