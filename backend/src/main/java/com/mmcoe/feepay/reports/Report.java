package com.mmcoe.feepay.reports;

import com.mmcoe.feepay.auth.UserAccount;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Team 3: FR14 Generate Reports
 * Table: Report (report_id PK, generated_by FK -> User, report_type, date_range, generated_on)
 * Concepts: DBMS (Report auditing & query aggregation) & OOP (Polymorphic reporting abstractions)
 */
@Entity
@Table(name = "report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "generated_by", nullable = false)
    private UserAccount generatedBy;

    @Column(name = "report_type", nullable = false, length = 50)
    private String reportType; // FEE_COLLECTION, PENDING_DUES, DEPARTMENT_SUMMARY

    @Column(name = "date_range", nullable = false, length = 50)
    private String dateRange;

    @Column(name = "generated_on", nullable = false)
    private LocalDateTime generatedOn = LocalDateTime.now();

    public Report() {
    }

    public Report(UserAccount generatedBy, String reportType, String dateRange) {
        this.generatedBy = generatedBy;
        this.reportType = reportType;
        this.dateRange = dateRange;
        this.generatedOn = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public UserAccount getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(UserAccount generatedBy) {
        this.generatedBy = generatedBy;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getDateRange() {
        return dateRange;
    }

    public void setDateRange(String dateRange) {
        this.dateRange = dateRange;
    }

    public LocalDateTime getGeneratedOn() {
        return generatedOn;
    }

    public void setGeneratedOn(LocalDateTime generatedOn) {
        this.generatedOn = generatedOn;
    }
}
