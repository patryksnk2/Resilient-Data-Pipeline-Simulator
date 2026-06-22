package com.patryksnk2.pipeline.resilientdatapipeline.pipeline.simulator;

import com.patryksnk2.pipeline.resilientdatapipeline.pipeline.exception.ExternalServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class ExternalServiceSimulator {

    @Value("${simulator.failure-rate:30}")
    private int failureRate;
    @Value("${simulator.slow-response-rate:30}")
    private int slowResponseRate;

    public void call(Map<String,Object> payload) throws InterruptedException {
        int random = ThreadLocalRandom.current().nextInt(100);
        if(random < failureRate){
            throw new ExternalServiceException("External service unavailable");
        }
        if (random < failureRate + slowResponseRate){
            Thread.sleep(10_100);
        }
    }

}
