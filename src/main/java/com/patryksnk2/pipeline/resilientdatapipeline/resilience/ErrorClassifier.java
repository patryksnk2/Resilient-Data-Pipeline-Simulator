package com.patryksnk2.pipeline.resilientdatapipeline.resilience;

/**
 * Interface to determine if an error should trigger a retry.
 * It helps to distinguish between temporary issues and permanent failures.
 */
public interface ErrorClassifier {

    /**
     * Checks if the given exception is retryable.
     * * @param throwable the error that occurred
     *
     * @return true if we should try again, false otherwise
     */
    boolean isRetryable(Throwable throwable);
}