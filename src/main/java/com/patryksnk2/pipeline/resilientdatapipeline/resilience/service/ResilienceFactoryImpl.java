package com.patryksnk2.pipeline.resilientdatapipeline.resilience.service;

import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.Stage;
import com.patryksnk2.pipeline.resilientdatapipeline.resilience.ResilienceFactory;
import com.patryksnk2.pipeline.resilientdatapipeline.resilience.decorator.RetryDecorator;
import com.patryksnk2.pipeline.resilientdatapipeline.resilience.decorator.TimeoutDecorator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResilienceFactoryImpl implements ResilienceFactory {
    private final RetryDecorator retryDecorator;
    private final TimeoutDecorator timeoutDecorator;

    public Stage decorate(Stage stage) {
        return timeoutDecorator.decorate(retryDecorator.decorate(stage));
    }
}
