package com.prj.LoneHPManagement.model.dto;

import lombok.Data;

@Data
public class LoanStatusChartDTO {
    private int year;
    private String loanType;
    private String status;
    private long count;
}
