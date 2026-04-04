package com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core;

import com.patryksnk2.pipeline.resilientdatapipeline.domain.model.PipelineJob;
import lombok.Builder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Builder
public record PipelineContext(
        PipelineJob pipelineJob,
        String rawPayload,
        Map<String, Object> metadata
) {
    public PipelineContext(PipelineJob pipelineJob, String rawPayload) {
        this(pipelineJob, rawPayload, new ConcurrentHashMap<>());
    }

    public void put(String key, Object value) {
        metadata.put(key, value);
    }

    public <T> T get(String key, Class<T> type) {
        Object value = metadata.get(key);
        if (value == null) return null;
        return type.cast(value);
    }
}
