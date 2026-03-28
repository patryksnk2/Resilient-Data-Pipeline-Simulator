package com.patryksnk2.pipeline.resilientdatapipeline.resilience;

import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.Stage;

/**
 * Strategy for wrapping a pipeline stage with rate limiting logic.
 * Ensures the system does not exceed a defined threshold of executions per time period.
 */
public interface RateLimiterDecorator {

    /**
     * Enhances a stage with rate limiting capabilities.
     *
     * @param stage The original stage to be decorated.
     * @return A new Stage instance that enforces rate limits during execution.
     */
    Stage decorate(Stage stage);
}