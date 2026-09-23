package com.mmcoe.feepay.student;

import com.mmcoe.feepay.admin.AuditLog;
import com.mmcoe.feepay.admin.AuditLogRepository;
import com.mmcoe.feepay.common.ResourceNotFoundException;
import com.mmcoe.feepay.feestructure.FeeStructure;
import com.mmcoe.feepay.feestructure.FeeStructureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service implementing Section 7 & Section 36 of Project Specification:
 * Automatic Category-Based Fee Assignment.
 *
 * Business Rule:
 * Student -> Student Category -> Applicable Fee Structure -> Fee Assignment -> Total / Paid / Outstanding
 *
 * Concept:
 * - OOP: Business logic encapsulation in dedicated service layer
 * - DBMS: Relational integrity joining Student and FeeStructure
 */
@Service
public class FeeAssignmentService {

    private final FeeStructureRepository feeStructureRepository;
    private final FeeAssignmentRepository feeAssignmentRepository;
    private final AuditLogRepository auditLogRepository;

    public FeeAssignmentService(
            FeeStructureRepository feeStructureRepository,
            FeeAssignmentRepository feeAssignmentRepository,
            AuditLogRepository auditLogRepository
    ) {
        this.feeStructureRepository = feeStructureRepository;
        this.feeAssignmentRepository = feeAssignmentRepository;
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Resolves the applicable FeeStructure based on the student's admission category, department, and semester.
     */
    public FeeStructure getApplicableFeeStructure(Student student) {
        String category = student.getCategory();
        if (category == null || category.trim().isEmpty()) {
            category = "OPEN";
        }

        // 1. Try matching exact category + department + semester
        Optional<FeeStructure> matched = feeStructureRepository.findFirstByCategoryAndDepartmentAndSemesterAndStatus(
                category, student.getDepartment(), student.getSemester(), "ACTIVE"
        );
        if (matched.isPresent()) {
            return matched.get();
        }

        // 2. Try matching category + ACTIVE status
        matched = feeStructureRepository.findFirstByCategoryAndStatus(category, "ACTIVE");
        if (matched.isPresent()) {
            return matched.get();
        }

        // 3. Try matching category directly
        matched = feeStructureRepository.findFirstByCategory(category);
        if (matched.isPresent()) {
            return matched.get();
        }

        // 4. Default fallback: OPEN category
        return feeStructureRepository.findFirstByCategory("OPEN")
                .orElseThrow(() -> new ResourceNotFoundException("No applicable fee structure configured for category: " + student.getCategory()));
    }

    /**
     * Assigns the applicable fee structure to the student.
     * Prevents duplicate assignments if one already exists.
     */
    @Transactional
    public FeeAssignment assignFeeToStudent(Student student) {
        List<FeeAssignment> existing = feeAssignmentRepository.findByStudent_StudentId(student.getStudentId());
        if (!existing.isEmpty()) {
            return existing.get(0);
        }

        FeeStructure applicableStructure = getApplicableFeeStructure(student);
        BigDecimal total = applicableStructure.getAmount();

        FeeAssignment assignment = new FeeAssignment(student, applicableStructure, total);
        assignment.setStatus("PENDING");
        assignment = feeAssignmentRepository.save(assignment);

        try {
            auditLogRepository.save(new AuditLog(null, "FEE_ASSIGNMENT_CREATED",
                    "Automated fee assignment for PRN: " + student.getPrn() + " (" + student.getCategory() + ") - Total: ₹" + total, "127.0.0.1"));
        } catch (Exception ignored) {
        }

        return assignment;
    }

    /**
     * Retrieves or auto-assigns the student's active fee assignment.
     */
    @Transactional
    public FeeAssignment getOrCreateStudentAssignment(Student student) {
        List<FeeAssignment> existing = feeAssignmentRepository.findByStudent_StudentId(student.getStudentId());
        if (!existing.isEmpty()) {
            return existing.get(0);
        }
        return assignFeeToStudent(student);
    }
}
