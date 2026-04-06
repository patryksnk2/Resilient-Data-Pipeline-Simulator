package com.patryksnk2.pipeline.resilientdatapipeline.exception;

public class DataValidationException extends RuntimeException {
    public DataValidationException(String message){
        super(message);
    }
}
