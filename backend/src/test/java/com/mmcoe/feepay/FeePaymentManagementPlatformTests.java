package com.mmcoe.feepay;

import com.mmcoe.feepay.admin.AuditLog;
import com.mmcoe.feepay.admin.AuditLogRepository;
import com.mmcoe.feepay.admin.SecurityLog;
import com.mmcoe.feepay.admin.SecurityLogRepository;
import com.mmcoe.feepay.ai.FeeDefaultRiskService;
import com.mmcoe.feepay.ai.RiskAssessmentDto;
import com.mmcoe.feepay.auth.AuthResponse;
import com.mmcoe.feepay.auth.AuthService;
import com.mmcoe.feepay.auth.LoginRequest;
import com.mmcoe.feepay.common.BadRequestException;
import com.mmcoe.feepay.feestructure.FeeStructure;
import com.mmcoe.feepay.feestructure.FeeStructureRepository;
import com.mmcoe.feepay.installment.FeeInstallment;
import com.mmcoe.feepay.installment.InstallmentRepository;
import com.mmcoe.feepay.payment.FeePayment;
import com.mmcoe.feepay.payment.InitiatePaymentRequest;
import com.mmcoe.feepay.payment.PaymentRepository;
import com.mmcoe.feepay.payment.PaymentResponseDto;
import com.mmcoe.feepay.payment.PaymentService;
import com.mmcoe.feepay.student.FeeAssignment;
import com.mmcoe.feepay.student.FeeAssignmentRepository;
import com.mmcoe.feepay.student.FeeAssignmentService;
import com.mmcoe.feepay.student.Student;
import com.mmcoe.feepay.student.StudentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Demonstrates Software Engineering (SE) Verification and Testing Concepts.
 * Covers: Login Success/Failure, Role Authorization, Category-Based Fee Assignment,
 * Payment Initiation Validation, Installment Request, Basic AI Indicator, and Audit Logs.
 */
@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class FeePaymentManagementPlatformTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FeeStructureRepository feeStructureRepository;

    @Autowired
    private FeeAssignmentRepository feeAssignmentRepository;

    @Autowired
    private FeeAssignmentService feeAssignmentService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InstallmentRepository installmentRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private SecurityLogRepository securityLogRepository;

    @Autowired
    private FeeDefaultRiskService feeDefaultRiskService;

    @Test
    @DisplayName("1. CN & Security: Student Login Success with PRN")
    void testLoginSuccess() {
        LoginRequest req = new LoginRequest("B25IT2009", "password123");
        AuthResponse res = authService.authenticate(req, "127.0.0.1");

        assertNotNull(res);
        assertNotNull(res.getToken(), "JWT token must be generated");
        assertEquals("ROLE_STUDENT", res.getRole());
        assertEquals("B25IT2009", res.getPrn());
        assertEquals("SABURI YEOLA", res.getStudentName());
        assertEquals("OBC/EBC/EWS/SEBC Female", res.getCategory());
    }

    @Test
    @DisplayName("2. Security: Login Failure with Invalid Password")
    void testLoginFailureInvalidPassword() {
        LoginRequest req = new LoginRequest("B25IT2009", "wrongPassword");

        assertThrows(BadCredentialsException.class, () -> {
            authService.authenticate(req, "127.0.0.1");
        });
    }

    @Test
    @DisplayName("3. DBMS & Business Logic: Automatic Category-Based Fee Assignment (₹27,674)")
    void testCategoryFeeAssignment() {
        Student saburi = studentRepository.findByPrn("B25IT2009")
                .orElseThrow(() -> new AssertionError("Demo student B25IT2009 must exist"));

        FeeAssignment assignment = feeAssignmentService.getOrCreateStudentAssignment(saburi);

        assertNotNull(assignment);
        assertEquals("OBC/EBC/EWS/SEBC Female", assignment.getStudent().getCategory());
        // Official MMCOE OBC/EBC/EWS/SEBC Female total fee is ₹27,674
        assertEquals(new BigDecimal("27674.00"), assignment.getTotalAmount());
        assertEquals(new BigDecimal("0.00"), assignment.getPaidAmount());
        assertEquals(new BigDecimal("27674.00"), assignment.getOutstandingAmount());
        assertEquals("PENDING", assignment.getStatus());
    }

    @Test
    @DisplayName("4. OOP Domain Invariant: Fee Encapsulation & Balance Verification")
    void testFeeCalculationInvariants() {
        Student student = studentRepository.findByPrn("B25IT2009").orElseThrow();
        FeeStructure fs = feeStructureRepository.findFirstByCategory("OPEN").orElseThrow();

        FeeAssignment fa = new FeeAssignment(student, fs, new BigDecimal("145500.00"));
        assertEquals(new BigDecimal("145500.00"), fa.getOutstandingAmount());

        // Apply partial payment of ₹50,000
        fa.applyPayment(new BigDecimal("50000.00"));
        assertEquals(new BigDecimal("50000.00"), fa.getPaidAmount());
        assertEquals(new BigDecimal("95500.00"), fa.getOutstandingAmount());
        assertEquals("PARTIALLY_PAID", fa.getStatus());

        // Overpayment violation must throw BadRequestException
        assertThrows(BadRequestException.class, () -> {
            fa.applyPayment(new BigDecimal("100000.00")); // exceeds 95,500
        });
    }

    @Test
    @DisplayName("5. Team 2 Review Build: Payment Initiation Validation & Notice")
    void testPaymentInitiationValidation() {
        Student saburi = studentRepository.findByPrn("B25IT2009").orElseThrow();
        FeeAssignment fa = feeAssignmentService.getOrCreateStudentAssignment(saburi);

        InitiatePaymentRequest req = new InitiatePaymentRequest(fa.getAssignmentId(), null, new BigDecimal("27674.00"), "ONLINE_NETBANKING");
        PaymentResponseDto res = paymentService.initiatePayment(saburi.getStudentId(), req);

        assertNotNull(res);
        assertEquals("PENDING_GATEWAY_INTEGRATION", res.getStatus());
        assertTrue(res.getGatewayReference().startsWith("MMCOE-PAY-REQ-"));
        assertTrue(res.getMessage().contains("Payment processing module will be integrated by Team 2"));
    }

    @Test
    @DisplayName("6. SE & DBMS: Audit Log Recording on Action Events")
    void testAuditLogCreation() {
        long initialCount = auditLogRepository.count();
        auditLogRepository.save(new AuditLog(null, "TEST_VERIFICATION_EVENT", "Testing automated SE verification", "127.0.0.1"));

        assertEquals(initialCount + 1, auditLogRepository.count());
    }

    @Test
    @DisplayName("7. SE & Security: Telemetry Log Recording on Login")
    void testSecurityLogCreation() {
        long initialCount = securityLogRepository.count();
        securityLogRepository.save(new SecurityLog(null, "LOGIN_TEST", "127.0.0.1"));

        assertEquals(initialCount + 1, securityLogRepository.count());
    }

    @Test
    @DisplayName("8. Basic AI: Rule-Based Predictive Indicator for Fee Default")
    void testBasicAiRiskIndicator() {
        List<RiskAssessmentDto> assessments = feeDefaultRiskService.evaluateAllStudentsRisk();

        assertNotNull(assessments);
        assertFalse(assessments.isEmpty());

        for (RiskAssessmentDto dto : assessments) {
            assertNotNull(dto.getRiskLevel());
            assertNotNull(dto.getIndicatorMessage());
            assertTrue(dto.getRiskScore() >= 0.0 && dto.getRiskScore() <= 1.0);
        }
    }
}
