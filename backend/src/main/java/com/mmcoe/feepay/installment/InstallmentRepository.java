package com.mmcoe.feepay.installment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstallmentRepository extends JpaRepository<FeeInstallment, Long> {
    List<FeeInstallment> findByAssignment_AssignmentId(Long assignmentId);
    List<FeeInstallment> findByAssignment_Student_StudentId(Long studentId);
}
