package com.patryksnk2.pipeline.resilientdatapipeline.resilience;

import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.Stage;

/**
 * Decorator that adds a time limit to a processing stage.
 * It ensures that a stage execution does not exceed a maximum allowed time.
 */
public interface TimeoutDecorator {

    /**
     * Wraps a stage with timeout logic.
     * * @param stage The stage to be monitored
     *
     * @return A stage instance that will time out if it takes too long
     */
    <T, R> Stage<T, R> decorate(Stage<T, R> stage);
}