package com.prj.LoneHPManagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountLimitUpdateDTO {

    private BigDecimal minAmount;

    private BigDecimal maxAmount;

    private int updatedUserId;


}