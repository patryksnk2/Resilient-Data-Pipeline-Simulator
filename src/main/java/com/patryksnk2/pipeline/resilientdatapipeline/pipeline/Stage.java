package com.patryksnk2.pipeline.resilientdatapipeline.pipeline;

/**
 * A discrete step in the processing pipeline.
 */
public interface Stage {

    /**
     * Executes the stage logic using the provided context.
     * All input data is read from the context, and results are stored back into it.
     */
    void execute(PipelineContext pipelineContext);

    /**
     * Returns the unique identifier of the stage.
     * Used for logging and tracking progress in the database.
     */
    String getName();
}