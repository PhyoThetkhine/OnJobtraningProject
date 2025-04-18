package com.prj.LoneHPManagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CompanyDTO {
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
    private FinancialDTO financial;
    private List<String> businessPhotoUrls;
    // Getters and Setters
}