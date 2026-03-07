package com.patryksnk2.pipeline.resilientdatapipeline.service;

import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.PipelineContext;

public interface PipelineService {
    void submitJobForProcessing(Long jobId);

    void batchStages(PipelineContext context);
}
