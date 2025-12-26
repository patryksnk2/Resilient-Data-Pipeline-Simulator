package com.patryksnk2.pipeline.resilientdatapipeline.resilience;

import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.Stage;

/**
 * Decorator that limits the rate of stage executions.
 * It ensures the system does not exceed a defined number of calls per time period.
 */
public interface RateLimiterDecorator {

    /**
     * Wraps a stage with rate limiting logic.
     * * @param stage The stage to be limited
     *
     * @return A stage instance that respects rate limits
     */
    <T, R> Stage<T, R> decorate(Stage<T, R> stage);
}