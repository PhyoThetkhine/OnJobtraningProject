package com.prj.LoneHPManagement.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CompanyDTOUpdate {
    private String name;
    private String companyType;
    private String businessType;
    private String category;
    private LocalDateTime registrationDate;
    private String licenseNumber;
    private LocalDate licenseIssueDate;
    private LocalDate licenseExpiryDate;
    private String phoneNumber;
    private int createdUserId;
    private int cifId;
    private String state;
    private String city;
    private String township;
    private String address;
}