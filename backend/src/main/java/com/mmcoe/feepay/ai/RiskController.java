package com.mmcoe.feepay.ai;

import com.mmcoe.feepay.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Coursework Demo Add-on for AI Subject
 * Restricted to Accounts Officer & Admin.
 */
@RestController
@RequestMapping("/api/ai/risk-assessment")
@Tag(name = "AI Default Risk (Coursework Add-on)", description = "Predictive fee default risk assessment demo for AI subject requirement")
@SecurityRequirement(name = "BearerAuth")
public class RiskController {

    private final FeeDefaultRiskService riskService;

    public RiskController(FeeDefaultRiskService riskService) {
        this.riskService = riskService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ACCOUNTS_OFFICER', 'ADMIN')")
    @Operation(summary = "Evaluate Student Fee Default Risk", description = "Coursework Demo: Generates risk probability scores using rule-based/logistic formulation")
    public ResponseEntity<ApiResponse<List<RiskAssessmentDto>>> getRiskAssessments() {
        return ResponseEntity.ok(ApiResponse.success("Risk evaluations generated (Coursework Demo Only)", riskService.evaluateAllStudentsRisk()));
    }
}
