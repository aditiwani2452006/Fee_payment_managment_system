package com.mmcoe.feepay.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponseDto {
    private Long paymentId;
    private Long studentId;
    private String studentName;
    private Long installmentId;
    private BigDecimal amountPaid;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private String status;
    private Long transactionId;
    private String gatewayReference;
    private String receiptNumber;
    private String message;

    public PaymentResponseDto() {
    }

    public PaymentResponseDto(FeePayment p, Transaction tx, Receipt r) {
        this.paymentId = p.getPaymentId();
        this.studentId = p.getStudent().getStudentId();
        this.studentName = p.getStudent().getName();
        this.installmentId = (p.getInstallment() != null) ? p.getInstallment().getInstallmentId() : null;
        this.amountPaid = p.getAmountPaid();
        this.paymentDate = p.getPaymentDate();
        this.paymentMethod = p.getPaymentMethod();
        this.status = p.getStatus();
        if (tx != null) {
            this.transactionId = tx.getTransactionId();
            this.gatewayReference = tx.getGatewayReference();
        }
        if (r != null) {
            this.receiptNumber = r.getReceiptNumber();
        }
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Long getInstallmentId() {
        return installmentId;
    }

    public void setInstallmentId(Long installmentId) {
        this.installmentId = installmentId;
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

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getGatewayReference() {
        return gatewayReference;
    }

    public void setGatewayReference(String gatewayReference) {
        this.gatewayReference = gatewayReference;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
