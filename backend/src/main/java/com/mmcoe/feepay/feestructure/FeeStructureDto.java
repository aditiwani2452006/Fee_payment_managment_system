package com.mmcoe.feepay.feestructure;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FeeStructureDto {

    private Long feeId;

    @NotBlank(message = "Department is required")
    private String department;

    @NotNull(message = "Semester is required")
    private Integer semester;

    private String category = "OPEN";

    @NotBlank(message = "Fee Type is required")
    private String feeType;

    private BigDecimal tuitionFee = BigDecimal.ZERO;
    private BigDecimal developmentFee = BigDecimal.ZERO;
    private BigDecimal otherFees = BigDecimal.ZERO;
    private BigDecimal cautionMoney = BigDecimal.ZERO;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Due Date is required")
    private LocalDate dueDate;

    @NotBlank(message = "Academic Year is required")
    private String academicYear;

    private String description;
    private String status;

    public FeeStructureDto() {
    }

    public FeeStructureDto(FeeStructure entity) {
        this.feeId = entity.getFeeId();
        this.department = entity.getDepartment();
        this.semester = entity.getSemester();
        this.category = entity.getCategory();
        this.feeType = entity.getFeeType();
        this.tuitionFee = entity.getTuitionFee();
        this.developmentFee = entity.getDevelopmentFee();
        this.otherFees = entity.getOtherFees();
        this.cautionMoney = entity.getCautionMoney();
        this.amount = entity.getAmount();
        this.dueDate = entity.getDueDate();
        this.academicYear = entity.getAcademicYear();
        this.description = entity.getDescription();
        this.status = entity.getStatus();
    }

    // Getters and Setters
    public Long getFeeId() {
        return feeId;
    }

    public void setFeeId(Long feeId) {
        this.feeId = feeId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public BigDecimal getTuitionFee() {
        return tuitionFee;
    }

    public void setTuitionFee(BigDecimal tuitionFee) {
        this.tuitionFee = tuitionFee;
    }

    public BigDecimal getDevelopmentFee() {
        return developmentFee;
    }

    public void setDevelopmentFee(BigDecimal developmentFee) {
        this.developmentFee = developmentFee;
    }

    public BigDecimal getOtherFees() {
        return otherFees;
    }

    public void setOtherFees(BigDecimal otherFees) {
        this.otherFees = otherFees;
    }

    public BigDecimal getCautionMoney() {
        return cautionMoney;
    }

    public void setCautionMoney(BigDecimal cautionMoney) {
        this.cautionMoney = cautionMoney;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
