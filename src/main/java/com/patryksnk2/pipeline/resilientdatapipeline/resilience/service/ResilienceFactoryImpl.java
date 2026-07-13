package com.patryksnk2.pipeline.resilientdatapipeline.resilience.service;

import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.Stage;
import com.patryksnk2.pipeline.resilientdatapipeline.resilience.ResilienceFactory;
import com.patryksnk2.pipeline.resilientdatapipeline.resilience.decorator.CircuitBreakerDecorator;
import com.patryksnk2.pipeline.resilientdatapipeline.resilience.decorator.RateLimiterDecorator;
import com.patryksnk2.pipeline.resilientdatapipeline.resilience.decorator.RetryDecorator;
import com.patryksnk2.pipeline.resilientdatapipeline.resilience.decorator.TimeoutDecorator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResilienceFactoryImpl implements ResilienceFactory {
    private final RetryDecorator retryDecorator;
    private final TimeoutDecorator timeoutDecorator;
    private final CircuitBreakerDecorator circuitBreakerDecorator;
    private final RateLimiterDecorator rateLimiterDecorator;

    public Stage decorate(Stage stage) {
        Stage withTimeout = timeoutDecorator.decorate(stage);
        Stage withRetry = retryDecorator.decorate(withTimeout);
        Stage withCircuitBreaker = circuitBreakerDecorator.decorate(withRetry);
        return rateLimiterDecorator.decorate(withCircuitBreaker);
    }
}
