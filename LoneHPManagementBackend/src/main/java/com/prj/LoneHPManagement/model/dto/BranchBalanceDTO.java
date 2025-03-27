package com.prj.LoneHPManagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BranchBalanceDTO {
    private String branchName;
    private BigDecimal balance;
}
