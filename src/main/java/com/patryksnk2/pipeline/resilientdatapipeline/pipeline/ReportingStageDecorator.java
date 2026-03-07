package com.patryksnk2.pipeline.resilientdatapipeline.pipeline;

import com.patryksnk2.pipeline.resilientdatapipeline.repository.ProcessingResultRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReportingStageDecorator implements Stage {


    private final Stage delegate;
    private final ProcessingResultRepository processingResultRepository;

    @Override
    public void execute(PipelineContext pipelineContext) {
        try {
            delegate.execute(pipelineContext);
            saveResult(pipelineContext, true, null);
        } catch (Exception e) {
            saveResult(pipelineContext, false, e.getMessage());
            throw e;
        }
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    private void saveResult(PipelineContext context, boolean status, String errorMessage) {
        //TODO: future database logic
    }
}
