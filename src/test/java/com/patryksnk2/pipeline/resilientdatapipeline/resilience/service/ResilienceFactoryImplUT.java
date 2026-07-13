package com.patryksnk2.pipeline.resilientdatapipeline.resilience.service;

import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.Stage;
import com.patryksnk2.pipeline.resilientdatapipeline.resilience.decorator.RateLimiterDecorator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResilienceFactoryImplUT {

    @Mock
    private Stage stage;
    @Mock
    private Stage decorated;
    @Mock
    private RetryDecoratorImpl retryDecorator;
    @Mock
    private TimeoutDecoratorImpl timeoutDecorator;
    @Mock
    private CircuitBreakerDecoratorImpl circuitBreakerDecorator;
    @Mock
    private RateLimiterDecorator rateLimiterDecorator;
    @InjectMocks
    private ResilienceFactoryImpl sut;

    @Test
    void should_apply_all_decorators() {
        //given
        when(rateLimiterDecorator.decorate(stage)).thenReturn(decorated);
        when(circuitBreakerDecorator.decorate(decorated)).thenReturn(decorated);
        when(retryDecorator.decorate(decorated)).thenReturn(decorated);
        when(timeoutDecorator.decorate(decorated)).thenReturn(decorated);

        //when
        sut.decorate(stage);
        //then

        InOrder inOrder = inOrder(
          rateLimiterDecorator,
          circuitBreakerDecorator,
          retryDecorator,
          timeoutDecorator
        );
        inOrder.verify(rateLimiterDecorator).decorate(stage);
        inOrder.verify(circuitBreakerDecorator).decorate(decorated);
        inOrder.verify(retryDecorator).decorate(decorated);
        inOrder.verify(timeoutDecorator).decorate(decorated);
    }
}