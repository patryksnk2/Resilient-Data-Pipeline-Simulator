package com.patryksnk2.pipeline.resilientdatapipeline.pipeline;

import com.patryksnk2.pipeline.resilientdatapipeline.domain.model.PipelineJob;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.model.ProcessingResult;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.PipelineContext;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.Stage;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.decorators.ReportingStageDecorator;
import com.patryksnk2.pipeline.resilientdatapipeline.repository.ProcessingResultRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportingStageDecoratorUT {

    @Mock
    private ProcessingResultRepository processingResultRepository;
    @Mock
    private Stage delegate;
    @Mock
    private PipelineJob pipelineJob;
    @Mock
    private PipelineContext pipelineContext;
    @InjectMocks
    private ReportingStageDecorator sut;


    @Test
    void should_create_ProcessingResult_with_success_true_when_stage_succeeds() {
        //given
        String delegateStageName = "dataValidatorStage";
        when(pipelineContext.pipelineJob()).thenReturn(pipelineJob);
        when(pipelineJob.getId()).thenReturn(10L);
        when(delegate.getName()).thenReturn(delegateStageName);

        ArgumentCaptor<ProcessingResult> processingCaptor = ArgumentCaptor.forClass(ProcessingResult.class);

        //when
        sut.execute(pipelineContext);
        //then
        verify(delegate).execute(pipelineContext);
        verify(processingResultRepository).save(processingCaptor.capture());

        Assertions.assertThat(processingCaptor.getValue().isSuccess()).isTrue();
        Assertions.assertThat(processingCaptor.getValue().getStageName()).isEqualTo(delegateStageName);
        Assertions.assertThat(processingCaptor.getValue().getError()).isNull();


    }

    @Test
    void should_create_ProcessingResult_with_success_false_and_rethrow_when_stage_fails() {
        //given
        String delegateStageName = "dataValidatorStage";
        when(pipelineContext.pipelineJob()).thenReturn(pipelineJob);
        when(pipelineJob.getId()).thenReturn(10L);
        when(delegate.getName()).thenReturn(delegateStageName);

        doThrow(new RuntimeException("failed")).when(delegate).execute(pipelineContext);

        ArgumentCaptor<ProcessingResult> processingCaptor = ArgumentCaptor.forClass(ProcessingResult.class);

        //when
        Assertions.assertThatThrownBy(() -> sut.execute(pipelineContext))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("failed");
        //then
        verify(delegate).execute(pipelineContext);
        verify(processingResultRepository).save(processingCaptor.capture());

        Assertions.assertThat(processingCaptor.getValue().isSuccess()).isFalse();
        Assertions.assertThat(processingCaptor.getValue().getStageName()).isEqualTo(delegateStageName);
        Assertions.assertThat(processingCaptor.getValue().getError()).isNotNull();

    }

    @Test
    void should_delegate_getName_to_wrapped_stage() {
        //given
        String expectedName = "clean-data-stage";
        when(delegate.getName()).thenReturn(expectedName);
        //when
        String actualName = sut.getName();
        //then
        Assertions.assertThat(actualName).isEqualTo(expectedName);
        verify(delegate, times(1)).getName();

    }
}