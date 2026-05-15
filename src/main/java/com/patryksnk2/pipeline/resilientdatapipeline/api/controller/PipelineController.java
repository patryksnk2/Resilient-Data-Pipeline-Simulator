package com.patryksnk2.pipeline.resilientdatapipeline.api.controller;

import com.patryksnk2.pipeline.resilientdatapipeline.api.dto.JobStatusDto;
import com.patryksnk2.pipeline.resilientdatapipeline.processing.service.PipelineJobService;
import com.patryksnk2.pipeline.resilientdatapipeline.processing.service.PipelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class PipelineController {
    private final PipelineJobService pipelineJobService;

    @GetMapping("/{jobId}/status")
    public ResponseEntity<JobStatusDto> status(@PathVariable Long jobId) {
        return ResponseEntity.ok(pipelineJobService.getJobStatus(jobId));
    }
}
