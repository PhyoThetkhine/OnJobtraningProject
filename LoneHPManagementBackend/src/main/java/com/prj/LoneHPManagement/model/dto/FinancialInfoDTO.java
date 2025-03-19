package com.prj.LoneHPManagement.model.dto;

import lombok.Data;

// FinancialInfoDTO.java
@Data
public class FinancialInfoDTO {
    private Double averageIncome;
    private Double expectedIncome;
    private Double averageExpenses;
    private Double averageInvestment;
    private Integer averageEmployees;
    private Double averageSalaryPaid;
    private String revenueProof;
}