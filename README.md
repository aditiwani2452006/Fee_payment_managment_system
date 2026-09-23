# Fee Payment Management Platform (Review Build)
### Marathwada Mitra Mandal's College of Engineering (MMCOE, Pune)
**Academic Project Review Build**  

---

## 1. Project Overview & Scope

The **MMCOE Fee Payment Management Platform** is a structured, educational academic platform designed for college fee administration and verification. Built with Spring Boot (Java 21), PostgreSQL 18, and React (Vite), it provides an authentic institutional portal experience while demonstrating the practical application of foundational Computer Science disciplines.

> **Clean Academic Decoupling:** In compliance with evaluation requirements, all academic concept explanations and team contribution markers are maintained in documentation rather than displayed directly on user-facing portal interfaces.

---

## 2. System Architecture & Tech Stack

```
                     ┌─────────────────────────────────────────┐
                     │    MMCOE Client Portal (React + Vite)   │
                     │  - Pure CSS, Institutional Blue Palette │
                     │  - Zero Viva/Academic Clutter on UI     │
                     └────────────────────┬────────────────────┘
                                          │ HTTPS / REST (JSON)
                                          ▼
                     ┌─────────────────────────────────────────┐
                     │     Spring Boot 3 API Server (Java 21)  │
                     │  - Spring Security (JWT / RBAC)         │
                     │  - Pessimistic Row Locking (OS)         │
                     │  - Deterministic Rule-based AI Engine   │
                     └────────────────────┬────────────────────┘
                                          │ JDBC / Hibernate ORM
                                          ▼
                     ┌─────────────────────────────────────────┐
                     │      PostgreSQL 18 Database (3NF)       │
                     │  - 14 Relational Tables                 │
                     │  - ACID Transactions & Audit Trails     │
                     └─────────────────────────────────────────┘
```

- **Backend:** Java 21, Spring Boot 3.3, Spring Data JPA, Spring Security (JWT), PostgreSQL Driver.
- **Frontend:** React 18, Vite, Vanilla CSS design system.
- **Database:** PostgreSQL 18 (`feepay_db`) with 14 normalized tables.
- **Testing:** JUnit 5, Spring Boot Test, Testcontainers / H2.

---

## 3. Quick Start & Setup Instructions

### Prerequisites
- **Java 21** (JDK 21 installed)
- **Node.js** (v18+ installed)
- **PostgreSQL 18** running on `localhost:5432` with user `postgres` / password `postgres` (or configurable in `application.properties`)

### Step 1: Database Setup
Execute the DDL script to create the 14 tables and initial records:
```powershell
# In PowerShell:
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d feepay_db -f db/schema.sql
```

### Step 2: Run Backend Server
```powershell
cd backend
.\mvnw.cmd spring-boot:run
```
* Backend runs on: `http://localhost:8080`
* Swagger OpenAPI docs: `http://localhost:8080/swagger-ui/index.html`

### Step 3: Run Frontend Client
In a separate terminal:
```powershell
cd frontend
npm install
npm run dev
```
* Web Portal runs on: `http://localhost:5173`

---

## 4. Demo Accounts & Credentials

| Role | Username / PRN | Password | Demo Persona | Key Capabilities |
| :--- | :--- | :--- | :--- | :--- |
| **Student** | `B25IT2009` | `password123` | Saburi Yeola (Female OBC/EBC/EWS) | View auto-assigned fee (₹27,674), initiate payment order, apply for installment |
| **Accounts Officer** | `officer1` | `password123` | Accounts Section Officer | Review and approve/reject installment applications, inspect fee ledgers |
| **System Admin** | `admin1` | `password123` | Portal Administrator | Manage master fee catalogues, trigger backups, inspect audit & security logs |

---

## 5. Review Demonstration Sequence (Steps 1 to 14)

