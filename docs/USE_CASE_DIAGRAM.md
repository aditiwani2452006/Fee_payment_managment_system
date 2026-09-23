# System Use Case Specifications & Diagram

```mermaid
graph LR
    subgraph Actors
        Student["fa:fa-user Student"]
        Officer["fa:fa-briefcase Accounts Officer"]
        Admin["fa:fa-cogs Admin"]
    end

    subgraph "Fee Payment Management Platform"
        %% Team 1 Use Cases
        UC1["FR1: Provision Student Account"]
        UC2["FR2: Login & Authenticate (JWT)"]
        UC4["FR4: Manage Student Profile"]
        UC5["FR5: Configure Fee Structure"]
        UC6["FR6: View Fee Structure & Dues"]
        UC7["FR7: Apply for Installment Plan"]

        %% Team 2 Use Cases
        UC8["FR8: Review & Approve Installments"]
        UC9["FR9: View Installment Status"]
        UC10["FR10: Initiate Fee Payment"]
        UC11["FR11: Process Payment (Razorpay Test)"]
        UC12["FR12: Verify Payment & Issue Receipt"]
        UC13["FR13: Track Payment History"]
        UC_STUB1["[Stub] Payment Rollback Flow"]

        %% Team 3 Use Cases
        UC14["FR14: Generate Financial Reports"]
        UC15["FR15: Admin Dashboard & Audit Logs"]
        UC_STUB2["[Stub] Backup & Restore Database"]
        UC_AI["[Coursework Add-on] AI Default Risk Assessment"]
    end

    %% Student Relationships
    Student --> UC2
    Student --> UC4
    Student --> UC6
    Student --> UC7
    Student --> UC9
    Student --> UC10
    Student --> UC11
    Student --> UC13

    %% Accounts Officer Relationships
    Officer --> UC2
    Officer --> UC8
    Officer --> UC12
    Officer --> UC13
    Officer --> UC14
    Officer -.-> UC_STUB1

    %% Admin Relationships
    Admin --> UC1
    Admin --> UC2
    Admin --> UC5
    Admin --> UC14
    Admin --> UC15
    Admin -.-> UC_STUB2
    Admin -.-> UC_AI

    %% Include / Extend relationships
    UC10 -.-> |include| UC11
    UC11 -.-> |include| UC12
```

## Detailed Use Case Summaries

### Use Case 1: UC-PAY (FR10 - FR12 Payment Lifecycle)
- **Primary Actor:** Student
- **Supporting Actors:** Accounts Officer, Payment Gateway (Razorpay Simulator)
- **Preconditions:** Student is logged in; has an outstanding balance on `FeeAssignment` or a pending `FeeInstallment`.
- **Main Flow:**
  1. Student selects pending fee item and clicks "Pay Now".
  2. System acquires a **Pessimistic Write Lock (`SELECT ... FOR UPDATE`)** on `FeeAssignment`.
  3. System initializes a `FeePayment` record (`INITIATED`) and creates gateway order.
  4. Student confirms payment via Razorpay Test checkout interface.
  5. Gateway callback returns signature & payment ID (`gateway_reference`).
  6. System verifies cryptographic signature.
  7. System updates `FeePayment` to `SUCCESS`, reduces `outstanding_amount`, records `Transaction`, and generates sequential `Receipt`.
  8. Receipt is rendered for student view and download.
- **Postconditions:** `outstanding_amount` updated atomically; student ledger refreshed; immutable audit log recorded.
