package com.patryksnk2.pipeline.resilientdatapipeline.ingestion;

import com.patryksnk2.pipeline.resilientdatapipeline.api.dto.IngestRequest;

/**
 * interface for ingesting data into the processing pipeline
 */
public interface IngestService {

    /**
     * Submits a new data ingestion request for processing
     *
     * @param request the object containing the input data and necessary metadata
     * @return a unique ID assigned to the submitted request
     */
    Long submit(IngestRequest request);
}
