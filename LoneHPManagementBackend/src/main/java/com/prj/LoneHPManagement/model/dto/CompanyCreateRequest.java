package com.prj.LoneHPManagement.model.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Date;
import java.time.LocalDateTime;

import java.util.List;

@Data
public class CompanyCreateRequest {
    private String name;
    private String companyType;
    private String businessType;
    private String category;


    private LocalDateTime registrationDate;

    private String licenseNumber;


    private Date licenseIssueDate;


    private Date licenseExpiryDate;

    private String phoneNumber;
    private AddressDTO address;
    private List<String> businessPhotos;
    private int cifId;
    private int createdUserId;

    // Getters and Setters
}