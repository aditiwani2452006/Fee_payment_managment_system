package com.mmcoe.feepay.auth;

import com.mmcoe.feepay.student.Student;
import jakarta.persistence.*;

/**
 * Team 1: FR1 Account Provisioning & FR2 Login Authentication
 * Table: User_Account (SRS: User table - user_id PK, username, password_hash, role_id FK, student_id FK)
 * Concepts: DBMS (Foreign key constraints) & CN/Security (BCrypt hashed credentials)
 */
@Entity
@Table(name = "user_account")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "student_id", nullable = true)
    private Student student;

    public UserAccount() {
    }

    public UserAccount(String username, String passwordHash, Role role, Student student) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.student = student;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
