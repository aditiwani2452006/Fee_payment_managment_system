package com.mmcoe.feepay.student;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for FeeAssignment.
 * Concepts:
 *  - OS: Pessimistic Row-Level Locking (SELECT ... FOR UPDATE) to prevent race conditions during concurrent payments
 *  - DBMS: Indexed retrieval by studentId
 */
@Repository
public interface FeeAssignmentRepository extends JpaRepository<FeeAssignment, Long> {

    List<FeeAssignment> findByStudent_StudentId(Long studentId);

    Optional<FeeAssignment> findFirstByStudent_StudentId(Long studentId);

    boolean existsByStudent_StudentId(Long studentId);

    /**
     * Acquires an exclusive row-level lock (SELECT ... FOR UPDATE) on the FeeAssignment row.
     * Prevents race conditions during simultaneous payment submissions.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT fa FROM FeeAssignment fa WHERE fa.assignmentId = :id")
    Optional<FeeAssignment> findByIdWithLock(@Param("id") Long id);
}
