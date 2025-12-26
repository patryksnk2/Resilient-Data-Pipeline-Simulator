package com.patryksnk2.pipeline.resilientdatapipeline.service;

import com.patryksnk2.pipeline.resilientdatapipeline.dto.IngestRequest;

public interface IngestService {

    Long submit(IngestRequest request);
}
