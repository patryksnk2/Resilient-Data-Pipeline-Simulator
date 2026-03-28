package com.patryksnk2.pipeline.resilientdatapipeline.resilience;

import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.Stage;

/**
 * Factory for creating and applying resilience decorators to pipeline stages.
 * Centralizes the assembly of retry, circuit breaker, and timeout logic.
 */
public interface ResilienceFactory {

    /**
     * Decorates a given stage with all configured resilience mechanisms.
     * * @param stage The base stage to be enhanced.
     * @return A decorated stage instance protected by resilience patterns.
     */
    Stage decorate(Stage stage);
}