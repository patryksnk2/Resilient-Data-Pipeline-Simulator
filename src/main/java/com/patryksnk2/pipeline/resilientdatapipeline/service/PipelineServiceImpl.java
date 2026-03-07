package com.patryksnk2.pipeline.resilientdatapipeline.service;

import com.patryksnk2.pipeline.resilientdatapipeline.domain.DataRecord;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.PipelineJob;
import com.patryksnk2.pipeline.resilientdatapipeline.exception.PipelineJobNotFoundException;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.PipelineContext;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.Stage;
import com.patryksnk2.pipeline.resilientdatapipeline.repository.PipelineJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PipelineServiceImpl implements PipelineService {

    private final PipelineJobRepository pipelineJobRepository;

    private final List<Stage> stages;

    @Override
    public void submitJobForProcessing(Long jobId) {
        PipelineJob pipelineJob = pipelineJobRepository.findById(jobId)
                .orElseThrow(() -> new PipelineJobNotFoundException("job does not exist"));

        DataRecord pipelineDataRecord = pipelineJob.getRecord();

        PipelineContext context = PipelineContext.builder()
                .jobId(pipelineJob.getId())
                .rawPayload(pipelineDataRecord.getRawPayload())
                .build();

        batchStages(context);

    }

    @Override
    public void batchStages(PipelineContext context) {
        for (Stage stage : stages) {
            stage.execute(context);
        }
    }


}
