CREATE TABLE data_record (
    id BIGSERIAL PRIMARY KEY,
    source VARCHAR(255) NOT NULL,
    payload JSON NOT NULL,
    received_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pipeline_job (
    id BIGSERIAL PRIMARY KEY,
    record_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    attempts INTEGER DEFAULT 0 NOT NULL,
    last_error TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_pipeline_job_record FOREIGN KEY (record_id) REFERENCES data_record(id),
    CONSTRAINT chk_pipeline_status CHECK (status IN ('CREATED', 'PROCESSING', 'SUCCEEDED', 'FAILED'))
);

CREATE TABLE processing_result (
    id BIGSERIAL PRIMARY KEY,
    job_id BIGINT NOT NULL,
    stage_name VARCHAR(100) NOT NULL,
    success BOOLEAN NOT NULL,
    output TEXT,
    error TEXT,
    duration_ms BIGINT,
    executed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_result_job FOREIGN KEY (job_id) REFERENCES pipeline_job(id)
);

CREATE INDEX idx_pipeline_job_record_id ON pipeline_job(record_id);
CREATE INDEX idx_pipeline_job_status ON pipeline_job(status);
CREATE INDEX idx_processing_result_job_id ON processing_result(job_id);
CREATE INDEX idx_data_record_received_at ON data_record(received_at);