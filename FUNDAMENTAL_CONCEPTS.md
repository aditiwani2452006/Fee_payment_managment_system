# Marathwada Mitra Mandal's College of Engineering, Pune
## Fee Payment Management Platform — Fundamental CS Concepts Mapping

This document provides a comprehensive technical mapping of the core Computer Science & Engineering concepts implemented across the Fee Payment Management Platform. All concepts are cleanly decoupled from the end-user interface to maintain an authentic, production-grade ERP portal experience.

---

### 1. Object-Oriented Programming (OOP)
* **Encapsulation:**
  * All domain models (`Student`, `User`, `FeeAssignment`, `Transaction`, `Receipt`, `FeeStructure`, `AuditLog`) maintain strictly `private` fields accessible only through controlled getter and setter methods or builder patterns.
  * Validation rules (e.g., non-negative fee amounts, PRN format validation) are enforced at the service boundary.
* **Inheritance & Abstraction:**
  * Spring Data JPA `JpaRepository<T, ID>` interfaces abstract all lower-level SQL interactions and database drivers.
  * Custom exception hierarchies (`BadCredentialsException`, `ResourceNotFoundException`) inherit from standard runtime exception abstractions.
* **Polymorphism:**
  * Interface-driven architecture separating Service interfaces from implementation classes (e.g., Spring Dependency Injection injecting runtime proxy implementations).
  * Method overloading for flexible querying in repository interfaces.

---

### 2. Database Management Systems (DBMS)
* **Relational Schema Design & 3NF Normalization:**
  * Clean 14-table schema (`users`, `students`, `fee_structure`, `fee_assignments`, `transactions`, `receipts`, `installment_plans`, `audit_logs`, `security_logs`, `fee_heads`, `concessions`, `system_backups`, etc.).
  * Third Normal Form (3NF) design guarantees no transitive functional dependencies and eliminates data anomalies (insertion, update, deletion).
* **ACID Transactions:**
  * Payment initiation, fee deduction, and receipt generation operate within `@Transactional(isolation = Isolation.READ_COMMITTED)` boundaries.
  * Rollback operations (`PaymentService.rollbackPayment`) guarantee that if an error occurs mid-operation, the ledger and student balance are restored to their pre-transaction state without partial writes.
* **Automatic Category-Based Fee Assignment:**
  * Managed by `FeeAssignmentService`: maps student admission categories (`OPEN`, `OBC/EBC/EWS/SEBC Female`, `SC/ST`, `VJNT/SBC/TFWS`, `J & K Quota`) to official fee catalogue rows, computing exact fee concessions without manual data entry.
* **Indexing & Referential Integrity:**
  * Unique indexes on `students.prn`, `users.username`, and `transactions.transaction_reference` ensure $O(1)$ lookups.
  * Foreign key constraints (`ON DELETE CASCADE` or `RESTRICT`) preserve relational integrity between students, assignments, and payments.

---

### 3. Operating Systems (OS)
* **Concurrency & Race Condition Prevention:**
  * Prevents the classic "double payment" or lost update anomaly when a student clicks "Pay Now" multiple times or across multiple browser tabs simultaneously.
  * Implemented using database row-level locking via JPA pessimistic write locking (`@Lock(LockModeType.PESSIMISTIC_WRITE)` / `SELECT ... FOR UPDATE`). Concurrent threads attempting to modify the same fee record are queued sequentially until the transaction commits.
* **Process & Thread Management:**
  * Tomcat servlet thread pool (`org.apache.tomcat.util.threads.ThreadPoolExecutor`) processes incoming client HTTP requests in parallel worker threads.
  * Daemon and background scheduled tasks (such as automated nightly backup triggers) execute on isolated scheduled executor pools.

---

### 4. Computer Networks (CN)
* **RESTful Architecture:**
  * Standard HTTP verbs (`GET`, `POST`, `PUT`, `DELETE`) with proper HTTP status codes (`200 OK`, `201 Created`, `400 Bad Request`, `401 Unauthorized`, `403 Forbidden`, `404 Not Found`).
* **Stateless Client-Server Model & JWT:**
  * Cross-site authentication uses JSON Web Tokens (JWT) signed with HMAC-SHA256 (`HS256`).
  * No server-side session state is stored in memory, enabling seamless horizontal scalability and cloud compliance.
* **Role-Based Access Control (RBAC):**
  * Spring Security filters intercept every HTTP request via `JwtAuthenticationFilter` and validate claims before dispatching to controllers.
  * Endpoints are protected by authorities: `ROLE_STUDENT`, `ROLE_ACCOUNTS_OFFICER`, and `ROLE_ADMIN`. If a student attempts to invoke `/api/admin/**`, the network filter immediately returns `403 Forbidden`.

---

### 5. Data Structures & Algorithms (DSA)
* **Hash Maps ($O(1)$ Average Lookup):**
  * In-memory cache structures and lookups by PRN or Username utilize hash table implementations (`HashMap`, `ConcurrentHashMap`) for constant-time $O(1)$ lookups.
* **Self-Balancing Binary Trees / B-Trees ($O(\log N)$):**
  * PostgreSQL underlying indexes on primary keys and foreign keys use B-Tree data structures, ensuring logarithmic $O(\log N)$ lookup, insertion, and range query performance.
* **Sorting Algorithms ($O(N \log N)$):**
  * Ledger sorting by transaction timestamp or fee amount leverages Dual-Pivot Quicksort / TimSort implementations.

---

### 6. Software Engineering (SE)
* **Layered Architectural Pattern (Separation of Concerns):**
  * **Presentation Layer:** React single-page application with responsive CSS components.
  * **API / Controller Layer:** Spring Boot `@RestController` classes handling DTO mapping and input validation.
  * **Service Layer:** Pure business logic, transaction boundaries, and fee computation rules.
  * **Data Access Layer:** Spring Data JPA repositories with typed query generation.
  * **Persistence Layer:** PostgreSQL relational database.
* **Defensive Programming & Validation:**
  * Bean Validation (`@NotNull`, `@NotBlank`, `@Positive`) prevents malformed data payloads from reaching persistent storage.
  * Centralized global exception handler (`@RestControllerAdvice`) maps internal errors to structured JSON responses without exposing raw stack traces.
* **Auditability & Traceability:**
  * Every sensitive operation (login, payment order initiation, password change, rollback) generates an immutable record in `audit_logs` with username, client IP, action type, and UTC timestamp.

---

### 7. Fundamental Rule-Based AI (Explainable Heuristics)
* **Coursework Scope:**
  * Faculty guidelines strictly require simple, transparent, and explainable AI logic rather than opaque, uninterpretable deep neural networks.
* **Implementation (`FeeDefaultRiskService`):**
  * Evaluates multi-factor financial indicators:
    1. Outstanding fee balance magnitude.
    2. Overdue duration relative to academic due dates.
    3. Installment payment compliance history.
  * When criteria thresholds are exceeded, the deterministic rule engine assigns a risk score and generates an actionable predictive recommendation: *"Payment Follow-up Required (Likely Fee Default)"*.
