package com.mmcoe.feepay.admin;

import com.mmcoe.feepay.auth.UserAccount;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Team 3: FR15 Admin Dashboard & Audit Logs
 * Table: AuditLog (audit_id PK, user_id FK, action, description, ip_address, timestamp)
 * Concepts:
 *  - SE: Non-repudiation and regulatory compliance tracking
 *  - DBMS: Immutable append-only audit trail
 */
@Entity
@Table(name = "auditlog")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long auditId;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "user_id", nullable = true)
    private UserAccount user;

    @Column(name = "action", nullable = false, length = 100)
    private String action;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "ip_address", length = 45)
    private String ipAddress = "127.0.0.1";

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public AuditLog() {
    }

    public AuditLog(UserAccount user, String action) {
        this.user = user;
        this.action = action;
        this.description = action;
        this.ipAddress = "127.0.0.1";
        this.timestamp = LocalDateTime.now();
    }

    public AuditLog(UserAccount user, String action, String description, String ipAddress) {
        this.user = user;
        this.action = action;
        this.description = description;
        this.ipAddress = ipAddress != null ? ipAddress : "127.0.0.1";
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getAuditId() {
        return auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }

    public UserAccount getUser() {
        return user;
    }

    public void setUser(UserAccount user) {
        this.user = user;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
