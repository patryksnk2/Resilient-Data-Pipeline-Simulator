package com.patryksnk2.pipeline.resilientdatapipeline.resilience;

import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.Stage;

/**
 * Wraps a stage with a Circuit Breaker to prevent calling failing services
 * It stops execution if too many errors occurs
 */
public interface CircuitBreakerDecorator {

    <T, R> Stage<T, R> decorate(Stage<T, R> stage);
}