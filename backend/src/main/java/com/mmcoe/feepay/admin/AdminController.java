package com.mmcoe.feepay.admin;

import com.mmcoe.feepay.auth.AuthService;
import com.mmcoe.feepay.auth.CreateUserRequest;
import com.mmcoe.feepay.auth.UserAccount;
import com.mmcoe.feepay.common.ApiResponse;
import com.mmcoe.feepay.config.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Team 3: FR15 Admin Dashboard, Audit Logs & System Administration
 * Role Gating: Strictly restricted to ROLE_ADMIN.
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Administration & Audit (Team 3)", description = "FR15: System audit logs, security telemetry, account provisioning, backup trigger")
@SecurityRequirement(name = "BearerAuth")
public class AdminController {

    private final AdminService adminService;
    private final BackupService backupService;
    private final AuthService authService;
    private final JwtService jwtService;

    public AdminController(
            AdminService adminService,
            BackupService backupService,
            AuthService authService,
            JwtService jwtService
    ) {
        this.adminService = adminService;
        this.backupService = backupService;
        this.authService = authService;
        this.jwtService = jwtService;
    }

    private Long getUserIdFromHeader(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return jwtService.extractUserId(auth.substring(7));
        }
        return null;
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "View System Audit Logs", description = "FR15: Admin inspects state-mutating actions across the system")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAuditLogs() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getRecentAuditLogs()));
    }

    @GetMapping("/security-logs")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "View Security Telemetry Logs", description = "FR15: Admin inspects login events, IP addresses, and access telemetry")
    public ResponseEntity<ApiResponse<List<SecurityLog>>> getSecurityLogs() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getRecentSecurityLogs()));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List Provisioned Accounts", description = "FR1 & FR15: Admin inspects all provisioned user accounts")
    public ResponseEntity<ApiResponse<List<UserAccount>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getAllUsers()));
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Provision New User Account", description = "FR1: Admin provisions student, accounts officer, or admin account")
    public ResponseEntity<ApiResponse<UserAccount>> provisionUser(@Valid @RequestBody CreateUserRequest req) {
        UserAccount created = authService.provisionAccount(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User account provisioned successfully", created));
    }

    @PostMapping("/backup")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Trigger Database Backup (Designed Stub)", description = "FR15 Stub: Spawns cold backup simulation and records entry in BackupHistory")
    public ResponseEntity<ApiResponse<BackupHistory>> triggerBackup(HttpServletRequest request) {
        Long adminId = getUserIdFromHeader(request);
        BackupHistory backup = backupService.triggerBackup(adminId != null ? adminId : 4L);
        return ResponseEntity.ok(ApiResponse.success("Database backup completed successfully (Designed Stub)", backup));
    }

    @GetMapping("/backup/history")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "View Backup History", description = "FR15 Stub: Inspect historical backup records")
    public ResponseEntity<ApiResponse<List<BackupHistory>>> getBackupHistory() {
        return ResponseEntity.ok(ApiResponse.success(backupService.getBackupHistory()));
    }
}
