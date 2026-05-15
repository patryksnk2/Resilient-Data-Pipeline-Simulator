package com.patryksnk2.pipeline.resilientdatapipeline.processing.service;

import com.patryksnk2.pipeline.resilientdatapipeline.api.dto.JobStatusDto;

public interface PipelineJobService {

    JobStatusDto getJobStatus(Long JobId);
}
