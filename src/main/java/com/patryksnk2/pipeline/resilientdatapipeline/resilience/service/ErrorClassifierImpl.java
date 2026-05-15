package com.patryksnk2.pipeline.resilientdatapipeline.resilience.service;

import com.patryksnk2.pipeline.resilientdatapipeline.exception.DataValidationException;
import com.patryksnk2.pipeline.resilientdatapipeline.resilience.ErrorClassifier;
import org.springframework.stereotype.Component;

@Component
public class ErrorClassifierImpl implements ErrorClassifier {

    @Override
    public boolean isRetryable(Throwable throwable) {
        Throwable cause = throwable;
        while(cause != null){
            if(cause instanceof DataValidationException){
                return false;
            }
            cause = cause.getCause();
        }
        return true;
    }
}