1. **Official Branding & Login:** Navigate to `http://localhost:5173/`. Verify the official circular Marathwada Mitra Mandal logo (Estd. 1967) is displayed.
2. **Student Authentication:** Click `Student (Saburi) B25IT2009` and sign in.
3. **Student Information Ribbon:** Verify the ribbon shows `SABURI YEOLA | B25IT2009 | B.TECH. IT | Active`.
4. **Category Fee Auto-Assignment:** In *Fee Summary*, observe the pre-assigned net fee of **₹27,674** calculated automatically from her admission category (`OBC/EBC/EWS/SEBC Female`).
5. **Fee Breakdown Catalogue:** Switch to *Category Fee Structure* tab to inspect itemized heads: Tuition Fee (₹0 after 100% concession), Development Fee (₹20,000), Other Fees (₹7,174), Caution Deposit (₹500).
6. **Payment Initiation:** In *Fee Summary*, click **Pay Now**. Observe that the system initiates a payment order with reference `MMCOE-PAY-REQ-...` in status `PENDING_GATEWAY_INTEGRATION` without simulating fake completed transactions.
7. **Installment Application:** Open *Installment Request* tab, enter reason *"Financial liquidity split request"*, choose 2 installments, and submit.
8. **Navigation & Home Action:** Click the **Home** button in the top navigation bar to return immediately to the primary fee overview.
9. **Password Self-Service:** Open the user profile dropdown, click *Change Password*, and observe the modal dialog.
10. **Officer Login:** Log out and sign in as `officer1` / `password123`.
11. **Installment Review:** In *Installment Requests*, review Saburi's pending request, click **Review**, and click **Approve Request (2 Splits)**.
12. **Admin Login:** Log out and sign in as `admin1` / `password123`.
13. **Master Fee Catalogue Administration:** Verify 6 official MMCOE fee structures are active and stored in PostgreSQL.
14. **Audit Logs & Backup:** Inspect real-time audit trail logs tracking all actions, and trigger a database snapshot.

---

## 6. Comprehensive Viva Examination Defense Guide (30 Questions & Answers)

### Section A: Object-Oriented Programming (OOP)
1. **Q: How is encapsulation implemented in the financial ledger?**  
   *A:* In `FeeAssignment.java` and `Student.java`, all monetary balances (`totalAmount`, `paidAmount`, `outstandingAmount`) are declared `private`. They cannot be directly overwritten from outside; balance mutations must pass through validated domain methods (`applyPayment()`), preserving financial invariants.
2. **Q: Where is inheritance utilized in the codebase?**  
   *A:* In our repository and service layers, Spring Data JPA interfaces inherit from `JpaRepository<T, ID>`, providing built-in CRUD and pagination abstractions.
3. **Q: How does polymorphism support modular development?**  
   *A:* Service classes implement business interfaces. At runtime, Spring's Inversion of Control (IoC) container injects transactional proxy instances implementing the interface contract.
4. **Q: How are domain invariants enforced?**  
   *A:* By defensive setter validation and JPA entity pre-persist hooks (`@PrePersist`, `@PreUpdate`) ensuring amounts never drop below zero.

### Section B: Database Management Systems (DBMS)
5. **Q: What is the normalization level of the relational schema?**  
   *A:* The schema is in Third Normal Form (3NF). Every non-key column is strictly dependent on the primary key, whole key, and nothing but the key, eliminating transitive dependencies.
6. **Q: How are ACID properties guaranteed during payment operations?**  
   *A:* Methods in `PaymentService` are annotated with `@Transactional(isolation = Isolation.READ_COMMITTED)`. In the event of an unexpected exception, all ledger updates and order inserts are rolled back atomically.
7. **Q: How does automatic category fee assignment work?**  
   *A:* `FeeAssignmentService.assignFeeToStudent()` queries `FeeStructureRepository` using the student's category enum (`OPEN`, `OBC/EBC/EWS/SEBC Female`, `SC/ST`, etc.) and automatically populates the fee assignment with exact itemized heads.
8. **Q: Why does the payment table maintain a direct student_id foreign key?**  
   *A:* To enforce non-repudiation and permanent auditability. Even if an installment plan is modified or deleted, the financial payment remains permanently linked to the student.
9. **Q: How are database indexes used?**  
   *A:* B-Tree indexes are created on `prn`, `username`, and `transaction_reference` to ensure $O(1)$ to $O(\log N)$ query lookups.

### Section C: Operating Systems (OS)
10. **Q: How does the system handle concurrent payments on the same fee record?**  
    *A:* Using pessimistic row-level locking via JPA (`@Lock(LockModeType.PESSIMISTIC_WRITE)` / SQL `SELECT ... FOR UPDATE`). When a payment transaction starts, the database locks the specific fee assignment row, blocking concurrent threads until the transaction completes.
11. **Q: What happens if two threads try to pay simultaneously?**  
    *A:* The second thread waits on the database row lock. Once the first thread commits and updates the balance, the second thread reads the newly updated balance and correctly computes the remaining due.
12. **Q: How does Tomcat handle multi-threaded client requests?**  
    *A:* Spring Boot uses Tomcat's worker thread pool (`ThreadPoolExecutor`). Each incoming HTTP connection is dispatched to an independent thread from the pool.
