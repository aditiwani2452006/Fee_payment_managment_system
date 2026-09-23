package com.mmcoe.feepay.feestructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {
    List<FeeStructure> findByDepartmentAndSemesterAndStatus(String department, Integer semester, String status);
    List<FeeStructure> findByStatus(String status);
    List<FeeStructure> findByCategory(String category);
    Optional<FeeStructure> findFirstByCategoryAndDepartmentAndSemesterAndStatus(String category, String department, Integer semester, String status);
    Optional<FeeStructure> findFirstByCategoryAndStatus(String category, String status);
    Optional<FeeStructure> findFirstByCategory(String category);
}
