package com.mmcoe.feepay.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * ============================================================================
 * Team 2: Payment Processing & Transaction Management
 * DESIGNED STUB: Payment Rollback Flow (State Machine)
 * 
 * STATE MACHINE SPECIFICATION:
 *   [INITIATED] ──> [PROCESSING] ──> [SUCCESS]
 *         │               │              │
 *         ▼               ▼              ▼
 *      [FAILED]       [FAILED]    [ROLLED_BACK] (Compensating Transaction)
 * 
 * Concepts:
 *  - Operating Systems & Distributed Systems: Compensating transactions & SAGA pattern
 *  - DBMS: Two-phase commit / state rollback mechanism
 * ============================================================================
 */
@Service
public class PaymentRollbackService {

    private static final Logger log = LoggerFactory.getLogger(PaymentRollbackService.class);

    /**
     * Designed Stub: Initiates financial rollback / refund flow.
     * In a production implementation, this issues a refund call to Razorpay API
     * and credits the student's FeeAssignment outstanding_amount.
     * 
     * @param transactionId ID of the transaction to roll back
     * @param reason Reason for rollback (e.g. Duplicate debit, administrative error)
     * @return Current state machine status
     */
    public String initiateRollback(Long transactionId, String reason) {
        log.info("[DESIGNED STUB] Rollback requested for Transaction ID: {} | Reason: {}", transactionId, reason);
        log.info("[STATE MACHINE] Transitioning state: SUCCESS -> ROLLED_BACK");
        log.info("[DBMS STUB] In full build, this restores FeeAssignment balance and marks Transaction as REFUNDED");

        return "ROLLED_BACK (Simulated Compensating Transaction for TX: " + transactionId + ")";
    }
}
