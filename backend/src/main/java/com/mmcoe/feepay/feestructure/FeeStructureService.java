package com.mmcoe.feepay.feestructure;

import com.mmcoe.feepay.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Team 1: FR5 Manage Fee Structure (Admin creates/updates fee structures)
 * Concepts: DBMS (Data manipulation and query filtering) & OOP (DTO to Domain mapping).
 */
@Service
public class FeeStructureService {

    private final FeeStructureRepository feeStructureRepository;

    public FeeStructureService(FeeStructureRepository feeStructureRepository) {
        this.feeStructureRepository = feeStructureRepository;
    }

    @Transactional(readOnly = true)
    public List<FeeStructureDto> getAllActiveFeeStructures() {
        return feeStructureRepository.findByStatus("ACTIVE").stream()
                .map(FeeStructureDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FeeStructureDto> getFeeStructuresForDepartment(String department, Integer semester) {
        return feeStructureRepository.findByDepartmentAndSemesterAndStatus(department, semester, "ACTIVE").stream()
                .map(FeeStructureDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FeeStructureDto> getFeeStructuresByCategory(String category) {
        return feeStructureRepository.findByCategory(category).stream()
                .map(FeeStructureDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public FeeStructureDto createFeeStructure(FeeStructureDto dto) {
        BigDecimal tuition = dto.getTuitionFee() != null ? dto.getTuitionFee() : BigDecimal.ZERO;
        BigDecimal dev = dto.getDevelopmentFee() != null ? dto.getDevelopmentFee() : BigDecimal.ZERO;
        BigDecimal other = dto.getOtherFees() != null ? dto.getOtherFees() : BigDecimal.ZERO;
        BigDecimal caution = dto.getCautionMoney() != null ? dto.getCautionMoney() : BigDecimal.ZERO;

        BigDecimal total = dto.getAmount();
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            total = tuition.add(dev).add(other).add(caution);
        }

        FeeStructure feeStructure = new FeeStructure(
                dto.getDepartment(),
                dto.getSemester(),
                dto.getCategory() != null ? dto.getCategory() : "OPEN",
                dto.getFeeType(),
                tuition,
                dev,
                other,
                caution,
                dto.getDueDate(),
                dto.getAcademicYear(),
                dto.getDescription()
        );
        feeStructure.setAmount(total);

        FeeStructure saved = feeStructureRepository.save(feeStructure);
        return new FeeStructureDto(saved);
    }

    @Transactional
    public FeeStructureDto updateFeeStructure(Long feeId, FeeStructureDto dto) {
        FeeStructure feeStructure = feeStructureRepository.findById(feeId)
                .orElseThrow(() -> new ResourceNotFoundException("FeeStructure not found with ID: " + feeId));

        feeStructure.setDepartment(dto.getDepartment());
        feeStructure.setSemester(dto.getSemester());
        if (dto.getCategory() != null) {
            feeStructure.setCategory(dto.getCategory());
        }
        feeStructure.setFeeType(dto.getFeeType());

        if (dto.getTuitionFee() != null) feeStructure.setTuitionFee(dto.getTuitionFee());
        if (dto.getDevelopmentFee() != null) feeStructure.setDevelopmentFee(dto.getDevelopmentFee());
        if (dto.getOtherFees() != null) feeStructure.setOtherFees(dto.getOtherFees());
        if (dto.getCautionMoney() != null) feeStructure.setCautionMoney(dto.getCautionMoney());

        if (dto.getAmount() != null) {
            feeStructure.setAmount(dto.getAmount());
        } else {
            feeStructure.setAmount(feeStructure.getTuitionFee().add(feeStructure.getDevelopmentFee()).add(feeStructure.getOtherFees()).add(feeStructure.getCautionMoney()));
        }

        feeStructure.setDueDate(dto.getDueDate());
        feeStructure.setAcademicYear(dto.getAcademicYear());
        feeStructure.setDescription(dto.getDescription());
        if (dto.getStatus() != null) {
            feeStructure.setStatus(dto.getStatus());
        }

        FeeStructure updated = feeStructureRepository.save(feeStructure);
        return new FeeStructureDto(updated);
    }
}
