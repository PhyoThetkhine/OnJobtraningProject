package com.prj.LoneHPManagement.model.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class CollateralRequest {
    private String propertyType;
    private BigDecimal estimatedValue;
    private String documentUrl;
    private Integer cifId;
    private Integer createdUserId;
}
