package com.prj.LoneHPManagement.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CollateralUpdateRequest {
    private String propertyType;
    private BigDecimal estimatedValue;
    private String documentUrl;
    private Integer updatedUserId;
}