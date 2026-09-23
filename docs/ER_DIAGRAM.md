# Entity-Relationship (ER) Diagram
## SRS Section 10 Normalized 14-Table Architecture

```mermaid
erDiagram
    ROLE ||--o{ USER_ACCOUNT : "assigns permissions"
    STUDENT ||--o| USER_ACCOUNT : "authenticates"
    STUDENT ||--o{ FEE_ASSIGNMENT : "allocated"
    FEE_STRUCTURE ||--o{ FEE_ASSIGNMENT : "defined by"
    FEE_ASSIGNMENT ||--o{ FEE_INSTALLMENT : "divided into"
    FEE_INSTALLMENT ||--o{ FEE_PAYMENT : "settled by"
    STUDENT ||--o{ FEE_PAYMENT : "direct traceability"
    FEE_PAYMENT ||--o{ TRANSACTION : "processes"
    TRANSACTION ||--|| RECEIPT : "generates"
    TRANSACTION ||--o{ PAYMENT_GATEWAY_LOG : "logs payloads"
    USER_ACCOUNT ||--o{ REPORT : "generates"
    USER_ACCOUNT ||--o{ AUDIT_LOG : "performs action"
    USER_ACCOUNT ||--o{ SECURITY_LOG : "emits telemetry"
    USER_ACCOUNT ||--o{ BACKUP_HISTORY : "triggers backup"

    ROLE {
        bigint role_id PK
        varchar role_name UK
    }

    STUDENT {
        bigint student_id PK
        varchar name
        varchar course
        int semester
        varchar email UK
        varchar contact_number
    }

    USER_ACCOUNT {
        bigint user_id PK
        varchar username UK
        varchar password_hash
        bigint role_id FK
        bigint student_id FK
    }

    FEE_STRUCTURE {
        bigint fee_id PK
        varchar department
        int semester
        varchar fee_type
        numeric amount
        date due_date
        varchar academic_year
        varchar status
        timestamp created_at
        timestamp updated_at
    }

    FEE_ASSIGNMENT {
        bigint assignment_id PK
        bigint student_id FK
        bigint fee_structure_id FK
        numeric total_amount
        numeric paid_amount
        numeric outstanding_amount
        varchar status
    }

    FEE_INSTALLMENT {
        bigint installment_id PK
        bigint assignment_id FK
        int installment_no
        date due_date
        numeric installment_amount
        varchar status
    }

    FEE_PAYMENT {
        bigint payment_id PK
        bigint installment_id FK
        bigint student_id FK
        numeric amount_paid
        timestamp payment_date
        varchar payment_method
        varchar status
    }

    TRANSACTION {
        bigint transaction_id PK
        bigint payment_id FK
        timestamp transaction_date
        varchar status
        varchar gateway_reference UK
    }

    RECEIPT {
        bigint receipt_id PK
        bigint transaction_id FK
        varchar receipt_number UK
        timestamp generated_date
    }

    PAYMENT_GATEWAY_LOG {
        bigint log_id PK
        bigint transaction_id FK
        text request_payload
        text response_payload
        timestamp timestamp
    }

    REPORT {
        bigint report_id PK
        bigint generated_by FK
        varchar report_type
        varchar date_range
        timestamp generated_on
    }

    AUDIT_LOG {
        bigint audit_id PK
        bigint user_id FK
        varchar action
        timestamp timestamp
    }

    SECURITY_LOG {
        bigint log_id PK
        bigint user_id FK
        varchar event_type
        varchar ip_address
        timestamp timestamp
    }

    BACKUP_HISTORY {
        bigint backup_id PK
        bigint performed_by FK
        timestamp backup_date
        varchar file_path
        varchar status
    }
```

### Architectural Notes for Database Viva
1. **Normalization:** The schema satisfies Third Normal Form (3NF). Every non-key attribute depends strictly on the primary key, the whole key, and nothing but the key.
2. **Double Foreign Key Design Pattern:** `FeePayment` deliberately preserves a foreign key to `Student` alongside `installment_id`. If an installment record is modified or payment happens on a full fee without installments, the payment record is permanently bound to the student, satisfying the non-repudiation requirement in financial systems without join performance penalties.
3. **Idempotency Key:** `Transaction.gateway_reference` is designated `UNIQUE`, preventing duplicate double-click transactions at the database engine level.
