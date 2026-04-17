package com.patryksnk2.pipeline.resilientdatapipeline.resilience.service;

import com.patryksnk2.pipeline.resilientdatapipeline.config.ResilienceConfig;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.PipelineContext;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.Stage;
import com.patryksnk2.pipeline.resilientdatapipeline.resilience.decorator.TimeoutDecorator;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.function.Supplier;

@Component
public class TimeoutDecoratorImpl implements TimeoutDecorator {

    private final TimeLimiter timeLimiter;
    private final Executor workerExecutor;
    private final ScheduledExecutorService scheduler;

    public TimeoutDecoratorImpl(
            ResilienceConfig config,
            @Qualifier("pipelineTaskExecutor") Executor workerExecutor) {

        this.workerExecutor = workerExecutor;

        this.scheduler = Executors.newSingleThreadScheduledExecutor(
                Thread.ofVirtual().name("timeout-scheduler").factory()
        );

        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                .timeoutDuration(config.timeoutDuration())
                .cancelRunningFuture(true)
                .build();

        this.timeLimiter = TimeLimiterRegistry.of(timeLimiterConfig)
                .timeLimiter("pipeline-timeout");
    }

    @Override
    public Stage decorate(Stage stage) {
        return new Stage() {
            @Override
            public void execute(PipelineContext pipelineContext) {
                Supplier<CompletionStage<Void>> task = () ->
                        CompletableFuture.runAsync(() -> stage.execute(pipelineContext), workerExecutor);
                try {
                    timeLimiter.executeCompletionStage(scheduler, task)
                            .toCompletableFuture()
                            .get();
                } catch (ExecutionException e) {
                    throw new RuntimeException("Stage error: " + stage.getName(), e.getCause());
                } catch (InterruptedException e) {
                    throw new RuntimeException("Timeout in stage: " + stage.getName(), e);
                } catch (Exception e) {
                    throw new RuntimeException("Unexpected error in stage: " + stage.getName(), e);
                }
            }

            @Override
            public String getName() {
                return stage.getName();
            }
        };
    }
}