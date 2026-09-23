package com.mmcoe.feepay.installment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReviewInstallmentRequest {

    @NotNull(message = "Assignment ID is required")
    private Long assignmentId;

    @NotBlank(message = "Action must be APPROVE or REJECT")
    private String action; // APPROVE or REJECT

    private Integer numberOfInstallments = 2; // Default 2 if approved

    public ReviewInstallmentRequest() {
    }

    public ReviewInstallmentRequest(Long assignmentId, String action, Integer numberOfInstallments) {
        this.assignmentId = assignmentId;
        this.action = action;
        this.numberOfInstallments = numberOfInstallments;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getNumberOfInstallments() {
        return numberOfInstallments;
    }

    public void setNumberOfInstallments(Integer numberOfInstallments) {
        this.numberOfInstallments = numberOfInstallments;
    }
}
