package com.patryksnk2.pipeline.resilientdatapipeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class ResilientDataPipelineApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResilientDataPipelineApplication.class, args);
    }
}
