-- ============================================================================
-- College: Marathwada Mitra Mandal's College of Engineering (MMCOE, Pune)
-- Department: Information Technology
-- Project: Fee Payment Management Platform (Half-Implementation Review Build)
-- Schema: SRS Section 10 & Official ER Diagram (14 Relational Tables)
-- ============================================================================

-- Drop existing tables in reverse dependency order if recreating
DROP TABLE IF EXISTS BackupHistory CASCADE;
DROP TABLE IF EXISTS SecurityLog CASCADE;
DROP TABLE IF EXISTS AuditLog CASCADE;
DROP TABLE IF EXISTS Report CASCADE;
DROP TABLE IF EXISTS PaymentGatewayLog CASCADE;
DROP TABLE IF EXISTS Receipt CASCADE;
DROP TABLE IF EXISTS Transaction CASCADE;
DROP TABLE IF EXISTS FeePayment CASCADE;
DROP TABLE IF EXISTS FeeInstallment CASCADE;
DROP TABLE IF EXISTS FeeAssignment CASCADE;
DROP TABLE IF EXISTS FeeStructure CASCADE;
DROP TABLE IF EXISTS User_Account CASCADE;
DROP TABLE IF EXISTS Student CASCADE;
DROP TABLE IF EXISTS Role CASCADE;

