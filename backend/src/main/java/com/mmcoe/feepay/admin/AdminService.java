package com.mmcoe.feepay.admin;

import com.mmcoe.feepay.auth.UserAccount;
import com.mmcoe.feepay.auth.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Team 3: FR15 Admin Dashboard, Audit Logs & Security Monitoring
 * Concepts:
 *  - SE: Non-repudiation and activity telemetry
 *  - CN: IP logging and access auditing
 */
@Service
public class AdminService {

    private final AuditLogRepository auditLogRepository;
    private final SecurityLogRepository securityLogRepository;
    private final UserRepository userRepository;

    public AdminService(
            AuditLogRepository auditLogRepository,
            SecurityLogRepository securityLogRepository,
            UserRepository userRepository
    ) {
        this.auditLogRepository = auditLogRepository;
        this.securityLogRepository = securityLogRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getRecentAuditLogs() {
        return auditLogRepository.findTop50ByOrderByTimestampDesc();
    }

    @Transactional(readOnly = true)
    public List<SecurityLog> getRecentSecurityLogs() {
        return securityLogRepository.findTop50ByOrderByTimestampDesc();
    }

    @Transactional(readOnly = true)
    public List<UserAccount> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void recordAuditLog(Long userId, String action) {
        if (userId != null) {
            userRepository.findById(userId).ifPresent(user -> {
                auditLogRepository.save(new AuditLog(user, action));
            });
        }
    }

    @Transactional
    public void recordSecurityLog(Long userId, String eventType, String ipAddress) {
        UserAccount user = (userId != null) ? userRepository.findById(userId).orElse(null) : null;
        securityLogRepository.save(new SecurityLog(user, eventType, ipAddress));
    }
}
