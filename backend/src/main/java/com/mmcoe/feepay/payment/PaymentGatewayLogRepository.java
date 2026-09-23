package com.mmcoe.feepay.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentGatewayLogRepository extends JpaRepository<PaymentGatewayLog, Long> {
    List<PaymentGatewayLog> findByTransaction_TransactionId(Long transactionId);
}
