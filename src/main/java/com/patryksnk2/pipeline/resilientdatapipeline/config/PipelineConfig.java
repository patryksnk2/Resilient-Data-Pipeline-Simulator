package com.patryksnk2.pipeline.resilientdatapipeline.config;

import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.DataValidatorStage;
import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.Stage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PipelineConfig {


    @Bean
    public List<Stage> getStages(DataValidatorStage dataValidatorStage) {
        return List.of(dataValidatorStage);
    }

}
