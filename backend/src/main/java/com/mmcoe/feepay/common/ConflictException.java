package com.mmcoe.feepay.common;

/**
 * Thrown when a concurrent update, double-submission, or race condition occurs.
 * Demonstrates: OS (Concurrency violation detection) & CN (HTTP 409 Conflict).
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
