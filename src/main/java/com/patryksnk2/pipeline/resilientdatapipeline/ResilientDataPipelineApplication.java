package com.patryksnk2.pipeline.resilientdatapipeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ResilientDataPipelineApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResilientDataPipelineApplication.class, args);
    }
}
