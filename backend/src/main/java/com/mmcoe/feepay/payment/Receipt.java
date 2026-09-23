package com.mmcoe.feepay.payment;

import com.mmcoe.feepay.student.Student;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Team 2: FR12 Verify Payment & Generate Receipt
 * Table: Receipt (receipt_id PK, transaction_id FK UK, student_id FK, receipt_number UK, generated_date, receipt_url)
 * Concepts:
 *  - DBMS: 1-to-1 relationship with Transaction; unique receipt sequence constraint
 *  - DSA: Indexed lookups by receipt_number (O(1) hash map lookup)
 */
@Entity
@Table(name = "receipt", uniqueConstraints = {
        @UniqueConstraint(name = "uk_receipt_number", columnNames = {"receipt_number"}),
        @UniqueConstraint(name = "uk_receipt_transaction", columnNames = {"transaction_id"})
})
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_id")
    private Long receiptId;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false, unique = true)
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "student_id", nullable = true)
    private Student student;

    @Column(name = "receipt_number", nullable = false, unique = true, length = 50)
    private String receiptNumber;

    @Column(name = "generated_date", nullable = false)
    private LocalDateTime generatedDate = LocalDateTime.now();

    @Column(name = "receipt_url")
    private String receiptUrl;

    public Receipt() {
    }

    public Receipt(Transaction transaction, String receiptNumber) {
        this.transaction = transaction;
        this.receiptNumber = receiptNumber;
        this.generatedDate = LocalDateTime.now();
        if (transaction != null && transaction.getPayment() != null) {
            this.student = transaction.getPayment().getStudent();
        }
    }

    public Receipt(Transaction transaction, Student student, String receiptNumber) {
        this.transaction = transaction;
        this.student = student;
        this.receiptNumber = receiptNumber;
        this.generatedDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(Long receiptId) {
        this.receiptId = receiptId;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public LocalDateTime getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(LocalDateTime generatedDate) {
        this.generatedDate = generatedDate;
    }

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }
}
