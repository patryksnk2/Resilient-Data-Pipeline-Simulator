package com.patryksnk2.pipeline.resilientdatapipeline.processing.service;

import com.patryksnk2.pipeline.resilientdatapipeline.api.dto.JobStatusDto;
import com.patryksnk2.pipeline.resilientdatapipeline.api.dto.ProcessingResultDto;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.Status;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.model.PipelineJob;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.model.ProcessingResult;
import com.patryksnk2.pipeline.resilientdatapipeline.exception.PipelineJobNotFoundException;
import com.patryksnk2.pipeline.resilientdatapipeline.repository.PipelineJobRepository;
import com.patryksnk2.pipeline.resilientdatapipeline.repository.ProcessingResultRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PipelineJobServiceImplUT {
    @InjectMocks
    private PipelineJobServiceImpl sut;
    @Mock
    private PipelineJobRepository pipelineJobRepository;
    @Mock
    private ProcessingResultRepository processingResultRepository;

    private List<ProcessingResult> getProcessingResultsFor(PipelineJob pipelineJob){
        List<ProcessingResult> processingResults = new ArrayList<>();
        String[] stageNames = new String[]{"pipeline-rate-limiter","pipeline-circuit-breaker","pipeline-retry","pipeline-timeout"};
        for (int i = 0; i < stageNames.length; i++) {
            ProcessingResult processingResult = ProcessingResult.builder()
                    .id((long) i)
                    .pipelineJob(pipelineJob)
                    .stageName(stageNames[Math.toIntExact(i)])
                    .success(true)
                    .build();
            processingResults.add(processingResult);
        };
        return processingResults;
    }
    @Test
    void when_jobId_exist_then_return_pipeline_status() {
        // given
        PipelineJob pipelineJob = PipelineJob.builder()
                .id(10L)
                .status(Status.SUCCEEDED)
                .attempts(1)
                .lastError(null)
                .build();
        List<ProcessingResult> processingResults = getProcessingResultsFor(pipelineJob);
        when(pipelineJobRepository.findById(10L)).thenReturn(Optional.of(pipelineJob));
        when(processingResultRepository.findAllByPipelineId(10L)).thenReturn(processingResults);

        // when
        JobStatusDto result = sut.getJobStatus(10L);

        // then
        Assertions.assertThat(result.jobId()).isEqualTo(10L);
        Assertions.assertThat(result.status()).isEqualTo(Status.SUCCEEDED);
        Assertions.assertThat(result.attempts()).isEqualTo(1);
        Assertions.assertThat(result.lastError()).isNull();
        Assertions.assertThat(result.processingResults())
                .hasSize(4)
                .extracting(ProcessingResultDto::stageName)
                .containsExactly(
                        "pipeline-rate-limiter",
                        "pipeline-circuit-breaker",
                        "pipeline-retry",
                        "pipeline-timeout"
                );
    }

    @Test
    void when_jobId_does_not_exist_then_throw_PipelineJobNotFoundException() {
        // given
        when(pipelineJobRepository.findById(999L)).thenReturn(Optional.empty());

        // when / then
        Assertions.assertThatThrownBy(() -> sut.getJobStatus(999L))
                .isInstanceOf(PipelineJobNotFoundException.class);

        verifyNoInteractions(processingResultRepository);
    }
}