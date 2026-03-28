package com.patryksnk2.pipeline.resilientdatapipeline.resilience.decorator;

import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.Stage;

/**
 * Strategy for wrapping a pipeline stage with additional resilience logic.
 */
public interface RetryDecorator {

    /**
     * Wraps the provided stage with retry capabilities.
     * * @param stage The original stage to be decorated.
     * @return A new Stage instance that includes the retry mechanism.
     */
    Stage decorate(Stage stage);
}