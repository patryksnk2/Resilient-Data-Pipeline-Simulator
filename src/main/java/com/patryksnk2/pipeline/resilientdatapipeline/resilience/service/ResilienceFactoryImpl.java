package com.patryksnk2.pipeline.resilientdatapipeline.resilience.service;

import com.patryksnk2.pipeline.resilientdatapipeline.config.ResilienceConfig;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.Stage;
import com.patryksnk2.pipeline.resilientdatapipeline.resilience.ResilienceFactory;
import com.patryksnk2.pipeline.resilientdatapipeline.resilience.decorator.RetryDecorator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResilienceFactoryImpl implements ResilienceFactory {
    private final RetryDecorator retryDecorator;

    public Stage decorate(Stage stage) {
        Stage decorated = stage;
        retryDecorator.decorate(stage);
        return decorated;
    }
}
