package com.mmcoe.feepay.installment;

import com.mmcoe.feepay.student.FeeAssignment;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Team 2: FR8 Review Installments & FR9 View Installment Status
 * Table: FeeInstallment (installment_id PK, assignment_id FK, installment_no, due_date, installment_amount, status)
 * Concepts: DBMS (Relational decomposition of fee obligations) & OOP (Encapsulation of schedule terms)
 */
@Entity
@Table(name = "feeinstallment")
public class FeeInstallment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "installment_id")
    private Long installmentId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "assignment_id", nullable = false)
    private FeeAssignment assignment;

    @Column(name = "installment_no", nullable = false)
    private Integer installmentNo;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "installment_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal installmentAmount;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING"; // PENDING, PAID, OVERDUE

    public FeeInstallment() {
    }

    public FeeInstallment(FeeAssignment assignment, Integer installmentNo, LocalDate dueDate, BigDecimal installmentAmount) {
        this.assignment = assignment;
        this.installmentNo = installmentNo;
        this.dueDate = dueDate;
        this.installmentAmount = installmentAmount;
        this.status = "PENDING";
    }

    public void markPaid() {
        this.status = "PAID";
    }

    // Getters and Setters
    public Long getInstallmentId() {
        return installmentId;
    }

    public void setInstallmentId(Long installmentId) {
        this.installmentId = installmentId;
    }

    public FeeAssignment getAssignment() {
        return assignment;
    }

    public void setAssignment(FeeAssignment assignment) {
        this.assignment = assignment;
    }

    public Integer getInstallmentNo() {
        return installmentNo;
    }

    public void setInstallmentNo(Integer installmentNo) {
        this.installmentNo = installmentNo;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getInstallmentAmount() {
        return installmentAmount;
    }

    public void setInstallmentAmount(BigDecimal installmentAmount) {
        this.installmentAmount = installmentAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
