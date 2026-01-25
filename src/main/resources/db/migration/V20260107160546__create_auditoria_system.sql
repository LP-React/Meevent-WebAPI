CREATE TABLE audit_logs (
    audit_id BIGSERIAL PRIMARY KEY,

    table_name VARCHAR(100) NOT NULL,
    operation VARCHAR(10) NOT NULL CHECK (operation IN ('INSERT', 'UPDATE', 'DELETE')),
    record_id INTEGER NOT NULL,

    old_data JSONB,
    new_data JSONB,
    modified_fields TEXT[],

    user_id INTEGER,
    user_email VARCHAR(150),
    user_name VARCHAR(150),
    ip_address VARCHAR(45),
    user_agent TEXT,

    operation_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    source VARCHAR(50),

    CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id)
        REFERENCES users(user_id) ON DELETE SET NULL
);

-- Indexes for query optimization
CREATE INDEX idx_audit_logs_table_name ON audit_logs(table_name);
CREATE INDEX idx_audit_logs_operation ON audit_logs(operation);
CREATE INDEX idx_audit_logs_record_id ON audit_logs(record_id);
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_operation_timestamp ON audit_logs(operation_timestamp DESC);
CREATE INDEX idx_audit_logs_table_record ON audit_logs(table_name, record_id);

-- GIN indexes for JSONB search
CREATE INDEX idx_audit_logs_old_data ON audit_logs USING GIN (old_data);
CREATE INDEX idx_audit_logs_new_data ON audit_logs USING GIN (new_data);
