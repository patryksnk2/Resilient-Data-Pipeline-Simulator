package com.patryksnk2.pipeline.resilientdatapipeline.repository;

import com.patryksnk2.pipeline.resilientdatapipeline.domain.ProcessingResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessingResultRepository extends JpaRepository<ProcessingResult,Long> {
}
