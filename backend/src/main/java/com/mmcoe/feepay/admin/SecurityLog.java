package com.mmcoe.feepay.admin;

import com.mmcoe.feepay.auth.UserAccount;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Team 3: Security Telemetry & Access Logging
 * Table: SecurityLog (log_id PK, user_id FK nullable, event_type, ip_address, timestamp)
 * Concepts:
 *  - CN: Client IP resolution and network-level security tracking
 *  - OS: Audit of unauthorized access attempts and security boundary hits
 */
@Entity
@Table(name = "securitylog")
public class SecurityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "user_id", nullable = true)
    private UserAccount user;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType; // LOGIN_SUCCESS, LOGIN_FAILURE, ACCESS_DENIED, CONCURRENCY_VIOLATION

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public SecurityLog() {
    }

    public SecurityLog(UserAccount user, String eventType, String ipAddress) {
        this.user = user;
        this.eventType = eventType;
        this.ipAddress = ipAddress;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public UserAccount getUser() {
        return user;
    }

    public void setUser(UserAccount user) {
        this.user = user;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
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
