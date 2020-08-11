CREATE TABLE viquse_job (
  reference_file              VARCHAR(1024),
  transcoded_file              VARCHAR(1024),
  job_id              VARBINARY(36) PRIMARY KEY,
  external_id         VARCHAR(256),
  message             VARCHAR(2048) NULL,
  created_date        TIMESTAMP DEFAULT current_timestamp,
  last_modified_date  TIMESTAMP DEFAULT current_timestamp,
  status              VARCHAR(28) DEFAULT 'NEW',
  result_summary_id   INTEGER DEFAULT NULL
);

CREATE INDEX idx_external_id ON viquse_job(external_id);
CREATE INDEX idx_status ON viquse_job(status);

CREATE TABLE result_summary (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    version VARCHAR(64),
    vmaf_score DOUBLE,
    exec_fps DOUBLE,
    model VARCHAR(64),
    scaled_width INTEGER,
    scaled_height INTEGER,
    subsample INTEGER,
    pool VARCHAR(128),
    frame_count INTEGER
);

CREATE TABLE result_summary_frame_results (
    result_summary_id INTEGER,
    frame_number INTEGER,
    adm2  FLOAT,
    motion2 FLOAT,
    vif_scale0 FLOAT,
    vif_scale1 FLOAT,
    vif_scale2 FLOAT,
    vif_scale3 FLOAT,
    vmaf FLOAT
);

CREATE INDEX idx_result_summary_frame_results ON result_summary_frame_results(result_summary_id, frame_number);