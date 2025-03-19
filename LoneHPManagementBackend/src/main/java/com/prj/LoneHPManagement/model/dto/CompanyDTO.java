package com.prj.LoneHPManagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDTO {
    private int id;
    private String name;
    private String companyType;
    private String category;
    private String businessType;
    private LocalDateTime registrationDate;
    private String licenseNumber;
    private Date licenseIssueDate;
    private Date licenseExpiryDate;
    private String phoneNumber;
    private int createdUserId;  // Changed to ID
    private int cifId;          // Changed to ID
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String state;
    private String city;
    private String township;
    private String address;     // Corrected to 'address' (lowercase)
}
