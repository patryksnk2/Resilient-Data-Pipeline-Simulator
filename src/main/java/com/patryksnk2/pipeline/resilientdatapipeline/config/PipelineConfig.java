package com.patryksnk2.pipeline.resilientdatapipeline.config;

import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.stages.DataValidatorStage;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.core.Stage;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.stages.ExternalServiceStage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PipelineConfig {

    @Bean
    public List<Stage> getStages(DataValidatorStage dataValidatorStage, ExternalServiceStage externalServiceStage ) {
        return List.of(dataValidatorStage,externalServiceStage);
    }

}
