package com.mmcoe.feepay.payment;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Team 2: Technical Gateway Audit Trail
 * Table: PaymentGatewayLog (log_id PK, transaction_id FK, request_payload, response_payload, timestamp)
 * Concepts:
 *  - CN: Capture and storage of HTTP wire payloads (request & webhook responses)
 *  - SE: Non-repudiation and external provider audit logging
 */
@Entity
@Table(name = "paymentgatewaylog")
public class PaymentGatewayLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @Column(name = "request_payload", nullable = false, columnDefinition = "TEXT")
    private String requestPayload;

    @Column(name = "response_payload", nullable = false, columnDefinition = "TEXT")
    private String responsePayload;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public PaymentGatewayLog() {
    }

    public PaymentGatewayLog(Transaction transaction, String requestPayload, String responsePayload) {
        this.transaction = transaction;
        this.requestPayload = requestPayload;
        this.responsePayload = responsePayload;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public String getRequestPayload() {
        return requestPayload;
    }

    public void setRequestPayload(String requestPayload) {
        this.requestPayload = requestPayload;
    }

    public String getResponsePayload() {
        return responsePayload;
    }

    public void setResponsePayload(String responsePayload) {
        this.responsePayload = responsePayload;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
