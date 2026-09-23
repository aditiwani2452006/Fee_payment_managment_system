package com.mmcoe.feepay.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByGatewayReference(String gatewayReference);
    boolean existsByGatewayReference(String gatewayReference);
}
