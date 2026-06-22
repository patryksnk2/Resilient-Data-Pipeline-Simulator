package com.patryksnk2.pipeline.resilientdatapipeline.pipeline.exception;

public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String message) {
        super(message);
    }
}
