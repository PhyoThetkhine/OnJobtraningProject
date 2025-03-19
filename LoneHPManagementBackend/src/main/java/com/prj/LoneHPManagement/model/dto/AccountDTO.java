package com.prj.LoneHPManagement.model.dto;

import lombok.Data;

@Data
public class AccountDTO {
    private Double minAmount;
    private Double maxAmount;

    public Double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Double minAmount) {
        this.minAmount = minAmount;
    }

    public Double getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Double maxAmount) {
        this.maxAmount = maxAmount;
    }
}
