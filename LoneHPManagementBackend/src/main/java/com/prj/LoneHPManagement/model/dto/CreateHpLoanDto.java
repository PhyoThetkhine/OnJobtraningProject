package com.prj.LoneHPManagement.model.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateHpLoanDto {
    private int cifId;
    private int productId;
    private BigDecimal loanAmount;
    private BigDecimal downPayment;
    private int duration;
    private int createdUserId;
}