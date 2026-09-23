package com.mmcoe.feepay.reports;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class FinancialSummaryDto {
    private BigDecimal totalAssignedAmount;
    private BigDecimal totalCollectedAmount;
    private BigDecimal totalOutstandingAmount;
    private Double collectionPercentage;
    private long totalStudents;
    private long paidStudentsCount;
    private long pendingStudentsCount;
    private Map<String, BigDecimal> departmentCollections;
    private Map<String, BigDecimal> departmentOutstanding;

    public FinancialSummaryDto() {
    }

    public BigDecimal getTotalAssignedAmount() {
        return totalAssignedAmount;
    }

    public void setTotalAssignedAmount(BigDecimal totalAssignedAmount) {
        this.totalAssignedAmount = totalAssignedAmount;
    }

    public BigDecimal getTotalCollectedAmount() {
        return totalCollectedAmount;
    }

    public void setTotalCollectedAmount(BigDecimal totalCollectedAmount) {
        this.totalCollectedAmount = totalCollectedAmount;
    }

    public BigDecimal getTotalOutstandingAmount() {
        return totalOutstandingAmount;
    }

    public void setTotalOutstandingAmount(BigDecimal totalOutstandingAmount) {
        this.totalOutstandingAmount = totalOutstandingAmount;
    }

    public Double getCollectionPercentage() {
        return collectionPercentage;
    }

    public void setCollectionPercentage(Double collectionPercentage) {
        this.collectionPercentage = collectionPercentage;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getPaidStudentsCount() {
        return paidStudentsCount;
    }

    public void setPaidStudentsCount(long paidStudentsCount) {
        this.paidStudentsCount = paidStudentsCount;
    }

    public long getPendingStudentsCount() {
        return pendingStudentsCount;
    }

    public void setPendingStudentsCount(long pendingStudentsCount) {
        this.pendingStudentsCount = pendingStudentsCount;
    }

    public Map<String, BigDecimal> getDepartmentCollections() {
        return departmentCollections;
    }

    public void setDepartmentCollections(Map<String, BigDecimal> departmentCollections) {
        this.departmentCollections = departmentCollections;
    }

    public Map<String, BigDecimal> getDepartmentOutstanding() {
        return departmentOutstanding;
    }

    public void setDepartmentOutstanding(Map<String, BigDecimal> departmentOutstanding) {
        this.departmentOutstanding = departmentOutstanding;
    }
}
