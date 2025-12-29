package com.patryksnk2.pipeline.resilientdatapipeline.service;

import com.patryksnk2.pipeline.resilientdatapipeline.domain.DataRecord;
import com.patryksnk2.pipeline.resilientdatapipeline.dto.IngestRequest;
import org.springframework.stereotype.Service;

@Service
public class IngestServiceImpl implements IngestService {
    @Override
    public Long submit(IngestRequest request) {
        
        return 0L;
    }
}
