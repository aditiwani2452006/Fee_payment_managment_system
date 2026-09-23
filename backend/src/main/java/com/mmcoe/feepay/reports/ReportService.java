package com.mmcoe.feepay.reports;

import com.mmcoe.feepay.auth.UserAccount;
import com.mmcoe.feepay.auth.UserRepository;
import com.mmcoe.feepay.student.FeeAssignment;
import com.mmcoe.feepay.student.FeeAssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Team 3: Reports, Security Monitoring & System Administration
 * Implements FR14: Generate Financial Reports (Collection, Pending, Department breakdown).
 * Concepts:
 *  - DBMS: Aggregation across relational datasets & non-blocking read replicas
 *  - OOP: Encapsulated financial metric calculations
 */
@Service
public class ReportService {

    private final FeeAssignmentRepository feeAssignmentRepository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public ReportService(
            FeeAssignmentRepository feeAssignmentRepository,
            ReportRepository reportRepository,
            UserRepository userRepository
    ) {
        this.feeAssignmentRepository = feeAssignmentRepository;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public FinancialSummaryDto generateSummaryReport(Long userId, String reportType) {
        List<FeeAssignment> assignments = feeAssignmentRepository.findAll();

        BigDecimal totalAssigned = BigDecimal.ZERO;
        BigDecimal totalCollected = BigDecimal.ZERO;
        BigDecimal totalOutstanding = BigDecimal.ZERO;

        Map<String, BigDecimal> deptCollections = new HashMap<>();
        Map<String, BigDecimal> deptOutstanding = new HashMap<>();

        long paidCount = 0;
        long pendingCount = 0;

        for (FeeAssignment fa : assignments) {
            totalAssigned = totalAssigned.add(fa.getTotalAmount());
            totalCollected = totalCollected.add(fa.getPaidAmount());
            totalOutstanding = totalOutstanding.add(fa.getOutstandingAmount());

            String dept = fa.getFeeStructure().getDepartment();
            deptCollections.put(dept, deptCollections.getOrDefault(dept, BigDecimal.ZERO).add(fa.getPaidAmount()));
            deptOutstanding.put(dept, deptOutstanding.getOrDefault(dept, BigDecimal.ZERO).add(fa.getOutstandingAmount()));

            if ("PAID".equalsIgnoreCase(fa.getStatus())) {
                paidCount++;
            } else {
                pendingCount++;
            }
        }

        double collectionPct = 0.0;
        if (totalAssigned.compareTo(BigDecimal.ZERO) > 0) {
            collectionPct = totalCollected.multiply(BigDecimal.valueOf(100))
                    .divide(totalAssigned, 2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        FinancialSummaryDto summary = new FinancialSummaryDto();
        summary.setTotalAssignedAmount(totalAssigned);
        summary.setTotalCollectedAmount(totalCollected);
        summary.setTotalOutstandingAmount(totalOutstanding);
        summary.setCollectionPercentage(collectionPct);
        summary.setTotalStudents(assignments.size());
        summary.setPaidStudentsCount(paidCount);
        summary.setPendingStudentsCount(pendingCount);
        summary.setDepartmentCollections(deptCollections);
        summary.setDepartmentOutstanding(deptOutstanding);

        // Audit report generation in Report table
        if (userId != null) {
            userRepository.findById(userId).ifPresent(user -> {
                Report report = new Report(user, reportType != null ? reportType : "FEE_COLLECTION", "CURRENT_ACADEMIC_YEAR");
                reportRepository.save(report);
            });
        }

        return summary;
    }

    @Transactional(readOnly = true)
    public List<Report> getReportAuditHistory() {
        return reportRepository.findAll();
    }
}
