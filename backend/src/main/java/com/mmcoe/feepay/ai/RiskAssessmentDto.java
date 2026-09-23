package com.mmcoe.feepay.ai;

import java.math.BigDecimal;

/**
 * Coursework Demo for AI Subject (Section 16 of SRS Specification):
 * Basic Rule-based Predictive Indicator for Fee Default.
 * 
 * Rules:
 * IF outstanding fee > 0 AND previous payment history shows delayed/missed payment
 * THEN display: "Payment Follow-up Required (Likely Fee Default)"
 */
public class RiskAssessmentDto {
    private Long studentId;
    private String prn;
    private String studentName;
    private BigDecimal outstandingDues;
    private long daysOverdue;
    private int missedInstallments;
    private String riskLevel; // LOW, MODERATE, HIGH
    private double riskScore; // 0.0 to 1.0 (Rule indicator score)
    private String indicatorMessage; // "Payment Follow-up Required" or "Timely / Up to Date"
    private String primaryRiskFactor;
    private String modelType = "Basic Rule-Based Predictive Indicator (Coursework Demo)";

    public RiskAssessmentDto() {
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getPrn() {
        return prn;
    }

    public void setPrn(String prn) {
        this.prn = prn;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public BigDecimal getOutstandingDues() {
        return outstandingDues;
    }

    public void setOutstandingDues(BigDecimal outstandingDues) {
        this.outstandingDues = outstandingDues;
    }

    public long getDaysOverdue() {
        return daysOverdue;
    }

    public void setDaysOverdue(long daysOverdue) {
        this.daysOverdue = daysOverdue;
    }

    public int getMissedInstallments() {
        return missedInstallments;
    }

    public void setMissedInstallments(int missedInstallments) {
        this.missedInstallments = missedInstallments;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(double riskScore) {
        this.riskScore = riskScore;
    }

    public String getIndicatorMessage() {
        return indicatorMessage;
    }

    public void setIndicatorMessage(String indicatorMessage) {
        this.indicatorMessage = indicatorMessage;
    }

    public String getPrimaryRiskFactor() {
        return primaryRiskFactor;
    }

    public void setPrimaryRiskFactor(String primaryRiskFactor) {
        this.primaryRiskFactor = primaryRiskFactor;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }
}
