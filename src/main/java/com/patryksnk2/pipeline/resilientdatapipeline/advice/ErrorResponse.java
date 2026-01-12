package com.patryksnk2.pipeline.resilientdatapipeline.advice;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;


public record ErrorResponse(Instant timestamp, int status, String error, String message, String path,
                            Map<String, String> fieldErrors) {
    public static ErrorResponse of(HttpStatus httpStatus, String message, String path) {
        return new ErrorResponse(Instant.now(), httpStatus.value(), httpStatus.getReasonPhrase(), message, path, null);
    }

    public static ErrorResponse ofValidation(HttpStatus status, String message, String path, Map<String, String> fieldErrors) {
        return new ErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(), message, path, fieldErrors);
    }
}
