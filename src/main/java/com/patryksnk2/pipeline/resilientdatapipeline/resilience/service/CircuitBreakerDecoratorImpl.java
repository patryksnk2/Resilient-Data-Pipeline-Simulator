package com.patryksnk2.pipeline.resilientdatapipeline.resilience.service;

import com.patryksnk2.pipeline.resilientdatapipeline.config.ResilienceConfig;
import com.patryksnk2.pipeline.resilientdatapipeline.exception.DataValidationException;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.PipelineContext;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.Stage;
import com.patryksnk2.pipeline.resilientdatapipeline.resilience.decorator.CircuitBreakerDecorator;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CircuitBreakerDecoratorImpl implements CircuitBreakerDecorator {
    private CircuitBreaker circuitBreaker;

    public CircuitBreakerDecoratorImpl(ResilienceConfig config) {
        CircuitBreakerConfig cbConfig = CircuitBreakerConfig.custom()
                .slidingWindowSize(config.slidingWindowSize())
                .failureRateThreshold(config.circuitBreakerThreshold())
                .waitDurationInOpenState(config.waitDurationInOpenState())
                .permittedNumberOfCallsInHalfOpenState(config.permittedNumberOfCallsInHalfOpenState())
                .ignoreExceptions(DataValidationException.class)
                .build();
        this.circuitBreaker = CircuitBreakerRegistry.of(cbConfig)
                .circuitBreaker("pipeline-circuit-breaker");
    }

    @Override
    public Stage decorate(Stage stage) {
        return new Stage() {
            @Override
            public void execute(PipelineContext pipelineContext) {
                circuitBreaker.executeRunnable(() -> stage.execute(pipelineContext));
            }

            @Override
            public String getName() {
                return stage.getName();
            }
        };
    }
}
