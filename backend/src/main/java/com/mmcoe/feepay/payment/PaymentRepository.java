package com.mmcoe.feepay.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<FeePayment, Long> {
    List<FeePayment> findByStudent_StudentId(Long studentId);
    List<FeePayment> findByInstallment_InstallmentId(Long installmentId);
}
