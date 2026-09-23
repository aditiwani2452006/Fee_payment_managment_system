package com.mmcoe.feepay.student;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Team 1: FR1 Student Account Provisioning & FR4 Manage Student Profile
 * Table: Student (student_id PK, prn UNIQUE, name, course, department, semester, academic_year, category, email, contact_number, status, admission_date)
 * Concepts: OOP (Encapsulation of student state) & DBMS (Core entity table with UNIQUE PRN)
 */
@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "prn", nullable = false, unique = true, length = 30)
    private String prn;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "course", nullable = false, length = 50)
    private String course;

    @Column(name = "department", nullable = false, length = 50)
    private String department = "Information Technology";

    @Column(name = "semester", nullable = false)
    private Integer semester;

    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear = "2026-2027";

    @Column(name = "category", nullable = false, length = 50)
    private String category; // OPEN, OBC/EBC/EWS/SEBC Male, SC/ST, VJNT/SBC/TFWS, J & K Quota, OBC/EBC/EWS/SEBC Female

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "contact_number", nullable = false, length = 20)
    private String contactNumber;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "Active";

    @Column(name = "admission_date")
    private LocalDate admissionDate = LocalDate.of(2023, 8, 1);

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Student() {
    }

    public Student(String prn, String name, String course, String department, Integer semester, String academicYear, String category, String email, String contactNumber) {
        this.prn = prn;
        this.name = name;
        this.course = course;
        this.department = department;
        this.semester = semester;
        this.academicYear = academicYear;
        this.category = category;
        this.email = email;
        this.contactNumber = contactNumber;
        this.status = "Active";
        this.admissionDate = LocalDate.of(2023, 8, 1);
        this.createdAt = LocalDateTime.now();
    }

    // Convenience constructor for backward compatibility
    public Student(String name, String course, Integer semester, String email, String contactNumber) {
        this("B25IT" + (int)(Math.random() * 9000 + 1000), name, course, "Information Technology", semester, "2026-2027", "OPEN", email, contactNumber);
    }

    // Getters and Setters
    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getPrn() {
        return prn;
    }

    public void setPrn(String prn) {
        this.prn = prn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(LocalDate admissionDate) {
        this.admissionDate = admissionDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
