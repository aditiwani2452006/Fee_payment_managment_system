package com.mmcoe.feepay.auth;

import com.mmcoe.feepay.common.ApiResponse;
import com.mmcoe.feepay.common.BadRequestException;
import com.mmcoe.feepay.config.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Team 1: FR2 Login Authentication & Password Management Endpoints
 * Concepts: Computer Networks (CN: REST API, Status 200 OK, JWT Bearer Token, Client IP Logging).
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication (Team 1)", description = "Endpoints for user login, password change/reset, and JWT token issuance (FR2)")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isEmpty()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticates Student (via PRN or username), Accounts Officer, or Admin and returns JWT token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            HttpServletRequest request,
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        String ip = getClientIp(request);
        AuthResponse response = authService.authenticate(loginRequest, ip);
        return ResponseEntity.ok(ApiResponse.success("Authentication successful", response));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change Password", description = "Authenticated user changes password by verifying existing password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            HttpServletRequest request,
            @Valid @RequestBody ChangePasswordRequest body
    ) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadRequestException("Bearer token required for password change");
        }
        String token = authHeader.substring(7);
        Long userId = jwtService.extractUserId(token);
        if (userId == null) {
            throw new BadRequestException("Invalid user token");
        }

        String ip = getClientIp(request);
        authService.changePassword(userId, body.getOldPassword(), body.getNewPassword(), ip);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", "OK"));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Forgot / Reset Password", description = "Academic reset workflow using student PRN/username and new password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            HttpServletRequest request,
            @Valid @RequestBody ResetPasswordRequest body
    ) {
        String ip = getClientIp(request);
        authService.resetPassword(body.getIdentifier(), body.getNewPassword(), ip);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully. You can now login with your new password.", "OK"));
    }
}
