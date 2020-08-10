CREATE TABLE viquse_job (
  reference_file              VARCHAR(1024),
  transcoded_file              VARCHAR(1024),
  job_id              VARBINARY(36) PRIMARY KEY,
  external_id         VARCHAR(256),
  message             VARCHAR(2048) NULL,
  created_date        TIMESTAMP DEFAULT current_timestamp,
  last_modified_date  TIMESTAMP DEFAULT current_timestamp,
  status              VARCHAR(28) DEFAULT 'NEW'
);

CREATE INDEX idx_external_id ON viquse_job(external_id);
CREATE INDEX idx_status ON viquse_job(status)
