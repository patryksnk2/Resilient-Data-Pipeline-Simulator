ALTER TABLE data_record RENAME COLUMN payload to raw_payload;

ALTER TABLE data_record ALTER COLUMN raw_payload TYPE VARCHAR2(4000);
