package com.patryksnk2.pipeline.resilientdatapipeline.pipeline.stages;

import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.PipelineContext;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.Stage;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.simulator.ExternalServiceSimulator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ExternalServiceStage implements Stage {

    private final ExternalServiceSimulator simulator;

    @Override
    public void execute(PipelineContext context) {
        Map<String, Object> payload = context.get("parsedPayload", Map.class);
        try {
            simulator.call(payload);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return "ExternalServiceStage";
    }
}