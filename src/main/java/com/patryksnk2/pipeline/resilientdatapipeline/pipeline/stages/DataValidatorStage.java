package com.patryksnk2.pipeline.resilientdatapipeline.pipeline.stages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patryksnk2.pipeline.resilientdatapipeline.exception.DataValidationException;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.PipelineContext;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataValidatorStage implements Stage {

    private final ObjectMapper objectMapper;

    @Override
    public void execute(PipelineContext pipelineContext) {
        String rawPayload = pipelineContext.getRawPayload();
        if (rawPayload == null || rawPayload.isBlank()) {
            throw new DataValidationException("Payload is null or empty");
        }
        Map<String,Object> parsed;

        try {
           parsed = objectMapper.readValue(rawPayload, Map.class);
        } catch (JsonProcessingException e) {
            throw new DataValidationException("Payload is not a valid JSON: " + e.getOriginalMessage());
        }
        if(parsed.isEmpty()){
            throw new DataValidationException("Payload has no fields");
        }

        pipelineContext.put("parsedPayload",parsed);
    }

    @Override
    public String getName() {
        return "DataValidatorStage";
    }
}
