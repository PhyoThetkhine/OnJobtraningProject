package com.prj.LoneHPManagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryResponse {
    private LocalDateTime date;
    private BigDecimal amount;
    private String type;
    private String counterparty;
    private String paymentMethod;
}
