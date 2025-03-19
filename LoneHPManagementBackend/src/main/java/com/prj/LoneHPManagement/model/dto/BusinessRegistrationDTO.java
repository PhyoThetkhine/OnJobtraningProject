package com.prj.LoneHPManagement.model.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

// BusinessRegistrationDTO.java
@Data
public class BusinessRegistrationDTO {
    private String name;
    private String companyType;
    private String businessType;
    private String category;
    private Date registrationDate;
    private String licenseNumber;
    private Date licenseIssueDate;
    private Date licenseExpiryDate;
    private String phoneNumber;
    private AddressDTO address;
    private List<String> businessPhotos;
    private FinancialInfoDTO financial;
    private int createdUserId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Date getLicenseExpiryDate() {
        return licenseExpiryDate;
    }

    public void setLicenseExpiryDate(Date licenseExpiryDate) {
        this.licenseExpiryDate = licenseExpiryDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getLicenseIssueDate() {
        return licenseIssueDate;
    }

    public void setLicenseIssueDate(Date licenseIssueDate) {
        this.licenseIssueDate = licenseIssueDate;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    public List<String> getBusinessPhotos() {
        return businessPhotos;
    }

    public void setBusinessPhotos(List<String> businessPhotos) {
        this.businessPhotos = businessPhotos;
    }

    public FinancialInfoDTO getFinancial() {
        return financial;
    }

    public void setFinancial(FinancialInfoDTO financial) {
        this.financial = financial;
    }

    public int getCreatedUserId() {
        return createdUserId;
    }

    public void setCreatedUserId(int createdUserId) {
        this.createdUserId = createdUserId;
    }
}