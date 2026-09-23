-- ============================================================================
-- College: MMCOE, Pune - Dept. of Information Technology
-- Project: Fee Payment Management Platform
-- Seed Data: Roles, Users, Students, Fee Structures, Initial Assignments
-- Default passwords for testing: 'password123'
-- BCrypt: $2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.twUOXYWBcGyOS
-- ============================================================================

-- 1. Insert Roles (Team 1)
INSERT INTO Role (role_id, role_name) VALUES
(1, 'ROLE_STUDENT'),
(2, 'ROLE_ACCOUNTS_OFFICER'),
(3, 'ROLE_ADMIN')
ON CONFLICT (role_id) DO NOTHING;

-- 2. Insert Students (Team 1)
INSERT INTO Student (student_id, name, course, semester, email, contact_number) VALUES
(1, 'Aarav Sharma', 'B.E. Information Technology', 6, 'aarav.sharma@mmcoe.edu.in', '9876543210'),
(2, 'Priya Patel', 'B.E. Information Technology', 6, 'priya.patel@mmcoe.edu.in', '9876543211'),
(3, 'Rohan Kulkarni', 'B.E. Computer Engineering', 6, 'rohan.kulkarni@mmcoe.edu.in', '9876543212')
ON CONFLICT (student_id) DO NOTHING;

-- 3. Insert Users (Team 1)
-- Credentials:
-- student1 / password123 (ROLE_STUDENT)
-- student2 / password123 (ROLE_STUDENT)
-- officer1 / password123 (ROLE_ACCOUNTS_OFFICER)
-- admin1 / password123   (ROLE_ADMIN)
INSERT INTO User_Account (user_id, username, password_hash, role_id, student_id) VALUES
(1, 'student1', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.twUOXYWBcGyOS', 1, 1),
(2, 'student2', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.twUOXYWBcGyOS', 1, 2),
(3, 'officer1', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.twUOXYWBcGyOS', 2, NULL),
(4, 'admin1',   '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.twUOXYWBcGyOS', 3, NULL)
ON CONFLICT (user_id) DO NOTHING;

-- 4. Insert Fee Structures (Team 1 - FR5)
INSERT INTO FeeStructure (fee_id, department, semester, fee_type, amount, due_date, academic_year, description, status) VALUES
(1, 'Information Technology', 6, 'TUITION_FEE', 65000.00, '2026-10-31', '2026-2027', 'Standard Semester 6 Tuition Fee', 'ACTIVE'),
(2, 'Information Technology', 6, 'DEVELOPMENT_FEE', 15000.00, '2026-10-31', '2026-2027', 'Campus & Lab Infrastructure Fee', 'ACTIVE'),
(3, 'Information Technology', 6, 'EXAMINATION_FEE', 3500.00, '2026-11-15', '2026-2027', 'SPPU Semester 6 Examination Fee', 'ACTIVE'),
(4, 'Computer Engineering', 6, 'TUITION_FEE', 65000.00, '2026-10-31', '2026-2027', 'Standard Semester 6 Tuition Fee', 'ACTIVE')
ON CONFLICT (fee_id) DO NOTHING;

-- 5. Insert Fee Assignments (Team 2 - Seeded link between student and fee structure)
-- Aarav Sharma has assigned Tuition Fee (65,000)
INSERT INTO FeeAssignment (assignment_id, student_id, fee_structure_id, total_amount, paid_amount, outstanding_amount, status) VALUES
(1, 1, 1, 65000.00, 0.00, 65000.00, 'UNPAID'),
(2, 1, 2, 15000.00, 0.00, 15000.00, 'UNPAID'),
(3, 2, 1, 65000.00, 32500.00, 32500.00, 'PARTIALLY_PAID')
ON CONFLICT (assignment_id) DO NOTHING;

-- 6. Insert Initial Installments for Priya Patel (Already partially paid demo)
INSERT INTO FeeInstallment (installment_id, assignment_id, installment_no, due_date, installment_amount, status) VALUES
(1, 3, 1, '2026-08-30', 32500.00, 'PAID'),
(2, 3, 2, '2026-10-15', 32500.00, 'PENDING')
ON CONFLICT (installment_id) DO NOTHING;

-- 7. Insert Existing Payment & Receipt (Team 2)
INSERT INTO FeePayment (payment_id, installment_id, student_id, amount_paid, payment_date, payment_method, status) VALUES
(1, 1, 2, 32500.00, '2026-08-28 11:30:00', 'RAZORPAY_TEST', 'SUCCESS')
ON CONFLICT (payment_id) DO NOTHING;

INSERT INTO Transaction (transaction_id, payment_id, transaction_date, status, gateway_reference) VALUES
(1, 1, '2026-08-28 11:30:05', 'SUCCESS', 'pay_test_priya_inst1_982347')
ON CONFLICT (transaction_id) DO NOTHING;

INSERT INTO Receipt (receipt_id, transaction_id, receipt_number, generated_date) VALUES
(1, 1, 'MMCOE-2026-RCP-0001', '2026-08-28 11:30:10')
ON CONFLICT (receipt_id) DO NOTHING;

-- 8. Seed Audit Log (Team 3 - FR15)
INSERT INTO AuditLog (audit_id, user_id, action, timestamp) VALUES
(1, 4, 'INITIALIZED_SYSTEM_SCHEMA', CURRENT_TIMESTAMP),
(2, 4, 'CREATED_FEE_STRUCTURE_SEM6', CURRENT_TIMESTAMP),
(3, 1, 'STUDENT_LOGGED_IN', CURRENT_TIMESTAMP)
ON CONFLICT (audit_id) DO NOTHING;

-- Reset sequence values for PostgreSQL so subsequent inserts work seamlessly
SELECT setval('role_role_id_seq', (SELECT MAX(role_id) FROM Role));
SELECT setval('student_student_id_seq', (SELECT MAX(student_id) FROM Student));
SELECT setval('user_account_user_id_seq', (SELECT MAX(user_id) FROM User_Account));
SELECT setval('feestructure_fee_id_seq', (SELECT MAX(fee_id) FROM FeeStructure));
SELECT setval('feeassignment_assignment_id_seq', (SELECT MAX(assignment_id) FROM FeeAssignment));
SELECT setval('feeinstallment_installment_id_seq', (SELECT MAX(installment_id) FROM FeeInstallment));
SELECT setval('feepayment_payment_id_seq', (SELECT MAX(payment_id) FROM FeePayment));
SELECT setval('transaction_transaction_id_seq', (SELECT MAX(transaction_id) FROM Transaction));
SELECT setval('receipt_receipt_id_seq', (SELECT MAX(receipt_id) FROM Receipt));
SELECT setval('auditlog_audit_id_seq', (SELECT MAX(audit_id) FROM AuditLog));
