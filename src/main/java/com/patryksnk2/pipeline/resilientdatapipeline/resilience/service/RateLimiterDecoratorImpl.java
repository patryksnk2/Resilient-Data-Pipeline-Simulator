package com.patryksnk2.pipeline.resilientdatapipeline.resilience.service;

import com.patryksnk2.pipeline.resilientdatapipeline.config.ResilienceConfig;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.PipelineContext;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.Stage;
import com.patryksnk2.pipeline.resilientdatapipeline.resilience.decorator.RateLimiterDecorator;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RateLimiterDecoratorImpl implements RateLimiterDecorator {
    private final RateLimiter rateLimiter;

    public RateLimiterDecoratorImpl(ResilienceConfig config) {
        RateLimiterConfig rateLimiterConfig = RateLimiterConfig.custom()
                .limitForPeriod(config.rateLimitForPeriod())
                .limitRefreshPeriod(config.rateLimitRefreshPeriod())
                .timeoutDuration(config.rateLimitTimeoutDuration())
                .build();

        this.rateLimiter = RateLimiterRegistry.of(rateLimiterConfig).rateLimiter("pipeline-rate-limiter");
    }

    @Override
    public Stage decorate(Stage stage) {
        return new Stage() {
            @Override
            public void execute(PipelineContext pipelineContext) {
                rateLimiter.executeRunnable(()->stage.execute(pipelineContext));
            }

            @Override
            public String getName() {
                return stage.getName();
            }
        };
    }
}
