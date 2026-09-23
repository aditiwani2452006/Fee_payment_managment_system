package com.mmcoe.feepay.student;

import com.mmcoe.feepay.common.BadRequestException;
import com.mmcoe.feepay.feestructure.FeeStructure;
import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Team 1 & Team 2: FR6 View Fee Structure & FR10 Payment Processing
 * Table: FeeAssignment (assignment_id PK, student_id FK, fee_structure_id FK, total_amount, paid_amount, outstanding_amount, status)
 * Concepts:
 *  - OOP: Encapsulation on financial balances (no public mutable balance setters).
 *  - DBMS: Strong referential integrity between Student and FeeStructure.
 */
@Entity
@Table(name = "feeassignment")
public class FeeAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Long assignmentId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fee_structure_id", nullable = false)
    private FeeStructure feeStructure;

    // Financial balances are encapsulated to prevent external arbitrary mutations
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "paid_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "outstanding_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal outstandingAmount;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "UNPAID"; // UNPAID, PARTIALLY_PAID, PAID, INSTALLMENT_REQUESTED, INSTALLMENT_APPROVED

    public FeeAssignment() {
    }

    public FeeAssignment(Student student, FeeStructure feeStructure, BigDecimal totalAmount) {
        this.student = student;
        this.feeStructure = feeStructure;
        this.totalAmount = totalAmount;
        this.paidAmount = BigDecimal.ZERO;
        this.outstandingAmount = totalAmount;
        this.status = "UNPAID";
    }

    /**
     * OOP Encapsulation: State-mutating domain method enforcing balance invariants.
     * Prevents negative balances and ensures: totalAmount == paidAmount + outstandingAmount
     */
    public synchronized void applyPayment(BigDecimal paymentAmount) {
        if (paymentAmount == null || paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Payment amount must be strictly greater than zero");
        }
        if (paymentAmount.compareTo(this.outstandingAmount) > 0) {
            throw new BadRequestException("Payment amount (" + paymentAmount + ") exceeds outstanding balance (" + this.outstandingAmount + ")");
        }

        this.paidAmount = this.paidAmount.add(paymentAmount);
        this.outstandingAmount = this.outstandingAmount.subtract(paymentAmount);

        if (this.outstandingAmount.compareTo(BigDecimal.ZERO) == 0) {
            this.status = "PAID";
        } else {
            this.status = "PARTIALLY_PAID";
        }
    }

    // Getters (Strict Encapsulation: Read-only balance getters, no arbitrary balance setters)
    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public FeeStructure getFeeStructure() {
        return feeStructure;
    }

    public void setFeeStructure(FeeStructure feeStructure) {
        this.feeStructure = feeStructure;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public BigDecimal getOutstandingAmount() {
        return outstandingAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
