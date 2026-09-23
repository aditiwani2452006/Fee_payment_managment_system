package com.mmcoe.feepay.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReceiptDto {
    private Long receiptId;
    private String receiptNumber;
    private LocalDateTime generatedDate;
    private Long transactionId;
    private String gatewayReference;
    private Long paymentId;
    private BigDecimal amountPaid;
    private String paymentMethod;
    private String studentName;
    private Long studentId;
    private String course;
    private String feeType;
    private String academicYear;

    public ReceiptDto() {
    }

    public ReceiptDto(Receipt receipt) {
        this.receiptId = receipt.getReceiptId();
        this.receiptNumber = receipt.getReceiptNumber();
        this.generatedDate = receipt.getGeneratedDate();

        Transaction tx = receipt.getTransaction();
        this.transactionId = tx.getTransactionId();
        this.gatewayReference = tx.getGatewayReference();

        FeePayment payment = tx.getPayment();
        this.paymentId = payment.getPaymentId();
        this.amountPaid = payment.getAmountPaid();
        this.paymentMethod = payment.getPaymentMethod();

        if (payment.getStudent() != null) {
            this.studentId = payment.getStudent().getStudentId();
            this.studentName = payment.getStudent().getName();
            this.course = payment.getStudent().getCourse();
        }

        if (payment.getInstallment() != null && payment.getInstallment().getAssignment() != null) {
            this.feeType = payment.getInstallment().getAssignment().getFeeStructure().getFeeType();
            this.academicYear = payment.getInstallment().getAssignment().getFeeStructure().getAcademicYear();
        } else {
            this.feeType = "Tuition / College Fee";
            this.academicYear = "2026-2027";
        }
    }

    // Getters and Setters
    public Long getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(Long receiptId) {
        this.receiptId = receiptId;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public LocalDateTime getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(LocalDateTime generatedDate) {
        this.generatedDate = generatedDate;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getGatewayReference() {
        return gatewayReference;
    }

    public void setGatewayReference(String gatewayReference) {
        this.gatewayReference = gatewayReference;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }
}
