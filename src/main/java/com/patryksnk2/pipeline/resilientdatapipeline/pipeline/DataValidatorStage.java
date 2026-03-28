package com.patryksnk2.pipeline.resilientdatapipeline.pipeline;

import org.springframework.stereotype.Component;

@Component
public class DataValidatorStage implements Stage {

    @Override
    public void execute(PipelineContext pipelineContext) {

    }

    @Override
    public String getName() {
        return "DataValidatorStage";
    }
}
