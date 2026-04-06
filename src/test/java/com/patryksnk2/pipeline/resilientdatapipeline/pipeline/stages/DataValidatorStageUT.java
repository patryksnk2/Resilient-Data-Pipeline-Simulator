package com.patryksnk2.pipeline.resilientdatapipeline.pipeline.stages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patryksnk2.pipeline.resilientdatapipeline.exception.DataValidationException;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.PipelineContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ExtendWith(MockitoExtension.class)
class DataValidatorStageUT {


    @Test
    void when_execute_is_called_then_rawPayload_is_parsed_successfully_to_pipelineContext() {
        // given
        DataValidatorStage sut = new DataValidatorStage(new ObjectMapper());
        PipelineContext context = PipelineContext.builder()
                .rawPayload("{\"test\":1}")
                .metadata(new ConcurrentHashMap<>())
                .build();
        // when
        sut.execute(context);
        // then
        Assertions.assertThat(context.get("parsedPayload", Map.class))
                .isNotNull()
                .containsKey("test");
    }

    @Test
    void should_throw_DataValidationException_when_rawPayload_is_null() {
        //given
        PipelineContext pipelineContext = PipelineContext.builder()
                .rawPayload(null)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        DataValidatorStage sut = new DataValidatorStage(objectMapper);
        //when /then
        Assertions.assertThatThrownBy(() -> sut.execute(pipelineContext))
                .isInstanceOf(DataValidationException.class).hasMessage("Payload is null or empty");
    }

    @Test
    void should_throw_DataValidationException_when_rawPayload_is_blank() {
        //given
        PipelineContext pipelineContext = PipelineContext.builder()
                .rawPayload(" ")
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        DataValidatorStage sut = new DataValidatorStage(objectMapper);
        //when /then
        Assertions.assertThatThrownBy(() -> sut.execute(pipelineContext))
                .isInstanceOf(DataValidationException.class).hasMessage("Payload is null or empty");

    }

    @Test
    void should_throw_DataValidationException_when_rawPayload_is_not_valid_json() throws JsonProcessingException {
        //given
        PipelineContext pipelineContext = PipelineContext.builder()
                .rawPayload("not-a-json")
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        DataValidatorStage sut = new DataValidatorStage(objectMapper);
        //when /then
        Assertions.assertThatThrownBy(() -> sut.execute(pipelineContext))
                .isInstanceOf(DataValidationException.class);
    }

    @Test
    void should_throw_DataValidationException_when_parsed_rawPayload_is_empty() {
        //given
        PipelineContext pipelineContext = PipelineContext.builder()
                .rawPayload("{}")
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        DataValidatorStage sut = new DataValidatorStage(objectMapper);
        //when / then
        Assertions.assertThatThrownBy(() -> sut.execute(pipelineContext))
                .isInstanceOf(DataValidationException.class).hasMessage("Payload has no fields");
    }
}