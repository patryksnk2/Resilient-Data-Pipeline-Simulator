package com.patryksnk2.pipeline.resilientdatapipeline.resilience.decorator;

import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.Stage;

/**
 * Strategy for wrapping a pipeline stage with a time limit.
 * Ensures that stage execution is interrupted if it exceeds the allocated duration.
 */
public interface TimeoutDecorator {

    /**
     * Enhances a stage with timeout capabilities.
     *
     * @param stage The original stage to be decorated.
     * @return A new Stage instance that enforces a timeout during execution.
     */
    Stage decorate(Stage stage);
}