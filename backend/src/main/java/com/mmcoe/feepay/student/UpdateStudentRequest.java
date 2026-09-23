package com.mmcoe.feepay.student;

import jakarta.validation.constraints.NotBlank;

public class UpdateStudentRequest {

    private String name;

    @NotBlank(message = "Contact number is required")
    private String contactNumber;

    @NotBlank(message = "Email is required")
    private String email;

    public UpdateStudentRequest() {
    }

    public UpdateStudentRequest(String contactNumber, String email) {
        this.contactNumber = contactNumber;
        this.email = email;
    }

    public UpdateStudentRequest(String name, String contactNumber, String email) {
        this.name = name;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
