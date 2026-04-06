package com.patryksnk2.pipeline.resilientdatapipeline.processing.listener;

import com.patryksnk2.pipeline.resilientdatapipeline.event.JobCreatedEvent;
import com.patryksnk2.pipeline.resilientdatapipeline.processing.service.PipelineService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class JobCreatedEventListenerUT {

    @Mock
    private PipelineService pipelineService;
    @InjectMocks
    private JobCreatedEventListener sut;

    @Test
    void should_call_submitJobForProcessing_when_event_received() {
        //given
        JobCreatedEvent jobCreatedEvent = new JobCreatedEvent(10L);
        //when
        sut.handleJobCreatedEvent(jobCreatedEvent);
        //then
        verify(pipelineService).submitJobForProcessing(jobCreatedEvent.jobId());
        verifyNoMoreInteractions(pipelineService);
    }

    @Test
    void when_pipeline_throws_exception_then_exception_should_be_caught_and_not_propaganded() {
        //given
        JobCreatedEvent jobCreatedEvent = new JobCreatedEvent(10L);
        doThrow(new RuntimeException("pipeline failed"))
                .when(pipelineService).submitJobForProcessing(jobCreatedEvent.jobId());
        //when / then
        Assertions.assertThatNoException().isThrownBy(() -> sut.handleJobCreatedEvent(jobCreatedEvent));
    }
}