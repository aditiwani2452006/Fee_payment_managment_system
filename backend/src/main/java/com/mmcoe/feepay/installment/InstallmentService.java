package com.mmcoe.feepay.installment;

import com.mmcoe.feepay.common.BadRequestException;
import com.mmcoe.feepay.common.ResourceNotFoundException;
import com.mmcoe.feepay.student.FeeAssignment;
import com.mmcoe.feepay.student.FeeAssignmentDto;
import com.mmcoe.feepay.student.FeeAssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Team 2: Payment Processing & Transaction Management
 * Implements:
 *  - FR7: Apply for Installment Plan
 *  - FR8: Review Installment Requests (Accounts Officer generates schedule)
 *  - FR9: View Installment Status
 * Concepts:
 *  - DBMS: Atomic generation of multiple schedule rows via @Transactional
 *  - OOP: Encapsulated calculation of installment amounts & dates
 */
@Service
public class InstallmentService {

    private final InstallmentRepository installmentRepository;
    private final FeeAssignmentRepository feeAssignmentRepository;

    public InstallmentService(
            InstallmentRepository installmentRepository,
            FeeAssignmentRepository feeAssignmentRepository
    ) {
        this.installmentRepository = installmentRepository;
        this.feeAssignmentRepository = feeAssignmentRepository;
    }

    @Transactional
    public FeeAssignmentDto applyForInstallment(Long studentId, ApplyInstallmentRequest request) {
        FeeAssignment assignment = feeAssignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("FeeAssignment not found with ID: " + request.getAssignmentId()));

        if (!assignment.getStudent().getStudentId().equals(studentId)) {
            throw new BadRequestException("You can only apply for installments on your own fee records");
        }

        if (!"UNPAID".equalsIgnoreCase(assignment.getStatus()) && !"PARTIALLY_PAID".equalsIgnoreCase(assignment.getStatus())) {
            throw new BadRequestException("Installments can only be requested on UNPAID or PARTIALLY_PAID fees (Current status: " + assignment.getStatus() + ")");
        }

        assignment.setStatus("INSTALLMENT_REQUESTED");
        FeeAssignment saved = feeAssignmentRepository.save(assignment);
        return new FeeAssignmentDto(saved);
    }

    @Transactional
    public List<InstallmentDto> reviewInstallment(ReviewInstallmentRequest request) {
        FeeAssignment assignment = feeAssignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("FeeAssignment not found with ID: " + request.getAssignmentId()));

        if (!"INSTALLMENT_REQUESTED".equalsIgnoreCase(assignment.getStatus())) {
            throw new BadRequestException("Assignment is not in INSTALLMENT_REQUESTED state");
        }

        if ("REJECT".equalsIgnoreCase(request.getAction())) {
            assignment.setStatus("UNPAID");
            feeAssignmentRepository.save(assignment);
            return List.of();
        }

        // Action: APPROVE -> Calculate and generate installment schedule
        int count = (request.getNumberOfInstallments() != null && request.getNumberOfInstallments() == 3) ? 3 : 2;
        BigDecimal totalToSplit = assignment.getOutstandingAmount();
        BigDecimal perInstallment = totalToSplit.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);

        // Delete any existing dangling installments
        List<FeeInstallment> existing = installmentRepository.findByAssignment_AssignmentId(assignment.getAssignmentId());
        installmentRepository.deleteAll(existing);

        List<FeeInstallment> schedule = new ArrayList<>();
        LocalDate baseDate = LocalDate.now();

        for (int i = 1; i <= count; i++) {
            BigDecimal amount = (i == count)
                    // Adjust last installment for any fractional rounding difference
                    ? totalToSplit.subtract(perInstallment.multiply(BigDecimal.valueOf(count - 1)))
                    : perInstallment;

            LocalDate dueDate = baseDate.plusDays(30L * i);
            FeeInstallment fi = new FeeInstallment(assignment, i, dueDate, amount);
            schedule.add(fi);
        }

        List<FeeInstallment> savedSchedule = installmentRepository.saveAll(schedule);
        assignment.setStatus("INSTALLMENT_APPROVED");
        feeAssignmentRepository.save(assignment);

        return savedSchedule.stream().map(InstallmentDto::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InstallmentDto> getInstallmentsForStudent(Long studentId) {
        return installmentRepository.findByAssignment_Student_StudentId(studentId).stream()
                .map(InstallmentDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InstallmentDto> getInstallmentsForAssignment(Long assignmentId) {
        return installmentRepository.findByAssignment_AssignmentId(assignmentId).stream()
                .map(InstallmentDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FeeAssignmentDto> getPendingInstallmentRequests() {
        return feeAssignmentRepository.findAll().stream()
                .filter(fa -> "INSTALLMENT_REQUESTED".equalsIgnoreCase(fa.getStatus()))
                .map(FeeAssignmentDto::new)
                .collect(Collectors.toList());
    }
}
