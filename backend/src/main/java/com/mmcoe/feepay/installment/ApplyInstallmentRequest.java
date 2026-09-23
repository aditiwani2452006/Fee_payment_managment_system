package com.mmcoe.feepay.installment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ApplyInstallmentRequest {

    @NotNull(message = "Fee assignment ID is required")
    private Long assignmentId;

    @NotNull(message = "Number of installments is required (2 or 3)")
    @Min(value = 2, message = "Minimum 2 installments")
    @Max(value = 3, message = "Maximum 3 installments")
    private Integer numberOfInstallments;

    private String reason;

    public ApplyInstallmentRequest() {
    }

    public ApplyInstallmentRequest(Long assignmentId, Integer numberOfInstallments, String reason) {
        this.assignmentId = assignmentId;
        this.numberOfInstallments = numberOfInstallments;
        this.reason = reason;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Integer getNumberOfInstallments() {
        return numberOfInstallments;
    }

    public void setNumberOfInstallments(Integer numberOfInstallments) {
        this.numberOfInstallments = numberOfInstallments;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
