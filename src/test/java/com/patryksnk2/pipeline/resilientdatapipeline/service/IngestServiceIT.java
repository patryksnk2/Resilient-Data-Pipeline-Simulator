package com.patryksnk2.pipeline.resilientdatapipeline.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.DataRecord;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.PipelineJob;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.Status;
import com.patryksnk2.pipeline.resilientdatapipeline.dto.IngestRequest;
import com.patryksnk2.pipeline.resilientdatapipeline.repository.PipelineJobRepository;
import com.patryksnk2.pipeline.resilientdatapipeline.repository.RecordRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@SpringBootTest
@Transactional
class IngestServiceIT {
    @Autowired
    private IngestServiceImpl sut;
    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private PipelineJobRepository pipelineJobRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void when_submit_then_data_record_and_pipeline_job_are_persisted() throws JsonProcessingException {
        //given
        IngestRequest request = new IngestRequest("test/1",Map.of("test",1));
        //when
        sut.submit(request);
        //then
        List<PipelineJob> pipelineJob = pipelineJobRepository.findAll();
        List<DataRecord> dataRecord = recordRepository.findAll();
        Assertions.assertThat(pipelineJob).hasSize(1);
        Assertions.assertThat(dataRecord).hasSize(1);
        Assertions.assertThat(pipelineJob.getFirst().getStatus()).isEqualTo(Status.CREATED);
        Assertions.assertThat(dataRecord.getFirst().getSource()).isEqualTo(request.source());
        Assertions.assertThat(pipelineJob.getFirst().getRecord().getId()).isEqualTo(dataRecord.getFirst().getId());

    }
}