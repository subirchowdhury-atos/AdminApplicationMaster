

-- Create users table --
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL DEFAULT '',
    encrypted_password VARCHAR(255) NOT NULL DEFAULT '',
    reset_password_token VARCHAR(255),
    reset_password_sent_at TIMESTAMP,
    remember_created_at TIMESTAMP,
    sign_in_count INTEGER NOT NULL DEFAULT 0,
    current_sign_in_at TIMESTAMP,
    last_sign_in_at TIMESTAMP,
    current_sign_in_ip VARCHAR(255),
    last_sign_in_ip VARCHAR(255),
    contact VARCHAR(255),
    authentication_token VARCHAR(30) UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    role VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255)
);

CREATE UNIQUE INDEX index_users_on_email ON users(email);
CREATE UNIQUE INDEX index_users_on_reset_password_token ON users(reset_password_token);

-- Create addresses table --
CREATE TABLE addresses (
    id BIGSERIAL PRIMARY KEY,
    street TEXT,
    unit_number TEXT,
    city TEXT,
    state VARCHAR(255),
    zip VARCHAR(255),
    county VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Create loan_applications table --
CREATE TABLE loan_applications (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    date_of_birth DATE NOT NULL,
    ssn VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    income DOUBLE PRECISION,
    income_type VARCHAR(255),
    requested_loan_amount DOUBLE PRECISION NOT NULL,
    address_id BIGINT NOT NULL,
    status VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_loan_applications_address FOREIGN KEY (address_id) REFERENCES addresses(id)
);

-- Create application_decisions table --
CREATE TABLE application_decisions (
    id BIGSERIAL PRIMARY KEY,
    loan_application_id BIGINT,
    encrypted_request TEXT,
    encrypted_request_iv VARCHAR(255),
    encrypted_response TEXT,
    encrypted_response_iv VARCHAR(255),
    decision VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_application_decisions_loan_application FOREIGN KEY (loan_application_id) REFERENCES loan_applications(id)
);

-- Create Active Storage tables  ---
-- Note: These are for file storage, may not be needed in Spring Boot unless you're using file uploads
CREATE TABLE active_storage_blobs (
    id BIGSERIAL PRIMARY KEY,
    key VARCHAR(255) NOT NULL,
    filename VARCHAR(255) NOT NULL,
    content_type VARCHAR(255),
    metadata TEXT,
    service_name VARCHAR(255) NOT NULL,
    byte_size BIGINT NOT NULL,
    checksum VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX index_active_storage_blobs_on_key ON active_storage_blobs(key);

CREATE TABLE active_storage_attachments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    record_type VARCHAR(255) NOT NULL,
    record_id BIGINT NOT NULL,
    blob_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_active_storage_attachments_blob FOREIGN KEY (blob_id) REFERENCES active_storage_blobs(id)
);

CREATE UNIQUE INDEX index_active_storage_attachments_uniqueness 
    ON active_storage_attachments(record_type, record_id, name, blob_id);
CREATE INDEX index_active_storage_attachments_on_blob_id ON active_storage_attachments(blob_id);

CREATE TABLE active_storage_variant_records (
    id BIGSERIAL PRIMARY KEY,
    blob_id BIGINT NOT NULL,
    variation_digest VARCHAR(255) NOT NULL,
    CONSTRAINT fk_active_storage_variant_records_blob FOREIGN KEY (blob_id) REFERENCES active_storage_blobs(id)
);

CREATE UNIQUE INDEX index_active_storage_variant_records_uniqueness 
    ON active_storage_variant_records(blob_id, variation_digest);