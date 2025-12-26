package com.patryksnk2.pipeline.resilientdatapipeline.pipeline;

/**
 * Represents a single stage in the pipeline.
 * Each stage is responsible for a specific transformation or action.
 *
 * @param <T> the type of the input data
 * @param <R> the type of the output result
 */
public interface Stage<T, R> {

    /**
     * Executes the processing logic for this stage.
     *
     * @param input the data to be transformed or processed
     * @return the result of the transformation
     * @throws RuntimeException or a specific exception if the processing fails
     */
    R execute(T input);
}