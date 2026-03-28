package com.patryksnk2.pipeline.resilientdatapipeline.service;

import com.patryksnk2.pipeline.resilientdatapipeline.domain.model.PipelineJob;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.Status;
import com.patryksnk2.pipeline.resilientdatapipeline.processing.manager.PipelineJobManager;
import com.patryksnk2.pipeline.resilientdatapipeline.repository.PipelineJobRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PipelineJobManagerUT {

    @Mock
    private PipelineJobRepository pipelineJobRepository;
    @Mock
    private PipelineJob job;
    @InjectMocks
    private PipelineJobManager sut;

    @BeforeEach
    void setUp() {
        job = PipelineJob.builder()
                .id(10L)
                .attempts(0)
                .status(Status.CREATED)
                .build();
    }

    @Test
    void should_mark_as_processing() {
        //when
        sut.markAsProcessing(job);

        //then
        verify(pipelineJobRepository).save(job);
        Assertions.assertThat(job.getStatus()).isEqualTo(Status.PROCESSING);
    }

    @Test
    void should_mark_as_succeeded_and_increment_attempts() {
        //when
        sut.markAsSucceeded(job);
        // then
        verify(pipelineJobRepository).save(job);
        Assertions.assertThat(job.getStatus()).isEqualTo(Status.SUCCEEDED);
        Assertions.assertThat(job.getAttempts()).isEqualTo(1);
    }

    @Test
    void should_mark_as_failed_with_error_and_increment_attempts() {
        //given
        String error = "Critical system failure";

        //when
        sut.markAsFailed(job, error);

        //then
        ArgumentCaptor<PipelineJob> captor = ArgumentCaptor.forClass(PipelineJob.class);
        verify(pipelineJobRepository).save(captor.capture());

        PipelineJob savedJob = captor.getValue();
        Assertions.assertThat(savedJob.getStatus()).isEqualTo(Status.FAILED);
        Assertions.assertThat(savedJob.getAttempts()).isEqualTo(1);
        Assertions.assertThat(savedJob.getLastError()).isEqualTo(error);
    }
}