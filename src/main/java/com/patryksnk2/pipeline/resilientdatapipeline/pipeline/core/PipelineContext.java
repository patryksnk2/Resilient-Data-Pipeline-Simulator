package com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core;

import com.patryksnk2.pipeline.resilientdatapipeline.domain.model.PipelineJob;
import lombok.Builder;
import lombok.Value;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Value
@Builder
public class PipelineContext {
    PipelineJob pipelineJob;
    String rawPayload;

    @Builder.Default
    Map<String, Object> metadata = new ConcurrentHashMap<>();

    public void put(String key, Object value) {
        metadata.put(key, value);
    }

    public <T> T get(String key, Class<T> type) {
        Object value = metadata.get(key);
        if (value == null) return null;
        return type.cast(value);
    }
}