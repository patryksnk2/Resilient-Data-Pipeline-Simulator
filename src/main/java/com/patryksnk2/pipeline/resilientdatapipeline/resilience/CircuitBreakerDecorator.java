package com.patryksnk2.pipeline.resilientdatapipeline.resilience;

import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.Stage;

/**
 * Strategy for wrapping a pipeline stage with a Circuit Breaker pattern.
 * Prevents execution of a failing stage to allow the system to recover
 * and avoid cascading failures.
 */
public interface CircuitBreakerDecorator {

    /**
     * Enhances a stage with circuit breaker capabilities.
     *
     * @param stage The original stage to be decorated.
     * @return A new Stage instance protected by a circuit breaker.
     */
    Stage decorate(Stage stage);
}