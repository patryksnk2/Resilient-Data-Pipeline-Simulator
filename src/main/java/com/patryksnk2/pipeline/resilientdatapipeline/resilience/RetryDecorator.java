package com.patryksnk2.pipeline.resilientdatapipeline.resilience;

import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.Stage;

/**
 * Decorator that adds retry logic to a processing stage.
 * It automatically re-runs the stage if an error occurs.
 */
public interface RetryDecorator {

    /**
     * Wraps a stage with retry capabilities.
     * * @param stage the original stage to decorate
     *
     * @param <T> input type
     * @param <R> output type
     * @return the stage enhanced with retry logic
     */
    <T, R> Stage<T, R> decorate(Stage<T, R> stage);
}