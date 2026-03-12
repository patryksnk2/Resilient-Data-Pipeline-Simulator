package com.patryksnk2.pipeline.resilientdatapipeline.service;

import com.patryksnk2.pipeline.resilientdatapipeline.domain.PipelineJob;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.ProcessingResult;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.Status;
import com.patryksnk2.pipeline.resilientdatapipeline.exception.PipelineJobNotFoundException;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.Stage;
import com.patryksnk2.pipeline.resilientdatapipeline.repository.PipelineJobRepository;
import com.patryksnk2.pipeline.resilientdatapipeline.repository.ProcessingResultRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PipelineServiceUT {

    @Mock
    private PipelineJobRepository pipelineJobRepository;
    @Mock
    private ProcessingResultRepository processingResultRepository;
    @Mock
    private Stage<Integer, Integer> stage1;
    @Mock
    private Stage<Integer, Integer> stage2;
    @InjectMocks
    private PipelineServiceImpl sut;
    private static final Long JOB_ID = 10L;

    private PipelineJob createSampleJob() {
        return PipelineJob.builder()
                .id(JOB_ID)
                .record(null)
                .attempts(0)
                .lastError(null)
                .status(Status.CREATED)
                .build();
    }

    @Test
    void when_submitJobForProcessing_then_pipelineJob_should_have_SUCCEDED_status() {
        //given
        PipelineJob pipelineJob = createSampleJob();
        when(pipelineJobRepository.findById(JOB_ID)).thenReturn(Optional.of(pipelineJob));
        //when
        sut.submitJobForProcessing(JOB_ID);
        //then
        ArgumentCaptor<PipelineJob> pipelineCaptor = ArgumentCaptor.forClass(PipelineJob.class);

        verify(pipelineJobRepository).findById(JOB_ID);
        verify(pipelineJobRepository).save(pipelineCaptor.capture());
        verifyNoMoreInteractions(pipelineJobRepository);

        Assertions.assertThat(pipelineCaptor.getValue().getStatus()).isEqualTo(Status.SUCCEEDED);
        Assertions.assertThat(pipelineCaptor.getValue().getAttempts()).isEqualTo(1);

    }

    @Test
    void when_all_stages_succeeded_then_processing_result_are_saved_for_each_stage() {
        //given
        PipelineJob pipelineJob = createSampleJob();
        when(pipelineJobRepository.findById(JOB_ID)).thenReturn(Optional.of(pipelineJob));
        //when
        sut.submitJobForProcessing(JOB_ID);
        //then
        ArgumentCaptor<PipelineJob> pipelineCaptor = ArgumentCaptor.forClass(PipelineJob.class);
        ArgumentCaptor<ProcessingResult> processingCaptor = ArgumentCaptor.forClass(ProcessingResult.class);

        verify(pipelineJobRepository).findById(JOB_ID);
        verify(pipelineJobRepository).save(pipelineCaptor.capture());
        verify(processingResultRepository, times(3)).save(processingCaptor.capture());
        verifyNoMoreInteractions(pipelineJobRepository);
        verifyNoMoreInteractions(processingResultRepository);

        Assertions.assertThat(processingCaptor.getAllValues()).hasSize(3).allSatisfy(res->assertThat(res.isSuccess()).isTrue());
        Assertions.assertThat(pipelineCaptor.getValue().getStatus()).isEqualTo(Status.SUCCEEDED);
        Assertions.assertThat(pipelineCaptor.getValue().getAttempts()).isEqualTo(1);
        Assertions.assertThat(processingCaptor.getAllValues()).hasSize(3);
    }

    @Test
    void when_some_stage_failed_then_pipelineJob_should_have_FAILED_status() {
        //given
        PipelineJob pipelineJob = createSampleJob();
        when(pipelineJobRepository.findById(JOB_ID)).thenReturn(Optional.of(pipelineJob));
        when(stage1.execute(any())).thenReturn(10);
        when(stage2.execute(any())).thenThrow(new RuntimeException());
        //when
        sut.submitJobForProcessing(JOB_ID);
        //then
        ArgumentCaptor<PipelineJob> pipelineCaptor = ArgumentCaptor.forClass(PipelineJob.class);
        ArgumentCaptor<ProcessingResult> processingResultCaptor = ArgumentCaptor.forClass(ProcessingResult.class);


        verify(processingResultRepository, times(2)).save(processingResultCaptor.capture());
        verify(pipelineJobRepository).save(pipelineCaptor.capture());
        verifyNoMoreInteractions(pipelineJobRepository);
        verifyNoMoreInteractions(processingResultRepository);

        Assertions.assertThat(processingResultCaptor.getAllValues())
                .extracting(ProcessingResult::isSuccess, ProcessingResult::getStageName)
                .containsExactly(tuple(true, "stage1"), tuple(false, "stage2"));
        Assertions.assertThat(pipelineCaptor.getValue().getStatus()).isEqualTo(Status.FAILED);
        Assertions.assertThat(pipelineCaptor.getValue().getLastError()).isNotNull();


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









