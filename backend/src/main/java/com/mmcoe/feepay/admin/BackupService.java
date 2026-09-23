package com.mmcoe.feepay.admin;

import com.mmcoe.feepay.auth.UserAccount;
import com.mmcoe.feepay.auth.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Team 3: FR15 Designed Stub - Database Backup & Restore
 * 
 * Concepts:
 *  - Operating Systems (OS): Child process spawning (pg_dump / pg_restore CLI invocation)
 *  - DBMS: Disaster recovery, cold dumps, write-ahead logging
 */
@Service
public class BackupService {

    private static final Logger log = LoggerFactory.getLogger(BackupService.class);

    private final BackupHistoryRepository backupHistoryRepository;
    private final UserRepository userRepository;

    public BackupService(BackupHistoryRepository backupHistoryRepository, UserRepository userRepository) {
        this.backupHistoryRepository = backupHistoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BackupHistory triggerBackup(Long adminUserId) {
        UserAccount admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String simulatedPath = "./backups/feepay_db_backup_" + timestamp + ".sql";

        log.info("[DESIGNED STUB] Admin {} initiated database backup", admin.getUsername());
        log.info("[OS CLI STUB] Executing: pg_dump -U postgres -d feepay_db -F c -f {}", simulatedPath);
        log.info("[DBMS STUB] Recorded cold backup file in BackupHistory table");

        BackupHistory history = new BackupHistory(admin, simulatedPath, "COMPLETED");
        return backupHistoryRepository.save(history);
    }

    @Transactional(readOnly = true)
    public List<BackupHistory> getBackupHistory() {
        return backupHistoryRepository.findTop20ByOrderByBackupDateDesc();
    }
}
