package com.patryksnk2.pipeline.resilientdatapipeline.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.DataRecord;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.PipelineJob;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.Status;
import com.patryksnk2.pipeline.resilientdatapipeline.dto.IngestRequest;
import com.patryksnk2.pipeline.resilientdatapipeline.exception.PayloadSerializeException;
import com.patryksnk2.pipeline.resilientdatapipeline.repository.PipelineJobRepository;
import com.patryksnk2.pipeline.resilientdatapipeline.repository.RecordRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngestServiceUT {

    @InjectMocks
    private IngestServiceImpl sut;
    @Mock
    private RecordRepository recordRepository;
    @Mock
    private PipelineJobRepository pipelineJobRepository;
    @Mock
    private ObjectMapper objectMapper;

    @Test
    void when_submit_request_is_valid_then_create_job_id() throws JsonProcessingException {
        //given
        String source = "test/1";
        Map<String, Object> payload = Map.of("test", 1);
        String rawPayload = "{\"test\":1}";

        IngestRequest request = new IngestRequest(source, payload);

        DataRecord savedRecord = DataRecord.builder()
                .id(1L)
                .source(request.source())
                .rawPayload(rawPayload)
                .build();

        PipelineJob savedPipelineJob = PipelineJob.builder()
                .id(10L)
                .record(savedRecord)
                .attempts(0)
                .lastError(null)
                .status(Status.CREATED)
                .build();

        when(recordRepository.save(any(DataRecord.class)))
                .thenReturn(savedRecord);

        when(pipelineJobRepository.save(any(PipelineJob.class)))
                .thenReturn(savedPipelineJob);

        when(objectMapper.writeValueAsString(request.payload()))
                .thenReturn(rawPayload);

        //when
        Long result = sut.submit(request);

        //then
        Assertions.assertThat(result).isEqualTo(10L);
        verify(recordRepository).save(any(DataRecord.class));
        verify(pipelineJobRepository).save(any(PipelineJob.class));
        verify(objectMapper).writeValueAsString(request.payload());
        verifyNoMoreInteractions(recordRepository, pipelineJobRepository, objectMapper);
    }

    @Test
    void submit_should_throw_PayloadSerializeException_when_serialization_fails() throws JsonProcessingException {
        //given
        String source = "test/1";
        Map<String, Object> payload = Map.of("test", "value");
        IngestRequest request = new IngestRequest(source, payload);

        when(objectMapper.writeValueAsString(request.payload()))
                .thenThrow(JsonProcessingException.class);

        //then
        Assertions.assertThatThrownBy(() -> sut.submit(request))
                .isInstanceOf(PayloadSerializeException.class)
                .hasMessage("unable to serialize payload for source: " + request.source());
        verify(objectMapper).writeValueAsString(payload);
    }
}
