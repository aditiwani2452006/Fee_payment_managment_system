package com.mmcoe.feepay.installment;

import com.mmcoe.feepay.common.ApiResponse;
import com.mmcoe.feepay.common.BadRequestException;
import com.mmcoe.feepay.config.JwtService;
import com.mmcoe.feepay.student.FeeAssignmentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Team 2: FR7 Apply for Installment Plan, FR8 Review Installments, FR9 View Installment Status
 * Role Gating:
 *  - Student: FR7 (Apply), FR9 (View own status)
 *  - Accounts Officer: FR8 (Review/Approve/Reject), View pending applications
 */
@RestController
@RequestMapping("/api/installments")
@Tag(name = "Installment Management (Team 2)", description = "FR7, FR8, FR9: Student installment requests & approval workflow")
@SecurityRequirement(name = "BearerAuth")
public class InstallmentController {

    private final InstallmentService installmentService;
    private final JwtService jwtService;

    public InstallmentController(InstallmentService installmentService, JwtService jwtService) {
        this.installmentService = installmentService;
        this.jwtService = jwtService;
    }

    private Long getStudentIdFromHeader(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            Long studentId = jwtService.extractStudentId(auth.substring(7));
            if (studentId != null) {
                return studentId;
            }
        }
        throw new BadRequestException("No authenticated student identity in security token");
    }

    @PostMapping("/apply")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Apply for Installment Plan", description = "FR7: Student requests fee division into 2 or 3 installments")
    public ResponseEntity<ApiResponse<FeeAssignmentDto>> applyForInstallment(
            HttpServletRequest request,
            @Valid @RequestBody ApplyInstallmentRequest req
    ) {
        Long studentId = getStudentIdFromHeader(request);
        FeeAssignmentDto updated = installmentService.applyForInstallment(studentId, req);
        return ResponseEntity.ok(ApiResponse.success("Installment application submitted successfully", updated));
    }

    @GetMapping("/my-installments")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "View Own Installment Schedule", description = "FR9: Student views generated installment schedule and dues")
    public ResponseEntity<ApiResponse<List<InstallmentDto>>> getMyInstallments(HttpServletRequest request) {
        Long studentId = getStudentIdFromHeader(request);
        return ResponseEntity.ok(ApiResponse.success(installmentService.getInstallmentsForStudent(studentId)));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ACCOUNTS_OFFICER')")
    @Operation(summary = "List Pending Installment Requests", description = "FR8: Accounts Officer retrieves all unapproved installment applications")
    public ResponseEntity<ApiResponse<List<FeeAssignmentDto>>> getPendingRequests() {
        return ResponseEntity.ok(ApiResponse.success(installmentService.getPendingInstallmentRequests()));
    }

    @PostMapping("/review")
    @PreAuthorize("hasRole('ACCOUNTS_OFFICER')")
    @Operation(summary = "Approve or Reject Installment Request", description = "FR8: Accounts Officer approves or rejects installment plan; generates schedule rows on approval")
    public ResponseEntity<ApiResponse<List<InstallmentDto>>> reviewInstallment(
            @Valid @RequestBody ReviewInstallmentRequest req
    ) {
        List<InstallmentDto> schedule = installmentService.reviewInstallment(req);
        String msg = "APPROVE".equalsIgnoreCase(req.getAction())
                ? "Installment plan approved and schedule generated"
                : "Installment plan rejected";
        return ResponseEntity.ok(ApiResponse.success(msg, schedule));
    }

    @GetMapping("/assignment/{assignmentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ACCOUNTS_OFFICER', 'ADMIN')")
    @Operation(summary = "Get Installments for a Fee Assignment", description = "Returns installment schedule rows for a specific assignment ID")
    public ResponseEntity<ApiResponse<List<InstallmentDto>>> getForAssignment(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(ApiResponse.success(installmentService.getInstallmentsForAssignment(assignmentId)));
    }
}
