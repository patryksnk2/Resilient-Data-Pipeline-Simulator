package com.patryksnk2.pipeline.resilientdatapipeline.resilience.service;

import com.patryksnk2.pipeline.resilientdatapipeline.config.ResilienceConfig;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.PipelineContext;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.Stage;
import com.patryksnk2.pipeline.resilientdatapipeline.resilience.ErrorClassifier;
import com.patryksnk2.pipeline.resilientdatapipeline.resilience.decorator.RetryDecorator;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.stereotype.Component;

@Component
public class RetryDecoratorImpl implements RetryDecorator {

    private final Retry retry;

    public RetryDecoratorImpl(ResilienceConfig config, ErrorClassifier errorClassifier) {
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(config.maxRetries())
                .waitDuration(config.timeoutDuration())
                .retryOnException(errorClassifier::isRetryable)
                .build();
        RetryRegistry registry = RetryRegistry.of(retryConfig);
        this.retry = registry.retry("pipeline-retry");
    }


    @Override
    public Stage decorate(Stage stage) {
        return new Stage() {
            @Override
            public void execute(PipelineContext pipelineContext) {
                retry.executeRunnable(() -> stage.execute(pipelineContext));
            }

            @Override
            public String getName() {
                return stage.getName();
            }
        };
    }
}
