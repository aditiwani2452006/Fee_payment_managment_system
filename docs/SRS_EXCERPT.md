# Software Requirements Specification (SRS) Excerpt
## College Fee Payment Management Platform
**Institution:** Marathwada Mitra Mandal's College of Engineering (MMCOE, Pune)  
**Department:** Information Technology  
**Evaluation Scope:** Mid-Semester Project Review (Half-Implementation Build)  
**Target Architecture:** Monorepo with Spring Boot REST Backend, React Frontend, PostgreSQL Database.

---

## 1. System Roles and Access Matrix

| Role | Scope & Description | Key Permissions |
|---|---|---|
| **Student** | Enrolled undergraduate student authenticated via college credentials. | View applicable fee structure, view ledger dues, submit installment applications, initiate simulated payments via Razorpay Test Mode, view installment schedules, download verified receipts. |
| **Accounts Officer** | Financial administrative authority responsible for cash flow governance. | Review, approve, or reject student installment applications; verify incoming payment transactions; trigger payment rollbacks (designed stub); generate fee collection, pending dues, and departmental financial reports. |
| **Admin** | System and IT administrator maintaining baseline platform operations. | Provision and manage user accounts; configure course/semester fee structures; inspect system audit logs and security telemetry; initiate database backup and restore operations. |

---

## 2. Functional Requirements (FR1–FR15) Scope Breakdown

Target: **~60% Working Slices / ~40% Designed Stubs** per team.

| FR # | Functional Requirement | Team | Implementation Status | Core CS Concept Demonstrated |
|---|---|---|---|---|
| **FR1** | **Student Account Provisioning** | Team 1 | **Working Slice** | SE (Lifecycle Provisioning), DBMS (Referential Integrity) |
| **FR2** | **Login Authentication** | Team 1 | **Working Slice** | CN (Stateless JWT Tokens, Authorization Headers) |
| **FR3** | **Role-Based Access Control (RBAC)** | Team 1 | **Working Slice** | CN & OS (Access Control Lists, Spring Security Gating) |
| **FR4** | **Manage Student Profile** | Team 1 | **Working Slice** | OOP (Encapsulation of sensitive academic & contact data) |
| **FR5** | **Manage Fee Structure** | Team 1 | **Working Slice** | DBMS (Catalogue configuration, temporal validity) |
| **FR6** | **View Fee Structure & Outstanding Dues**| Team 1 | **Working Slice** | DBMS (Relational joins across assignments and structures) |
| **FR7** | **Apply for Installment Plan** | Team 1 | **Working Slice** | SE (Workflow State Transitions) |
| **FR8** | **Review Installment Requests** | Team 2 | **Working Slice** | DBMS (Schedule calculation, multi-row insertion) |
| **FR9** | **View Installment Status** | Team 2 | **Working Slice** | OOP (Domain entity representation of payment intervals) |
| **FR10** | **Initiate Fee Payment** | Team 2 | **Working Slice** | OS (Idempotent order generation, race condition prevention) |
| **FR11** | **Process Payment (Razorpay Test Mode)** | Team 2 | **Working Slice** | CN (External REST Gateway communication, Webhooks) |
| **FR12** | **Verify Payment & Generate Receipt** | Team 2 | **Working Slice** | DBMS & DSA (ACID transactional write, Receipt hash-lookup) |
| **FR13** | **Track Fee Status & Payment History** | Team 2 | **Working Slice** | DSA (In-memory sorting & searching on payment lists) |
| **FR-Stub**| **Payment Rollback Flow** | Team 2 | **Designed Stub** | OS/DBMS (State Machine: `INITIATED→PROCESSING→SUCCESS/FAILED→ROLLED_BACK`) |
| **FR14** | **Generate Reports** | Team 3 | **Working Slice** | DBMS & OOP (Aggregation queries, polymorphic reporting) |
| **FR15** | **Admin Dashboard & Audit Logs** | Team 3 | **Working Slice** | SE & CN (Non-repudiation audit trails, security telemetry) |
| **FR-Stub**| **Database Backup & Disaster Recovery** | Team 3 | **Designed Stub** | OS & DBMS (`pg_dump` wrapper, `BackupHistory` audit record) |
| **FR-AI** | **Coursework AI Risk Scoring** | Team 3 | **Designed Stub / Add-on** | AI/DSA (Rule-based fee-default risk indicator for academic viva) |

---

## 3. Non-Functional Requirements (NFRs)

1. **Security (NFR-SEC)**:
   - Authentication via signed JSON Web Tokens (JJWT) with HMAC-SHA256 signature.
   - Passwords hashed using standard BCrypt with cost factor 10.
   - All REST endpoints gated by `@PreAuthorize("hasRole(...)")`.
2. **Concurrency & Thread Safety (NFR-CON)**:
   - Must handle at least 10 simultaneous payment requests without race conditions or dirty reads.
   - Implemented using Pessimistic Database Row Locking (`SELECT ... FOR UPDATE`) on the target `FeeAssignment` and `FeeInstallment` records.
3. **Idempotency (NFR-IDM)**:
   - Duplicate payment submission prevention enforced via unique constraint on `Transaction.gateway_reference`.
4. **Traceability (NFR-TRC)**:
   - Direct `student_id` foreign key on `FeePayment` allows disputed transactions to be traced immediately without complex relational joins.
   - All state mutations recorded in `AuditLog`.
