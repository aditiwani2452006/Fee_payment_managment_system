package com.mmcoe.feepay.feestructure;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Team 1: FR5 Manage Fee Structure & FR6 View Fee Structure
 * Table: FeeStructure (fee_id PK, department, semester, category, fee_type, tuition_fee, development_fee, other_fees, caution_money, amount [total], due_date, academic_year, description, status, created_at, updated_at)
 * Concepts: DBMS (Catalogue schema) & OOP (Encapsulation and temporal lifecycle)
 */
@Entity
@Table(name = "feestructure")
public class FeeStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fee_id")
    private Long feeId;

    @Column(name = "department", nullable = false, length = 50)
    private String department;

    @Column(name = "semester", nullable = false)
    private Integer semester;

    @Column(name = "category", nullable = false, length = 50)
    private String category = "OPEN"; // OPEN, OBC/EBC/EWS/SEBC Male, SC/ST, VJNT/SBC/TFWS, J & K Quota, OBC/EBC/EWS/SEBC Female

    @Column(name = "fee_type", nullable = false, length = 50)
    private String feeType;

    @Column(name = "tuition_fee", precision = 10, scale = 2)
    private BigDecimal tuitionFee = BigDecimal.ZERO;

    @Column(name = "development_fee", precision = 10, scale = 2)
    private BigDecimal developmentFee = BigDecimal.ZERO;

    @Column(name = "other_fees", precision = 10, scale = 2)
    private BigDecimal otherFees = BigDecimal.ZERO;

    @Column(name = "caution_money", precision = 10, scale = 2)
    private BigDecimal cautionMoney = BigDecimal.ZERO;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public FeeStructure() {
    }

    public FeeStructure(String department, Integer semester, String category, String feeType,
                        BigDecimal tuitionFee, BigDecimal developmentFee, BigDecimal otherFees, BigDecimal cautionMoney,
                        LocalDate dueDate, String academicYear, String description) {
        this.department = department;
        this.semester = semester;
        this.category = category;
        this.feeType = feeType;
        this.tuitionFee = tuitionFee != null ? tuitionFee : BigDecimal.ZERO;
        this.developmentFee = developmentFee != null ? developmentFee : BigDecimal.ZERO;
        this.otherFees = otherFees != null ? otherFees : BigDecimal.ZERO;
        this.cautionMoney = cautionMoney != null ? cautionMoney : BigDecimal.ZERO;
        this.amount = this.tuitionFee.add(this.developmentFee).add(this.otherFees).add(this.cautionMoney);
        this.dueDate = dueDate;
        this.academicYear = academicYear;
        this.description = description;
        this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor for backwards compatibility
    public FeeStructure(String department, Integer semester, String feeType, BigDecimal amount, LocalDate dueDate, String academicYear, String description) {
        this.department = department;
        this.semester = semester;
        this.category = "OPEN";
        this.feeType = feeType;
        this.amount = amount;
        this.tuitionFee = amount;
        this.developmentFee = BigDecimal.ZERO;
        this.otherFees = BigDecimal.ZERO;
        this.cautionMoney = BigDecimal.ZERO;
        this.dueDate = dueDate;
        this.academicYear = academicYear;
        this.description = description;
        this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
