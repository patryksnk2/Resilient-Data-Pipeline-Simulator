package com.patryksnk2.pipeline.resilientdatapipeline.exception;

public class PipelineJobNotFoundException extends RuntimeException {
    public PipelineJobNotFoundException(String message) {
        super(message);
    }
}
