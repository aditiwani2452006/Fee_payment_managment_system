package com.mmcoe.feepay.feestructure;

import com.mmcoe.feepay.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Team 1: FR5 Manage Fee Structure & FR6 View Fee Structure
 * Security: Gated via @PreAuthorize according to SRS Section 3 RBAC.
 */
@RestController
@RequestMapping("/api/feestructures")
@Tag(name = "Fee Structure Management (Team 1)", description = "FR5 & FR6: Configure and view fee catalogue")
@SecurityRequirement(name = "BearerAuth")
public class FeeStructureController {

    private final FeeStructureService feeStructureService;

    public FeeStructureController(FeeStructureService feeStructureService) {
        this.feeStructureService = feeStructureService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'ACCOUNTS_OFFICER', 'ADMIN')")
    @Operation(summary = "View Active Fee Structures", description = "FR6: View fee structures filtered optionally by department and semester")
    public ResponseEntity<ApiResponse<List<FeeStructureDto>>> getAll(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Integer semester
    ) {
        List<FeeStructureDto> results;
        if (department != null && semester != null) {
            results = feeStructureService.getFeeStructuresForDepartment(department, semester);
        } else {
            results = feeStructureService.getAllActiveFeeStructures();
        }
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Fee Structure (Admin only)", description = "FR5: Admin creates a new fee structure record")
    public ResponseEntity<ApiResponse<FeeStructureDto>> create(@Valid @RequestBody FeeStructureDto dto) {
        FeeStructureDto created = feeStructureService.createFeeStructure(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Fee structure configured successfully", created));
    }

    @PutMapping("/{feeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Fee Structure (Admin only)", description = "FR5: Admin updates an existing fee structure")
    public ResponseEntity<ApiResponse<FeeStructureDto>> update(
            @PathVariable Long feeId,
            @Valid @RequestBody FeeStructureDto dto
    ) {
        FeeStructureDto updated = feeStructureService.updateFeeStructure(feeId, dto);
        return ResponseEntity.ok(ApiResponse.success("Fee structure updated successfully", updated));
    }
}
