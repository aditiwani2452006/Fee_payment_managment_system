package com.mmcoe.feepay.installment;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InstallmentDto {
    private Long installmentId;
    private Long assignmentId;
    private String studentName;
    private String feeType;
    private Integer installmentNo;
    private LocalDate dueDate;
    private BigDecimal installmentAmount;
    private String status;

    public InstallmentDto() {
    }

    public InstallmentDto(FeeInstallment fi) {
        this.installmentId = fi.getInstallmentId();
        this.assignmentId = fi.getAssignment().getAssignmentId();
        this.studentName = fi.getAssignment().getStudent().getName();
        this.feeType = fi.getAssignment().getFeeStructure().getFeeType();
        this.installmentNo = fi.getInstallmentNo();
        this.dueDate = fi.getDueDate();
        this.installmentAmount = fi.getInstallmentAmount();
        this.status = fi.getStatus();
    }

    public Long getInstallmentId() {
        return installmentId;
    }

    public void setInstallmentId(Long installmentId) {
        this.installmentId = installmentId;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
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
