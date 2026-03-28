package com.patryksnk2.pipeline.resilientdatapipeline.repository;

import com.patryksnk2.pipeline.resilientdatapipeline.domain.model.ProcessingResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;

public interface ProcessingResultRepository extends JpaRepository<ProcessingResult,Long> {
    @Query("select p from ProcessingResult p where p.pipelineJob.id = ?1")
    List<ProcessingResult> findAllByPipelineId(@NonNull Long id);
}
