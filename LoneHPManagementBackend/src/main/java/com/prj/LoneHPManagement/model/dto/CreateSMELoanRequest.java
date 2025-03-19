package com.prj.LoneHPManagement.model.dto;

import com.prj.LoneHPManagement.model.entity.SMELoan;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateSMELoanRequest {

    private Integer cifId;

    private BigDecimal loanAmount;

    private SMELoan.FREQUENCY frequency;

    private Integer duration;


    private List<Integer> collateralIds;


    private String purpose;

    private Integer createdUserId;
}