package com.mmcoe.feepay.student;

import java.time.LocalDate;

public class StudentProfileDto {
    private Long studentId;
    private String prn;
    private String name;
    private String course;
    private String department;
    private Integer semester;
    private String academicYear;
    private String category;
    private String email;
    private String contactNumber;
    private String status;
    private LocalDate admissionDate;
    private FeeAssignmentDto feeAssignment;

    public StudentProfileDto() {
    }

    public StudentProfileDto(Student student) {
        this.studentId = student.getStudentId();
        this.prn = student.getPrn();
        this.name = student.getName();
        this.course = student.getCourse();
        this.department = student.getDepartment();
        this.semester = student.getSemester();
        this.academicYear = student.getAcademicYear();
        this.category = student.getCategory();
        this.email = student.getEmail();
        this.contactNumber = student.getContactNumber();
        this.status = student.getStatus();
        this.admissionDate = student.getAdmissionDate();
    }

    public StudentProfileDto(Student student, FeeAssignment feeAssignment) {
        this(student);
        if (feeAssignment != null) {
            this.feeAssignment = new FeeAssignmentDto(feeAssignment);
        }
    }

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

    public FeeAssignmentDto getFeeAssignment() {
        return feeAssignment;
    }

    public void setFeeAssignment(FeeAssignmentDto feeAssignment) {
        this.feeAssignment = feeAssignment;
    }
}
