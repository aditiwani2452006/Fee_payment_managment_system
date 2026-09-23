# Object-Oriented Domain Model & Class Diagram

```mermaid
classDiagram
    class Role {
        +Long roleId
        +String roleName
        +getRoleName() String
    }

    class UserAccount {
        +Long userId
        +String username
        +String passwordHash
        +Role role
        +Student student
        +hasRole(String role) boolean
    }

    class Student {
        +Long studentId
        +String name
        +String course
        +Integer semester
        +String email
        +String contactNumber
        +getProfileSummary() String
    }

    class FeeStructure {
        +Long feeId
        +String department
        +Integer semester
        +String feeType
        +BigDecimal amount
        +LocalDate dueDate
        +String academicYear
        +String status
        +isOverdue() boolean
    }

    class FeeAssignment {
        +Long assignmentId
        +Student student
        +FeeStructure feeStructure
        -BigDecimal totalAmount
        -BigDecimal paidAmount
        -BigDecimal outstandingAmount
        +String status
        +applyPayment(BigDecimal amount) void
        +getOutstandingAmount() BigDecimal
    }

    class FeeInstallment {
        +Long installmentId
        +FeeAssignment assignment
        +Integer installmentNo
        +LocalDate dueDate
        +BigDecimal installmentAmount
        +String status
        +markPaid() void
    }

    class FeePayment {
        +Long paymentId
        +FeeInstallment installment
        +Student student
        +BigDecimal amountPaid
        +LocalDateTime paymentDate
        +String paymentMethod
        +String status
        +verifySignature() boolean
    }

    class Transaction {
        +Long transactionId
        +FeePayment payment
        +LocalDateTime transactionDate
        +String status
        +String gatewayReference
    }

    class Receipt {
        +Long receiptId
        +Transaction transaction
        +String receiptNumber
        +LocalDateTime generatedDate
        +renderPrintableFormat() String
    }

    class AuditLog {
        +Long auditId
        +UserAccount user
        +String action
        +LocalDateTime timestamp
    }

    UserAccount --> Role : has
    UserAccount --> Student : maps to (optional)
    FeeAssignment --> Student : belongs to
    FeeAssignment --> FeeStructure : references
    FeeInstallment --> FeeAssignment : child of
    FeePayment --> FeeInstallment : satisfies (optional)
    FeePayment --> Student : direct link (traceability)
    Transaction --> FeePayment : tracks
    Receipt --> Transaction : generated for
    AuditLog --> UserAccount : executed by
```

## OOP Principles Demonstrated in Code
1. **Encapsulation:**
   - In `FeeAssignment`, financial amounts (`totalAmount`, `paidAmount`, `outstandingAmount`) are `private`. Direct setter modification is prohibited. Updates are strictly mediated via the business method `applyPayment(BigDecimal amount)`, ensuring invariants (`paidAmount + outstandingAmount == totalAmount`) are preserved.
2. **Polymorphism:**
   - Flexible reporting pipeline where report exporters implement a common `ReportGenerator` interface supporting both `JsonReportGenerator` and `CsvReportGenerator`.
3. **Single Responsibility Principle (SRP):**
   - Distinct service boundaries separating Authentication (`AuthService`), Student Profile (`StudentService`), Payment Processing (`PaymentService`), and Concurrency Control.