13. **Q: Are background operations thread-isolated?**  
    *A:* Yes, scheduled tasks (such as automated backup checks) execute on an isolated `ScheduledExecutorService` pool, preventing CPU starvation of student web traffic.

### Section D: Computer Networks (CN)
14. **Q: Why was JWT chosen over server-side sessions?**  
    *A:* JWT provides stateless authentication. The server does not maintain session state in memory; claims and roles are cryptographically signed using HMAC-SHA256, enabling horizontal scaling.
15. **Q: How is Role-Based Access Control (RBAC) enforced?**  
    *A:* The `JwtAuthenticationFilter` intercepts requests, validates token signatures, and populates Spring's `SecurityContext`. Controller endpoints are secured using `@PreAuthorize` or request matchers (`ROLE_STUDENT`, `ROLE_ACCOUNTS_OFFICER`, `ROLE_ADMIN`).
16. **Q: What happens if a student attempts to access admin APIs?**  
    *A:* Spring Security immediately aborts request execution and issues an HTTP `403 Forbidden` response.
17. **Q: What HTTP status codes are used across the REST API?**  
    *A:* `200 OK` (successful retrieval/update), `201 Created` (new resource created), `400 Bad Request` (validation failure), `401 Unauthorized` (missing/invalid JWT), `403 Forbidden` (role violation), `404 Not Found` (entity missing), and `409 Conflict` (duplicate request).

### Section E: Data Structures & Algorithms (DSA)
18. **Q: Which data structure is used for fast receipt verification?**  
    *A:* Hash table indexing ($O(1)$ average complexity) on transaction reference keys.
19. **Q: What sorting algorithm is used for transaction ledger views?**  
    *A:* Dual-Pivot Quicksort / TimSort with $O(N \log N)$ average complexity for chronological and amount-based sorting.
20. **Q: How are installment amounts calculated?**  
    *A:* An integer division algorithm divides total dues across $N$ installments, assigning any remainder cents/pennies to the final installment to avoid rounding loss.
21. **Q: What data structures represent audit logs in memory?**  
    *A:* Contiguous memory dynamic arrays (`ArrayList`) for sequential iteration and JSON serialization.

### Section F: Software Engineering (SE)
22. **Q: What architectural design pattern is used?**  
    *A:* Layered Architecture (Presentation Layer $\rightarrow$ Controller Layer $\rightarrow$ Service Layer $\rightarrow$ Data Access / Repository Layer $\rightarrow$ Database).
23. **Q: How is separation of concerns achieved?**  
    *A:* Controllers only handle HTTP translation and validation; Services handle business rules and transactions; Repositories handle database persistence.
24. **Q: How is the system defended against SQL injection?**  
    *A:* By using parameterized queries and JPA/Hibernate prepared statements; no string concatenation is used in queries.
25. **Q: How does defensive programming prevent bad data?**  
    *A:* Java Bean Validation (`@NotNull`, `@NotBlank`, `@Positive`) validates DTO payloads at the API boundary before passing to domain services.

### Section G: Basic Rule-Based AI
26. **Q: Why is a rule-based indicator used instead of a neural network?**  
    *A:* In educational fee administration, decisions must be 100% deterministic, explainable, and transparent to college staff and students. Complex deep learning black-box models are inappropriate and over-engineered for this task.
27. **Q: What features are evaluated by the risk indicator?**  
    *A:* Days overdue past academic deadline, total outstanding balance, and history of unfulfilled installment commitments.
28. **Q: What output does the risk assessment generate?**  
    *A:* A categorical risk classification (`LOW`, `MEDIUM`, `HIGH`) and a clear administrative recommendation (e.g. *"Payment Follow-up Required (Likely Fee Default)"*).
29. **Q: Where is this indicator displayed?**  
    *A:* In the Administrator and Accounts dashboards under the Predictive Follow-up section.
30. **Q: Can the risk rules be customized by the college?**  
    *A:* Yes, threshold parameters (days overdue limit, minimum balance threshold) are decoupled in configuration.

---

## 7. Additional Project Documentation
- [FUNDAMENTAL_CONCEPTS.md](file:///c:/Users/SURESH%20RAMDAS%20YEOLA/OneDrive/Desktop/edi1/FUNDAMENTAL_CONCEPTS.md): Complete curriculum mapping across OOP, DBMS, OS, CN, SE, DSA, and Basic AI.
- [REVIEW_STATUS.md](file:///c:/Users/SURESH%20RAMDAS%20YEOLA/OneDrive/Desktop/edi1/REVIEW_STATUS.md): Explicit breakdown of WORKING features vs. PENDING subsequent-phase modules.
