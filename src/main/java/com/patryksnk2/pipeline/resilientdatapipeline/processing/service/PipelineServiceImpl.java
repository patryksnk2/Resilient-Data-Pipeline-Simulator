package com.patryksnk2.pipeline.resilientdatapipeline.processing.service;

import com.patryksnk2.pipeline.resilientdatapipeline.api.dto.JobStatusDto;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.Status;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.model.PipelineJob;
import com.patryksnk2.pipeline.resilientdatapipeline.exception.PipelineJobNotFoundException;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.PipelineContext;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.Stage;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.decorators.ReportingStageDecorator;
import com.patryksnk2.pipeline.resilientdatapipeline.processing.manager.PipelineJobManager;
import com.patryksnk2.pipeline.resilientdatapipeline.repository.PipelineJobRepository;
import com.patryksnk2.pipeline.resilientdatapipeline.repository.ProcessingResultRepository;
import com.patryksnk2.pipeline.resilientdatapipeline.resilience.ResilienceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineServiceImpl implements PipelineService {

    private final PipelineJobRepository pipelineJobRepository;
    private final List<Stage> stages;
    private final ProcessingResultRepository processingResultRepository;
    private final PipelineJobManager pipelineJobManager;
    private final ResilienceFactory resilienceFactory;

    @Transactional
    @Override
    public void submitJobForProcessing(Long jobId) {
        log.info("Pipeline processing started for jobId={}", jobId);

        PipelineJob pipelineJob = pipelineJobRepository.findById(jobId)
                .orElseThrow(() -> new PipelineJobNotFoundException("job does not exist"));

        pipelineJobManager.markAsProcessing(pipelineJob);

        PipelineContext context = PipelineContext.builder()
                .pipelineJob(pipelineJob)
                .rawPayload(pipelineJob.getRecord().getRawPayload())
                .build();

        try {
            batchStages(context);
            pipelineJobManager.markAsSucceeded(pipelineJob);
            log.info("Pipeline processing succeeded for jobId={}", jobId);
        } catch (Exception e) {
            log.warn("Pipeline processing failed for jobId={}, reason={}", jobId, e.getMessage());
            pipelineJobManager.markAsFailed(pipelineJob, e.getMessage());
        }

    }

    private void batchStages(PipelineContext context) {
        for (Stage stage : stages) {
            Stage resilient = resilienceFactory.decorate(stage);
            new ReportingStageDecorator(resilient, processingResultRepository).execute(context);
        }
    }

}
