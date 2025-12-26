package com.patryksnk2.pipeline.resilientdatapipeline.repository;

import com.patryksnk2.pipeline.resilientdatapipeline.domain.PipelineJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PipelineJobRepository extends JpaRepository<PipelineJob, Long> {

}
