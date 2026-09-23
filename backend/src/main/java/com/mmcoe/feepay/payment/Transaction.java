package com.mmcoe.feepay.payment;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Team 2: FR11 Process Payment & Idempotency Enforcement
 * Table: Transaction (transaction_id PK, payment_id FK, transaction_reference UK, gateway_name, transaction_status, transaction_date, amount)
 * Concepts:
 *  - DBMS: Unique constraint on transaction_reference for strict idempotency (double-submission prevention)
 *  - CN: Technical gateway settlement transaction
 */
@Entity
@Table(name = "transaction", uniqueConstraints = {
        @UniqueConstraint(name = "uk_tx_gateway_ref", columnNames = {"transaction_reference"})
})
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private FeePayment payment;

    @Column(name = "transaction_reference", nullable = false, unique = true, length = 100)
    private String gatewayReference;

    @Column(name = "gateway_name", length = 50)
    private String gatewayName = "RAZORPAY_TEST";

    @Column(name = "transaction_status", nullable = false, length = 30)
    private String status = "SUCCESS"; // PENDING, SUCCESS, FAILED, REFUNDED

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    public Transaction() {
    }

    public Transaction(FeePayment payment, String status, String gatewayReference) {
        this.payment = payment;
        this.status = status;
        this.gatewayReference = gatewayReference;
        this.transactionDate = LocalDateTime.now();
        this.amount = (payment != null && payment.getAmountPaid() != null) ? payment.getAmountPaid() : BigDecimal.ZERO;
        this.gatewayName = "RAZORPAY_TEST";
    }

    // Getters and Setters
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public FeePayment getPayment() {
        return payment;
    }

    public void setPayment(FeePayment payment) {
        this.payment = payment;
    }

    public String getGatewayReference() {
        return gatewayReference;
    }

    public void setGatewayReference(String gatewayReference) {
        this.gatewayReference = gatewayReference;
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
