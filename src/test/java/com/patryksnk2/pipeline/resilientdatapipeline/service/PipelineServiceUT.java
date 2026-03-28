package com.patryksnk2.pipeline.resilientdatapipeline.service;

import com.patryksnk2.pipeline.resilientdatapipeline.domain.DataRecord;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.PipelineJob;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.Status;
import com.patryksnk2.pipeline.resilientdatapipeline.exception.PipelineJobNotFoundException;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.PipelineContext;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.Stage;
import com.patryksnk2.pipeline.resilientdatapipeline.repository.PipelineJobRepository;
import com.patryksnk2.pipeline.resilientdatapipeline.repository.ProcessingResultRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PipelineServiceUT {

    @Mock
    private PipelineJobRepository pipelineJobRepository;
    @Mock
    private ProcessingResultRepository processingResultRepository;
    @Mock
    private PipelineJobManager pipelineJobManager;
    @Mock
    private Stage stage1;
    @Mock
    private Stage stage2;
    @InjectMocks
    private PipelineServiceImpl sut;

    private static final Long JOB_ID = 10L;

    @BeforeEach
    void setUp() {
        sut = new PipelineServiceImpl(
                pipelineJobRepository,
                List.of(stage1,stage2),
                processingResultRepository,
                pipelineJobManager
        );
    }


    private PipelineJob createSampleJob() {

        DataRecord dataRecord = DataRecord.builder()
                .source("test/1")
                .rawPayload("{\"test\":1}")
                .id(1L).build();

        return PipelineJob.builder()
                .id(JOB_ID)
                .record(dataRecord)
                .attempts(0)
                .lastError(null)
                .status(Status.CREATED)
                .build();
    }

    @Test
    void when_submitJobForProcessing_is_called_then_job_is_marked_as_processing_then_succeeded() {
        //given
        PipelineJob pipelineJob = createSampleJob();
        when(pipelineJobRepository.findById(JOB_ID)).thenReturn(Optional.of(pipelineJob));

        //when
        sut.submitJobForProcessing(JOB_ID);

        //then
        InOrder inOrder = inOrder(pipelineJobManager);
        inOrder.verify(pipelineJobManager).markAsProcessing(pipelineJob);
        inOrder.verify(pipelineJobManager).markAsSucceeded(pipelineJob);
        inOrder.verifyNoMoreInteractions();

        verify(pipelineJobRepository).findById(JOB_ID);
        verifyNoMoreInteractions(pipelineJobRepository);
    }

    @Test
    void when_some_stage_failed_then_job_is_marked_as_failed() {
        //given
        PipelineJob pipelineJob = createSampleJob();
        when(pipelineJobRepository.findById(JOB_ID)).thenReturn(Optional.of(pipelineJob));
        doThrow(new RuntimeException("Simulated Failure")).when(stage1).execute(any(PipelineContext.class));
        //when
        sut.submitJobForProcessing(JOB_ID);
        //then
        verify(pipelineJobManager).markAsProcessing(pipelineJob);
        verify(pipelineJobManager).markAsFailed(pipelineJob,"Simulated Failure");
        verifyNoMoreInteractions(pipelineJobManager);
    }

    @Test
    void should_throw_exception_when_job_does_not_exist() {
        //when
        when(pipelineJobRepository.findById(JOB_ID)).thenReturn(Optional.empty());
        //then
        Assertions.assertThatThrownBy(() -> sut.submitJobForProcessing(JOB_ID)).isInstanceOf(PipelineJobNotFoundException.class);
        verifyNoMoreInteractions(processingResultRepository);
    }
}









