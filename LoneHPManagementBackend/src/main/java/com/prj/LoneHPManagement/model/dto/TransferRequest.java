package com.prj.LoneHPManagement.model.dto;

import com.prj.LoneHPManagement.model.entity.Transaction;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Data
public class TransferRequest {
    private int fromAccountId;
    private int toAccountId;
    private Transaction.AccountType fromAccountType;
    private Transaction.AccountType toAccountType;
    private BigDecimal amount;
    private int paymentMethodId;
}