package com.mmcoe.feepay.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class InitiatePaymentRequest {

    @NotNull(message = "Assignment ID is required")
    private Long assignmentId;

    private Long installmentId; // Optional: set if paying a specific installment

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "1.0", message = "Minimum payment is 1.00")
    private BigDecimal amount;

    private String paymentMethod = "RAZORPAY_TEST";

    public InitiatePaymentRequest() {
    }

    public InitiatePaymentRequest(Long assignmentId, Long installmentId, BigDecimal amount, String paymentMethod) {
        this.assignmentId = assignmentId;
        this.installmentId = installmentId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Long getInstallmentId() {
        return installmentId;
    }

    public void setInstallmentId(Long installmentId) {
        this.installmentId = installmentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
