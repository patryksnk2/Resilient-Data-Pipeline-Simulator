package com.patryksnk2.pipeline.resilientdatapipeline.exception;

public class PayloadSerializeException extends RuntimeException {
    public PayloadSerializeException(String message,Throwable cause) {
        super(message,cause);
    }
}
