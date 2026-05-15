package com.patryksnk2.pipeline.resilientdatapipeline.processing.service;

import com.patryksnk2.pipeline.resilientdatapipeline.api.dto.JobStatusDto;
import com.patryksnk2.pipeline.resilientdatapipeline.api.dto.ProcessingResultDto;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.Status;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.model.PipelineJob;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.model.ProcessingResult;
import com.patryksnk2.pipeline.resilientdatapipeline.exception.PipelineJobNotFoundException;
import com.patryksnk2.pipeline.resilientdatapipeline.repository.PipelineJobRepository;
import com.patryksnk2.pipeline.resilientdatapipeline.repository.ProcessingResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PipelineJobServiceImpl implements PipelineJobService {
    public final PipelineJobRepository pipelineJobRepository;
    public final ProcessingResultRepository processingResultRepository;

    @Override
    public JobStatusDto getJobStatus(Long jobId) {
        PipelineJob pipelineJob = pipelineJobRepository.findById(jobId).orElseThrow(() -> new PipelineJobNotFoundException("Job with id: " + jobId + " dont exist"));
        List<ProcessingResultDto> processingResults = processingResultRepository.findAllByPipelineId(jobId).stream().map(this::toDTO).toList();
        Status status = pipelineJob.getStatus();
        int attempts = pipelineJob.getAttempts();
        String lastError = pipelineJob.getLastError();
        return new JobStatusDto(jobId, status, attempts, lastError, processingResults);
    }

    private ProcessingResultDto toDTO(ProcessingResult processingResult) {
        String stageName = processingResult.getStageName();
        boolean isSuccess = processingResult.isSuccess();
        String output = processingResult.getOutput();
        String error = processingResult.getError();
        Long durationMs = processingResult.getDurationMs();
        LocalDateTime executedAt = processingResult.getExecutedAt();
        return new ProcessingResultDto(stageName, isSuccess, output, error, durationMs, executedAt);
    }
}
