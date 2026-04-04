package com.patryksnk2.pipeline.resilientdatapipeline.processing.listener;

import com.patryksnk2.pipeline.resilientdatapipeline.event.JobCreatedEvent;
import com.patryksnk2.pipeline.resilientdatapipeline.processing.service.PipelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobCreatedEventListener {
    private final PipelineService pipelineService;

    @Async("pipelineTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleJobCreatedEvent(JobCreatedEvent event) {
        log.info("Received JobCreatedEvent for jobId={}. Starting async processing...", event.jobId());
        try {
            pipelineService.submitJobForProcessing(event.jobId());
        } catch (Exception e) {
            log.error("Failed to process pipeline job for jobId={}. Reason: {}", event.jobId(), e.getMessage(), e);
        }
    }
}
