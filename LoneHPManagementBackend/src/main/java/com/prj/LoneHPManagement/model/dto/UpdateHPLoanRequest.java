package com.prj.LoneHPManagement.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateHPLoanRequest {
    private BigDecimal loanAmount;
    private BigDecimal downPayment;
    private Integer duration;
    private Integer productId;
}
