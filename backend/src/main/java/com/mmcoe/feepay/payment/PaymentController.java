package com.mmcoe.feepay.payment;

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
 * Team 2: Payment Processing & Transaction Management Controller
 * Implements: FR10 (Initiate Payment), FR11 (Process Payment), FR12 (Receipt Verification), FR13 (History).
 * Gated with @PreAuthorize roles.
 */
@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments & Transactions (Team 2)", description = "FR10, FR11, FR12, FR13: Payment initiation, Razorpay test mode, receipt generation")
@SecurityRequirement(name = "BearerAuth")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRollbackService rollbackService;
    private final JwtService jwtService;

    public PaymentController(
            PaymentService paymentService,
            PaymentRollbackService rollbackService,
            JwtService jwtService
    ) {
        this.paymentService = paymentService;
        this.rollbackService = rollbackService;
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

    @PostMapping("/initiate")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Initiate Fee Payment Order", description = "FR10: Student creates pending payment order before invoking Razorpay checkout")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> initiatePayment(
            HttpServletRequest request,
            @Valid @RequestBody InitiatePaymentRequest req
    ) {
        Long studentId = getStudentIdFromHeader(request);
        PaymentResponseDto response = paymentService.initiatePayment(studentId, req);
        return ResponseEntity.ok(ApiResponse.success("Payment initiated successfully", response));
    }

    @PostMapping("/process")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Process and Confirm Payment", description = "FR11 & FR12: Processes Razorpay test callback, applies pessimistic lock, and issues receipt")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> processPayment(
            @Valid @RequestBody ProcessPaymentRequest req
    ) {
        PaymentResponseDto response = paymentService.processPayment(req);
        return ResponseEntity.ok(ApiResponse.success("Payment processed successfully and receipt generated", response));
    }

    @GetMapping("/receipt/{receiptNumber}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ACCOUNTS_OFFICER', 'ADMIN')")
    @Operation(summary = "Get Receipt by Receipt Number", description = "FR12 & DSA: In-memory O(1) hash table lookup for verified receipt")
    public ResponseEntity<ApiResponse<ReceiptDto>> getReceipt(@PathVariable String receiptNumber) {
        ReceiptDto receipt = paymentService.getReceiptByNumber(receiptNumber);
        return ResponseEntity.ok(ApiResponse.success(receipt));
    }

    @GetMapping("/my-history")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "View Personal Payment History", description = "FR13 & DSA: Returns payment history with optional in-memory QuickSort by amount")
    public ResponseEntity<ApiResponse<List<PaymentResponseDto>>> getMyHistory(
            HttpServletRequest request,
            @RequestParam(defaultValue = "false") boolean sortByAmount
    ) {
        Long studentId = getStudentIdFromHeader(request);
        List<PaymentResponseDto> history = paymentService.getStudentPaymentHistory(studentId, sortByAmount);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ACCOUNTS_OFFICER', 'ADMIN')")
    @Operation(summary = "List All Payment Transactions", description = "FR13: Accounts Officer & Admin view global ledger of payments")
    public ResponseEntity<ApiResponse<List<PaymentResponseDto>>> getAllPayments() {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getAllPayments()));
    }

    @PostMapping("/rollback/{transactionId}")
    @PreAuthorize("hasRole('ACCOUNTS_OFFICER')")
    @Operation(summary = "Initiate Payment Rollback (Designed Stub)", description = "Demonstrates state machine transition to ROLLED_BACK")
    public ResponseEntity<ApiResponse<String>> rollbackPayment(
            @PathVariable Long transactionId,
            @RequestParam(defaultValue = "Disputed or duplicate payment") String reason
    ) {
        String result = rollbackService.initiateRollback(transactionId, reason);
        return ResponseEntity.ok(ApiResponse.success("Rollback initiated", result));
    }
}
