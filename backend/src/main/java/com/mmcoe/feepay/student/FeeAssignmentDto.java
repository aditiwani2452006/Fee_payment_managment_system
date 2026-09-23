package com.mmcoe.feepay.student;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FeeAssignmentDto {
    private Long assignmentId;
    private Long studentId;
    private String studentName;
    private String prn;
    private String category;
    private Long feeStructureId;
    private String feeType;
    private String department;
    private Integer semester;
    private BigDecimal tuitionFee;
    private BigDecimal developmentFee;
    private BigDecimal otherFees;
    private BigDecimal cautionMoney;
    private LocalDate dueDate;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal outstandingAmount;
    private String status;

    public FeeAssignmentDto() {
    }

    public FeeAssignmentDto(FeeAssignment fa) {
        this.assignmentId = fa.getAssignmentId();
        this.studentId = fa.getStudent().getStudentId();
        this.studentName = fa.getStudent().getName();
        this.prn = fa.getStudent().getPrn();
        this.category = fa.getStudent().getCategory();
        this.feeStructureId = fa.getFeeStructure().getFeeId();
        this.feeType = fa.getFeeStructure().getFeeType();
        this.department = fa.getFeeStructure().getDepartment();
        this.semester = fa.getFeeStructure().getSemester();
        this.tuitionFee = fa.getFeeStructure().getTuitionFee();
        this.developmentFee = fa.getFeeStructure().getDevelopmentFee();
        this.otherFees = fa.getFeeStructure().getOtherFees();
        this.cautionMoney = fa.getFeeStructure().getCautionMoney();
        this.dueDate = fa.getFeeStructure().getDueDate();
        this.totalAmount = fa.getTotalAmount();
        this.paidAmount = fa.getPaidAmount();
        this.outstandingAmount = fa.getOutstandingAmount();
        this.status = fa.getStatus();
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
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

    public String getPrn() {
        return prn;
    }

    public void setPrn(String prn) {
        this.prn = prn;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getFeeStructureId() {
        return feeStructureId;
    }

    public void setFeeStructureId(Long feeStructureId) {
        this.feeStructureId = feeStructureId;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
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

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(BigDecimal outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
