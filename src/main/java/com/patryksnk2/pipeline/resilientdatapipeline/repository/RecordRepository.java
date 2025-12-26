package com.patryksnk2.pipeline.resilientdatapipeline.repository;

import com.patryksnk2.pipeline.resilientdatapipeline.domain.DataRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepository extends JpaRepository<DataRecord, Long> {

}