-- ----------------------------------------------------------------------------
-- 1. Role: System access levels (ROLE_STUDENT, ROLE_ACCOUNTS_OFFICER, ROLE_ADMIN)
-- ----------------------------------------------------------------------------
CREATE TABLE Role (
    role_id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

-- ----------------------------------------------------------------------------
-- 2. Student: Core student profile details provisioned by college administration
-- ----------------------------------------------------------------------------
CREATE TABLE Student (
    student_id BIGSERIAL PRIMARY KEY,
    prn VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    course VARCHAR(50) NOT NULL,
    department VARCHAR(50) NOT NULL DEFAULT 'Information Technology',
    semester INT NOT NULL,
    academic_year VARCHAR(20) NOT NULL DEFAULT '2026-2027',
    category VARCHAR(50) NOT NULL, -- OPEN, OBC/EBC/EWS/SEBC Male, SC/ST, VJNT/SBC/TFWS, J & K Quota, OBC/EBC/EWS/SEBC Female
    email VARCHAR(100) NOT NULL UNIQUE,
    contact_number VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Active',
    admission_date DATE DEFAULT '2023-08-01',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- 3. User: Authentication credentials and role mapping (SRS: User table)
-- ----------------------------------------------------------------------------
CREATE TABLE User_Account (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL,
    student_id BIGINT NULL,
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES Role (role_id) ON DELETE RESTRICT,
    CONSTRAINT fk_user_student FOREIGN KEY (student_id) REFERENCES Student (student_id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------------
-- 4. FeeStructure: Master fee catalogue configured by Admin
-- ----------------------------------------------------------------------------
CREATE TABLE FeeStructure (
    fee_id BIGSERIAL PRIMARY KEY,
    department VARCHAR(50) NOT NULL,
    semester INT NOT NULL,
    category VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    fee_type VARCHAR(50) NOT NULL,
    tuition_fee NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    development_fee NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    other_fees NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    caution_money NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    amount NUMERIC(10, 2) NOT NULL CHECK (amount >= 0),
    due_date DATE NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- 5. FeeAssignment: Links student to fee structure and tracks financial balances
-- ----------------------------------------------------------------------------
CREATE TABLE FeeAssignment (
    assignment_id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    fee_structure_id BIGINT NOT NULL,
    total_amount NUMERIC(10, 2) NOT NULL CHECK (total_amount >= 0),
    paid_amount NUMERIC(10, 2) NOT NULL DEFAULT 0.00 CHECK (paid_amount >= 0),
    outstanding_amount NUMERIC(10, 2) NOT NULL CHECK (outstanding_amount >= 0),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, PARTIALLY_PAID, PAID, INSTALLMENT_REQUESTED, INSTALLMENT_APPROVED
    CONSTRAINT fk_fee_assign_student FOREIGN KEY (student_id) REFERENCES Student (student_id) ON DELETE RESTRICT,
    CONSTRAINT fk_fee_assign_structure FOREIGN KEY (fee_structure_id) REFERENCES FeeStructure (fee_id) ON DELETE RESTRICT
);

-- ----------------------------------------------------------------------------
-- 6. FeeInstallment: Schedule generated when installment application is approved
-- ----------------------------------------------------------------------------
CREATE TABLE FeeInstallment (
    installment_id BIGSERIAL PRIMARY KEY,
    assignment_id BIGINT NOT NULL,
    installment_no INT NOT NULL,
    due_date DATE NOT NULL,
    installment_amount NUMERIC(10, 2) NOT NULL CHECK (installment_amount > 0),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, PAID, OVERDUE
    CONSTRAINT fk_installment_assignment FOREIGN KEY (assignment_id) REFERENCES FeeAssignment (assignment_id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------------
-- 7. FeePayment: Core payment attempt ledger
-- ----------------------------------------------------------------------------
CREATE TABLE FeePayment (
    payment_id BIGSERIAL PRIMARY KEY,
    installment_id BIGINT NULL,
    student_id BIGINT NOT NULL,
    amount_paid NUMERIC(10, 2) NOT NULL CHECK (amount_paid > 0),
    payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    payment_method VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_GATEWAY_INTEGRATION',
    CONSTRAINT fk_payment_installment FOREIGN KEY (installment_id) REFERENCES FeeInstallment (installment_id) ON DELETE SET NULL,
    CONSTRAINT fk_payment_student FOREIGN KEY (student_id) REFERENCES Student (student_id) ON DELETE RESTRICT
);

-- ----------------------------------------------------------------------------
-- 8. Transaction: Technical gateway transaction record with verification state
-- ----------------------------------------------------------------------------
CREATE TABLE Transaction (
    transaction_id BIGSERIAL PRIMARY KEY,
    payment_id BIGINT NOT NULL UNIQUE,
    transaction_reference VARCHAR(100) NOT NULL UNIQUE,
    gateway_name VARCHAR(50) NOT NULL DEFAULT 'RAZORPAY_TEST',
    transaction_status VARCHAR(30) NOT NULL DEFAULT 'SUCCESS',
    transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    amount NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    verified_by BIGINT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_txn_payment FOREIGN KEY (payment_id) REFERENCES FeePayment (payment_id) ON DELETE CASCADE,
    CONSTRAINT fk_txn_officer FOREIGN KEY (verified_by) REFERENCES User_Account (user_id) ON DELETE SET NULL
);

-- ----------------------------------------------------------------------------
-- 9. Receipt: Official proof-of-payment document metadata
-- ----------------------------------------------------------------------------
CREATE TABLE Receipt (
    receipt_id BIGSERIAL PRIMARY KEY,
    transaction_id BIGINT NOT NULL UNIQUE,
    student_id BIGINT NULL,
    receipt_number VARCHAR(50) NOT NULL UNIQUE,
    generated_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    receipt_url VARCHAR(255) NULL,
    CONSTRAINT fk_receipt_txn FOREIGN KEY (transaction_id) REFERENCES Transaction (transaction_id) ON DELETE CASCADE,
    CONSTRAINT fk_receipt_student FOREIGN KEY (student_id) REFERENCES Student (student_id) ON DELETE RESTRICT
);

-- ----------------------------------------------------------------------------
-- 10. PaymentGatewayLogs: Raw audit payload of webhooks and API calls
-- ----------------------------------------------------------------------------
CREATE TABLE PaymentGatewayLog (
    gateway_log_id BIGSERIAL PRIMARY KEY,
    transaction_id BIGINT NULL,
    gateway_name VARCHAR(50) NOT NULL,
    request_data TEXT,
    response_data TEXT,
    status VARCHAR(20) NOT NULL,
    log_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_gateway_txn FOREIGN KEY (transaction_id) REFERENCES Transaction (transaction_id) ON DELETE SET NULL
);

-- ----------------------------------------------------------------------------
-- 11. Report: Snapshots of collection summaries generated by Admin/Accounts
-- ----------------------------------------------------------------------------
CREATE TABLE Report (
    report_id BIGSERIAL PRIMARY KEY,
    report_type VARCHAR(50) NOT NULL,
    from_date DATE NOT NULL,
    to_date DATE NOT NULL,
    generated_by BIGINT NOT NULL,
    file_path VARCHAR(255) NULL,
    generated_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_report_user FOREIGN KEY (generated_by) REFERENCES User_Account (user_id) ON DELETE RESTRICT
);

-- ----------------------------------------------------------------------------
-- 12. AuditLogs: General non-repudiation administrative and state mutation logs
-- ----------------------------------------------------------------------------
CREATE TABLE AuditLog (
    audit_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NULL,
    action VARCHAR(100) NOT NULL,
    description TEXT,
    ip_address VARCHAR(45) DEFAULT '127.0.0.1',
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES User_Account (user_id) ON DELETE SET NULL
);

-- ----------------------------------------------------------------------------
-- 13. SecurityLogs: Security boundary events, failed logins, and access violations
-- ----------------------------------------------------------------------------
CREATE TABLE SecurityLog (
    log_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NULL,
    event_type VARCHAR(50) NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_security_user FOREIGN KEY (user_id) REFERENCES User_Account (user_id) ON DELETE SET NULL
);

-- ----------------------------------------------------------------------------
-- 14. BackupHistory: Administrative logs of periodic schema/data snapshots
-- ----------------------------------------------------------------------------
CREATE TABLE BackupHistory (
    backup_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    backup_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    backup_location VARCHAR(255) NOT NULL,
    backup_status VARCHAR(20) NOT NULL,
    remarks TEXT,
    CONSTRAINT fk_backup_user FOREIGN KEY (user_id) REFERENCES User_Account (user_id) ON DELETE RESTRICT
);

-- Indexes for performance optimization (DBMS Concept)
CREATE INDEX idx_student_prn ON Student(prn);
CREATE INDEX idx_user_username ON User_Account(username);
CREATE INDEX idx_fee_assign_student ON FeeAssignment(student_id);
CREATE INDEX idx_payment_student ON FeePayment(student_id);
CREATE INDEX idx_receipt_number ON Receipt(receipt_number);
CREATE INDEX idx_audit_timestamp ON AuditLog(timestamp);
