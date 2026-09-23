package com.mmcoe.feepay.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProcessPaymentRequest {

    @NotNull(message = "Payment ID is required")
    private Long paymentId;

    @NotBlank(message = "Gateway reference / Order ID is required")
    private String gatewayReference;

    // Simulated Razorpay payment signature
    private String gatewaySignature;

    public ProcessPaymentRequest() {
    }

    public ProcessPaymentRequest(Long paymentId, String gatewayReference, String gatewaySignature) {
        this.paymentId = paymentId;
        this.gatewayReference = gatewayReference;
        this.gatewaySignature = gatewaySignature;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getGatewayReference() {
        return gatewayReference;
    }

    public void setGatewayReference(String gatewayReference) {
        this.gatewayReference = gatewayReference;
    }

    public String getGatewaySignature() {
        return gatewaySignature;
    }

    public void setGatewaySignature(String gatewaySignature) {
        this.gatewaySignature = gatewaySignature;
    }
}
