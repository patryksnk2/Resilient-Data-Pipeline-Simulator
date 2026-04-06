package com.patryksnk2.pipeline.resilientdatapipeline.service;

import com.patryksnk2.pipeline.resilientdatapipeline.domain.model.DataRecord;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.model.PipelineJob;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.model.ProcessingResult;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.Status;
import com.patryksnk2.pipeline.resilientdatapipeline.exception.PipelineJobNotFoundException;
import com.patryksnk2.pipeline.resilientdatapipeline.processing.manager.PipelineJobManager;
import com.patryksnk2.pipeline.resilientdatapipeline.processing.service.PipelineServiceImpl;
import com.patryksnk2.pipeline.resilientdatapipeline.repository.PipelineJobRepository;
import com.patryksnk2.pipeline.resilientdatapipeline.repository.ProcessingResultRepository;
import com.patryksnk2.pipeline.resilientdatapipeline.repository.RecordRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class PipelineServiceImplIT {

    @Autowired
    private PipelineJobRepository pipelineJobRepository;
    @Autowired
    private PipelineServiceImpl sut;
    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private ProcessingResultRepository processingResultRepository;
    @Autowired
    private PipelineJobManager pipelineJobManager;

    @AfterEach
    public void tearDown() {
        processingResultRepository.deleteAll();
        pipelineJobRepository.deleteAll();
        recordRepository.deleteAll();
    }

    private PipelineJob createAndSaveJob() {
        String rawPayload = "{\"test\":1}";
        String source = "test/1";

        DataRecord dataRecord = DataRecord.builder()
                .rawPayload(rawPayload)
                .source(source)
                .build();
        DataRecord savedDataRecord = recordRepository.save(dataRecord);

        PipelineJob pipelineJob = PipelineJob.builder()
                .record(savedDataRecord)
                .status(Status.CREATED)
                .attempts(0)
                .build();
        return pipelineJobRepository.save(pipelineJob);
    }

    @Test
    void when_submitJobForProcessing_then_pipeline_should_change_status_to_SUCCEEDED() {
        //given
        PipelineJob savedJob = createAndSaveJob();
        //when
        sut.submitJobForProcessing(savedJob.getId());
        //then
        PipelineJob job = pipelineJobRepository.findById(savedJob.getId())
                .orElseThrow(() -> new PipelineJobNotFoundException("job not found"));
        Assertions.assertThat(job.getStatus()).isEqualTo(Status.SUCCEEDED);
        Assertions.assertThat(job.getAttempts()).isEqualTo(1);

    }


    @Test
    void when_submitJobForProcessing_then_success_processing_should_be_created_for_each_stage() {
        //given
        PipelineJob savedJob = createAndSaveJob();
        int expectedStagesCount = 1;
        //when
        sut.submitJobForProcessing(savedJob.getId());
        //then
        List<ProcessingResult> processingResults = processingResultRepository.findAllByPipelineId(savedJob.getId());
        Assertions.assertThat(processingResults)
                .hasSize(expectedStagesCount)
                .extracting(ProcessingResult::isSuccess)
                .containsOnly(true);
    }


    @Test
    void when_some_stage_fails_then_job_status_should_be_FAILED_and_failure_result_saved() {
        // given
        String rawPayload = "{}";
        String source = "test/1";

        DataRecord dataRecord = DataRecord.builder()
                .rawPayload(rawPayload)
                .source(source)
                .build();
        DataRecord savedDataRecord = recordRepository.save(dataRecord);

        PipelineJob pipelineJob = PipelineJob.builder()
                .record(savedDataRecord)
                .status(Status.CREATED)
                .attempts(0)
                .build();
        PipelineJob job = pipelineJobRepository.save(pipelineJob);

        // when
        sut.submitJobForProcessing(job.getId());

        // then
        PipelineJob result = pipelineJobRepository.findById(job.getId()).orElseThrow();
        Assertions.assertThat(result.getStatus()).isEqualTo(Status.FAILED);
        Assertions.assertThat(result.getLastError()).isNotBlank();

        List<ProcessingResult> results = processingResultRepository.findAllByPipelineId(job.getId());
        Assertions.assertThat(results)
                .anySatisfy(res -> Assertions.assertThat(res.isSuccess()).isFalse());
    }

    @Test
    void when_job_does_not_exist_then_should_throw_PipelineJobNotFoundException() {
        // when / then
        Assertions.assertThatThrownBy(() -> sut.submitJobForProcessing(999L))
                .isInstanceOf(PipelineJobNotFoundException.class);

        Assertions.assertThat(processingResultRepository.findAllByPipelineId(999L)).isEmpty();
    }
}