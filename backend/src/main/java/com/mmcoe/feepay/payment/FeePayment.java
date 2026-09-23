package com.mmcoe.feepay.payment;

import com.mmcoe.feepay.installment.FeeInstallment;
import com.mmcoe.feepay.student.Student;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Team 2: FR10 Initiate Payment, FR11 Process Payment, FR13 Track Payment History
 * Table: FeePayment (payment_id PK, installment_id FK, student_id FK, amount_paid, payment_date, payment_method, status)
 * 
 * NOTE (SRS Section 6 & 10): Intentional Double Foreign Key Design Pattern!
 * Both installment_id AND student_id are stored directly on FeePayment.
 * This guarantees immediate student traceability on disputed payments without joining
 * through FeeInstallment and FeeAssignment.
 * 
 * Concepts:
 *  - DBMS: Double foreign key constraint & 3NF preservation
 *  - OOP: Encapsulated payment state and validation
 */
@Entity
@Table(name = "feepayment")
public class FeePayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "installment_id", nullable = true)
    private FeeInstallment installment;

    // Intentional direct FK to Student for non-repudiation and direct audit trail
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "amount_paid", nullable = false, precision = 10, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate = LocalDateTime.now();

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod; // RAZORPAY_TEST, UPI, NET_BANKING, DEBIT_CARD

    @Column(name = "status", nullable = false, length = 30)
    private String status; // INITIATED, SUCCESS, FAILED, ROLLED_BACK

    public FeePayment() {
    }

    public FeePayment(FeeInstallment installment, Student student, BigDecimal amountPaid, String paymentMethod, String status) {
        this.installment = installment;
        this.student = student;
        this.amountPaid = amountPaid;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.paymentDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public FeeInstallment getInstallment() {
        return installment;
    }

    public void setInstallment(FeeInstallment installment) {
        this.installment = installment;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
