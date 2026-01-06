package com.patryksnk2.pipeline.resilientdatapipeline.controller;

import com.patryksnk2.pipeline.resilientdatapipeline.dto.IngestRequest;
import com.patryksnk2.pipeline.resilientdatapipeline.dto.IngestResponse;
import com.patryksnk2.pipeline.resilientdatapipeline.service.IngestService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
public class RecordsController {

    private final IngestService ingestService;

    @Transactional
    @PostMapping(consumes = "application/json")
    public ResponseEntity<IngestResponse> ingest(@Valid @RequestBody IngestRequest request) {
        Long jobId = ingestService.submit(request);
        return ResponseEntity.status(201).body(new IngestResponse(jobId));
    }
}
