package com.prj.LoneHPManagement.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardDTO {
    private long totalCif;
    private long totalUsers;
    private long totalLoans;
    private long totalBranchAccounts;
    private long activeLoans;
    private BigDecimal totalLoanAmount;
}
