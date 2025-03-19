package com.prj.LoneHPManagement.model.dto;

import com.prj.LoneHPManagement.model.entity.BranchCurrentAccount;
import com.prj.LoneHPManagement.model.entity.CashInOutTransaction;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class CashTransactionDTO {
    private CashInOutTransaction.Type type;
    private BigDecimal amount;
    private String description;
    private BranchCurrentAccount branchCurrentAccount;
}
