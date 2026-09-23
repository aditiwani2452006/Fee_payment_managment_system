package com.mmcoe.feepay.student;

import com.mmcoe.feepay.common.ApiResponse;
import com.mmcoe.feepay.common.BadRequestException;
import com.mmcoe.feepay.config.JwtService;
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
 * Team 1: FR4 Manage Student Profile & FR6 View Fee Structure & Dues
 * Gated with @PreAuthorize roles according to SRS Section 3.
 */
@RestController
@RequestMapping("/api/students")
@Tag(name = "Student Operations (Team 1)", description = "FR4 & FR6: Profile inspection, update, and assigned fee dues")
@SecurityRequirement(name = "BearerAuth")
public class StudentController {

    private final StudentService studentService;
    private final JwtService jwtService;

    public StudentController(StudentService studentService, JwtService jwtService) {
        this.studentService = studentService;
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

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get Authenticated Student Profile", description = "FR4: Returns personal profile details of logged-in student")
    public ResponseEntity<ApiResponse<StudentProfileDto>> getMyProfile(HttpServletRequest request) {
        Long studentId = getStudentIdFromHeader(request);
        return ResponseEntity.ok(ApiResponse.success(studentService.getProfile(studentId)));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Update Authenticated Student Profile", description = "FR4: Allows student to update contact number and email")
    public ResponseEntity<ApiResponse<StudentProfileDto>> updateMyProfile(
            HttpServletRequest request,
            @Valid @RequestBody UpdateStudentRequest req
    ) {
        Long studentId = getStudentIdFromHeader(request);
        StudentProfileDto updated = studentService.updateProfile(studentId, req);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }

    @GetMapping("/me/fees")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "View Assigned Fees & Ledger Dues", description = "FR6: Student views fee structures, paid amounts, and outstanding balances")
    public ResponseEntity<ApiResponse<List<FeeAssignmentDto>>> getMyFees(HttpServletRequest request) {
        Long studentId = getStudentIdFromHeader(request);
        return ResponseEntity.ok(ApiResponse.success(studentService.getStudentFeeAssignments(studentId)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ACCOUNTS_OFFICER', 'ADMIN')")
    @Operation(summary = "List All Students", description = "Admin and Accounts Officer view list of enrolled students")
    public ResponseEntity<ApiResponse<List<StudentProfileDto>>> getAllStudents() {
        return ResponseEntity.ok(ApiResponse.success(studentService.getAllStudents()));
    }

    @GetMapping("/{studentId}/fees")
    @PreAuthorize("hasAnyRole('ACCOUNTS_OFFICER', 'ADMIN')")
    @Operation(summary = "View Fees for a Student", description = "Accounts Officer/Admin views fee assignments for specific student")
    public ResponseEntity<ApiResponse<List<FeeAssignmentDto>>> getStudentFees(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success(studentService.getStudentFeeAssignments(studentId)));
    }

    @PostMapping("/{studentId}/assign-fee/{feeStructureId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign Fee Structure to Student", description = "Admin assigns a specific fee structure to a student")
    public ResponseEntity<ApiResponse<FeeAssignmentDto>> assignFee(
            @PathVariable Long studentId,
            @PathVariable Long feeStructureId
    ) {
        FeeAssignmentDto dto = studentService.assignFee(studentId, feeStructureId);
        return ResponseEntity.ok(ApiResponse.success("Fee structure assigned successfully", dto));
    }
}
