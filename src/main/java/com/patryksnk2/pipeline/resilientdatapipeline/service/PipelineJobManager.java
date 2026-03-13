package com.patryksnk2.pipeline.resilientdatapipeline.service;

import com.patryksnk2.pipeline.resilientdatapipeline.domain.PipelineJob;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.Status;
import com.patryksnk2.pipeline.resilientdatapipeline.repository.PipelineJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineJobManager {

    private final PipelineJobRepository pipelineJobRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markAsProcessing(PipelineJob pipelineJob) {
        log.info("Marking jobId={} as PROCESSING", pipelineJob.getId());
        pipelineJob.setStatus(Status.PROCESSING);
        pipelineJobRepository.save(pipelineJob);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markAsSucceeded(PipelineJob pipelineJob) {
        log.info("Marking jobId={} as SUCCEEDED", pipelineJob.getId());
        pipelineJob.setStatus(Status.SUCCEEDED);
        pipelineJob.setAttempts(pipelineJob.getAttempts() + 1);
        pipelineJobRepository.save(pipelineJob);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markAsFailed(PipelineJob pipelineJob, String errorMessage) {
        log.warn("Marking jobId={} as FAILED, reason={}", pipelineJob.getId(), errorMessage);
        pipelineJob.setStatus(Status.FAILED);
        pipelineJob.setAttempts(pipelineJob.getAttempts() + 1);
        pipelineJob.setLastError(errorMessage);
        pipelineJobRepository.save(pipelineJob);
    }

}