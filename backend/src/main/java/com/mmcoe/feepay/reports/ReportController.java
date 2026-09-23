package com.mmcoe.feepay.reports;

import com.mmcoe.feepay.common.ApiResponse;
import com.mmcoe.feepay.config.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Team 3: FR14 Generate Financial Reports
 * Role Gating: Restricted to Accounts Officer and Admin.
 */
@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports & Analytics (Team 3)", description = "FR14: Fee collection, pending dues, and departmental financial analytics")
@SecurityRequirement(name = "BearerAuth")
public class ReportController {

    private final ReportService reportService;
    private final JwtService jwtService;

    public ReportController(ReportService reportService, JwtService jwtService) {
        this.reportService = reportService;
        this.jwtService = jwtService;
    }

    private Long getUserIdFromHeader(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return jwtService.extractUserId(auth.substring(7));
        }
        return null;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ACCOUNTS_OFFICER', 'ADMIN')")
    @Operation(summary = "Generate Financial Summary Report", description = "FR14: Real-time calculation of fee collection, pending dues, and department breakdown")
    public ResponseEntity<ApiResponse<FinancialSummaryDto>> getSummaryReport(
            HttpServletRequest request,
            @RequestParam(defaultValue = "FEE_COLLECTION") String reportType
    ) {
        Long userId = getUserIdFromHeader(request);
        FinancialSummaryDto summary = reportService.generateSummaryReport(userId, reportType);
        return ResponseEntity.ok(ApiResponse.success("Report generated successfully", summary));
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('ACCOUNTS_OFFICER', 'ADMIN')")
    @Operation(summary = "View Generated Reports History", description = "FR14: Audit history of previously requested report runs")
    public ResponseEntity<ApiResponse<List<Report>>> getReportHistory() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getReportAuditHistory()));
    }
}
