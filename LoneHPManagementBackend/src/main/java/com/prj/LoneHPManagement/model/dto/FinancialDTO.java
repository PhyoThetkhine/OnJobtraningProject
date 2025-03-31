package com.prj.LoneHPManagement.model.dto;

import lombok.Data;

@Data
public class FinancialDTO {
    private double averageIncome;
    private double expectedIncome;
    private double averageExpenses;
    private double averageInvestment;
    private int averageEmployees;
    private double averageSalaryPaid;
    private String revenueProof;
    // Getters and Setters
}