package com.patryksnk2.pipeline.resilientdatapipeline.ingestion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patryksnk2.pipeline.resilientdatapipeline.api.dto.IngestRequest;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.Status;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.model.DataRecord;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.model.PipelineJob;
import com.patryksnk2.pipeline.resilientdatapipeline.event.JobCreatedEvent;
import com.patryksnk2.pipeline.resilientdatapipeline.exception.PayloadSerializeException;
import com.patryksnk2.pipeline.resilientdatapipeline.repository.PipelineJobRepository;
import com.patryksnk2.pipeline.resilientdatapipeline.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngestServiceImpl implements IngestService {

    private final RecordRepository recordRepository;
    private final PipelineJobRepository pipelineJobRepository;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Long submit(IngestRequest request) {
        log.info("Ingest started for source={}", request.source());
        String rawPayload = serializer(request);

        DataRecord dataRecord = DataRecord.builder()
                .source(request.source())
                .rawPayload(rawPayload)
                .build();

        dataRecord = recordRepository.save(dataRecord);

        PipelineJob pipelineJob = PipelineJob.builder()
                .record(dataRecord)
                .status(Status.CREATED)
                .attempts(0)
                .lastError(null)
                .build();

        pipelineJob = pipelineJobRepository.save(pipelineJob);
        log.info("Ingest completed, jobId={} created for source={}", pipelineJob.getId(), request.source());
        eventPublisher.publishEvent(new JobCreatedEvent(pipelineJob.getId()));
        log.debug("JobCreatedEvent published for jobId={}", pipelineJob.getId());
        return pipelineJob.getId();
    }

    private String serializer(IngestRequest request) {
        try {
            return objectMapper.writeValueAsString(request.payload());
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize payload for source={}",request.source());
            throw new PayloadSerializeException("unable to serialize payload for source:" + request.source(),e);
        }
    }
}
