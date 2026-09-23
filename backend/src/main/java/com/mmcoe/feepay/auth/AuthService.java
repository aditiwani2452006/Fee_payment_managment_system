package com.mmcoe.feepay.auth;

import com.mmcoe.feepay.admin.AuditLog;
import com.mmcoe.feepay.admin.AuditLogRepository;
import com.mmcoe.feepay.admin.SecurityLog;
import com.mmcoe.feepay.admin.SecurityLogRepository;
import com.mmcoe.feepay.common.BadRequestException;
import com.mmcoe.feepay.common.ResourceNotFoundException;
import com.mmcoe.feepay.config.JwtService;
import com.mmcoe.feepay.student.Student;
import com.mmcoe.feepay.student.StudentRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Team 1: Student, Authentication & Fee Management
 * Implements FR1 (Student Account Provisioning) & FR2 (Login Authentication) & Password Reset.
 * Concepts:
 *  - CN: Cryptographic JWT token issuance & authentication handshake
 *  - DBMS: Transactional boundary across UserAccount, Role, and Student tables
 *  - OOP: Encapsulated business domain rules for account lifecycle
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuditLogRepository auditLogRepository;
    private final SecurityLogRepository securityLogRepository;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            StudentRepository studentRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuditLogRepository auditLogRepository,
            SecurityLogRepository securityLogRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.auditLogRepository = auditLogRepository;
        this.securityLogRepository = securityLogRepository;
    }

    @Transactional
    public AuthResponse authenticate(LoginRequest request, String clientIp) {
        String identifier = request.getUsername() != null ? request.getUsername().trim() : "";

        UserAccount user = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findFirstByStudent_Prn(identifier))
                .orElseThrow(() -> {
                    securityLogRepository.save(new SecurityLog(null, "LOGIN_FAILED", clientIp != null ? clientIp : "127.0.0.1"));
                    return new BadCredentialsException("Invalid PRN or password.");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            securityLogRepository.save(new SecurityLog(user, "LOGIN_FAILED", clientIp != null ? clientIp : "127.0.0.1"));
            throw new BadCredentialsException("Invalid PRN or password.");
        }

        Long studentId = (user.getStudent() != null) ? user.getStudent().getStudentId() : null;
        String studentName = (user.getStudent() != null) ? user.getStudent().getName() : null;
        String prn = (user.getStudent() != null) ? user.getStudent().getPrn() : null;
        String category = (user.getStudent() != null) ? user.getStudent().getCategory() : null;
        String branch = (user.getStudent() != null) ? user.getStudent().getDepartment() : null;
        String status = (user.getStudent() != null) ? user.getStudent().getStatus() : "Active";
        String roleName = user.getRole().getRoleName();

        String token = jwtService.generateToken(user.getUserId(), user.getUsername(), roleName, studentId);

        // Record successful login in security telemetry & audit trail
        securityLogRepository.save(new SecurityLog(user, "LOGIN_SUCCESS", clientIp != null ? clientIp : "127.0.0.1"));
        auditLogRepository.save(new AuditLog(user, "LOGIN_SUCCESS", "User authenticated: " + user.getUsername(), clientIp));

        return new AuthResponse(
                token,
                user.getUserId(),
                user.getUsername(),
                roleName,
                studentId,
                studentName,
                prn,
                category,
                branch,
                status
        );
    }

    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword, String clientIp) {
        if (newPassword == null || newPassword.trim().length() < 6) {
            throw new BadRequestException("New password must be at least 6 characters long.");
        }

        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new BadRequestException("Current password does not match.");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        auditLogRepository.save(new AuditLog(user, "CHANGE_PASSWORD", "User changed password", clientIp));
    }

    @Transactional
    public void resetPassword(String identifier, String newPassword, String clientIp) {
        if (newPassword == null || newPassword.trim().length() < 6) {
            throw new BadRequestException("New password must be at least 6 characters long.");
        }

        UserAccount user = userRepository.findByUsername(identifier.trim())
                .or(() -> userRepository.findFirstByStudent_Prn(identifier.trim()))
                .orElseThrow(() -> new ResourceNotFoundException("No account registered with identifier: " + identifier));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        auditLogRepository.save(new AuditLog(user, "RESET_PASSWORD", "Password reset for account: " + user.getUsername(), clientIp));
    }

    @Transactional
    public UserAccount provisionAccount(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username '" + request.getUsername() + "' already exists");
        }

        Role role = roleRepository.findByRoleName(request.getRole())
                .orElseGet(() -> roleRepository.save(new Role(request.getRole())));

        Student student = null;
        if ("ROLE_STUDENT".equalsIgnoreCase(request.getRole())) {
            student = new Student(
                    request.getUsername(),
                    request.getStudentName() != null ? request.getStudentName() : request.getUsername(),
                    request.getCourse() != null ? request.getCourse() : "B.Tech. Information Technology",
                    "Information Technology",
                    request.getSemester() != null ? request.getSemester() : 6,
                    "2026-2027",
                    "OPEN",
                    request.getEmail() != null ? request.getEmail() : request.getUsername() + "@mmcoe.edu.in",
                    request.getContactNumber() != null ? request.getContactNumber() : "9876543210"
            );
            student = studentRepository.save(student);
        }

        UserAccount newUser = new UserAccount(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                role,
                student
        );

        return userRepository.save(newUser);
    }
}
