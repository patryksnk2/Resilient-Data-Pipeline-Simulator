package com.patryksnk2.pipeline.resilientdatapipeline.pipeline.decorators;

import com.patryksnk2.pipeline.resilientdatapipeline.domain.model.ProcessingResult;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.PipelineContext;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.Stage;
import com.patryksnk2.pipeline.resilientdatapipeline.repository.ProcessingResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ReportingStageDecorator implements Stage {

    private final Stage delegate;
    private final ProcessingResultRepository processingResultRepository;

    @Override
    public void execute(PipelineContext pipelineContext) {
        log.debug("Executing stage={} for jobId={}", delegate.getName(), pipelineContext.pipelineJob().getId());
        try {
            delegate.execute(pipelineContext);
            saveResult(pipelineContext, true, null);
            log.debug("Stage={} succeeded for jobId={}", delegate.getName(), pipelineContext.pipelineJob().getId());
        } catch (Exception e) {
            log.warn("Stage={} failed for jobId={}, reason={}", delegate.getName(), pipelineContext.pipelineJob().getId(), e.getMessage());
            saveResult(pipelineContext, false, e.getMessage());
            throw e;
        }
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    private void saveResult(PipelineContext context, boolean success, String errorMessage) {
        ProcessingResult processingResult = ProcessingResult.builder()
                .pipelineJob(context.pipelineJob())
                .success(success)
                .error(errorMessage)
                .stageName(delegate.getName())
                .build();
        processingResultRepository.save(processingResult);
    }
}