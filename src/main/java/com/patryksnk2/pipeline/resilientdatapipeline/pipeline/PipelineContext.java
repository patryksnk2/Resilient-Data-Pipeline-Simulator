package com.patryksnk2.pipeline.resilientdatapipeline.pipeline;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record PipelineContext(
        Long jobId,
        String rawPayload,
        Map<String,Object> metadata
) {
    public PipelineContext(Long jobId,String rawPayload){
        this(jobId,rawPayload,new ConcurrentHashMap<>());
    }

    public void put(String key, Object value){
        metadata.put(key,value);
    }

    public <T> T get(String key,Class<T> type){
        Object value = metadata.get(key);
        if(value == null) return null;
        return type.cast(value);
    }
}
