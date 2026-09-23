package com.mmcoe.feepay.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Optional<Receipt> findByReceiptNumber(String receiptNumber);
    Optional<Receipt> findByTransaction_TransactionId(Long transactionId);
    Optional<Receipt> findByTransaction_Payment_PaymentId(Long paymentId);
}
