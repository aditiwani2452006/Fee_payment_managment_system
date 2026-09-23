package com.mmcoe.feepay.admin;

import com.mmcoe.feepay.auth.UserAccount;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Team 3: FR15 Designed Stub - Database Backup & Disaster Recovery History
 * Table: BackupHistory (backup_id PK, performed_by FK, backup_date, file_path, status)
 * Concepts:
 *  - OS: Child process management & cold storage persistence
 *  - DBMS: Disaster recovery, write-ahead log dumps, and point-in-time recovery
 */
@Entity
@Table(name = "backuphistory")
public class BackupHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "backup_id")
    private Long backupId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "performed_by", nullable = false)
    private UserAccount performedBy;

    @Column(name = "backup_date", nullable = false)
    private LocalDateTime backupDate = LocalDateTime.now();

    @Column(name = "file_path", nullable = false, length = 255)
    private String filePath;

    @Column(name = "status", nullable = false, length = 30)
    private String status; // COMPLETED, FAILED, RESTORED

    public BackupHistory() {
    }

    public BackupHistory(UserAccount performedBy, String filePath, String status) {
        this.performedBy = performedBy;
        this.filePath = filePath;
        this.status = status;
        this.backupDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getBackupId() {
        return backupId;
    }

    public void setBackupId(Long backupId) {
        this.backupId = backupId;
    }

    public UserAccount getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(UserAccount performedBy) {
        this.performedBy = performedBy;
    }

    public LocalDateTime getBackupDate() {
        return backupDate;
    }

    public void setBackupDate(LocalDateTime backupDate) {
        this.backupDate = backupDate;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
