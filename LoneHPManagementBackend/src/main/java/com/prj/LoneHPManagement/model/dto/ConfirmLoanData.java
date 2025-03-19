package com.prj.LoneHPManagement.model.dto;

import java.math.BigDecimal;

public class ConfirmLoanData {

    private BigDecimal disbursementAmount; // Amount to be disbursed
    private int documentFeeRate;           // Document fee rate (percentage)
    private int serviceChargeRate;         // Service charge rate (percentage)
    private int gracePeriod;               // Grace period in days
    private int interestRate;               // Interest rate (percentage per year)
    private int lateFeeRate;               // Late fee rate (percentage per day)
    private int defaultRate;               // Default rate (percentage per day)
    private int longTermOverdueRate;      // Long-term overdue rate (percentage per day)
    private int confirmUserId;
    private String paidPrincipalStatus;

    public String getPaidPrincipalStatus() {
        return paidPrincipalStatus;
    }

    public void setPaidPrincipalStatus(String paidPrincipalStatus) {
        this.paidPrincipalStatus = paidPrincipalStatus;
    }

    public int getConfirmUserId() {
        return confirmUserId;
    }

    public void setConfirmUserId(int confirmUserId) {
        this.confirmUserId = confirmUserId;
    }

    // Default constructor
    public ConfirmLoanData() {}

    // Getters and Setters
    public BigDecimal getDisbursementAmount() {
        return disbursementAmount;
    }

    public void setDisbursementAmount(BigDecimal disbursementAmount) {
        this.disbursementAmount = disbursementAmount;
    }

    public int getDocumentFeeRate() {
        return documentFeeRate;
    }

    public void setDocumentFeeRate(int documentFeeRate) {
        this.documentFeeRate = documentFeeRate;
    }

    public int getServiceChargeRate() {
        return serviceChargeRate;
    }

    public void setServiceChargeRate(int serviceChargeRate) {
        this.serviceChargeRate = serviceChargeRate;
    }

    public int getGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(int gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public int getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(int interestRate) {
        this.interestRate = interestRate;
    }

    public int getLateFeeRate() {
        return lateFeeRate;
    }

    public void setLateFeeRate(int lateFeeRate) {
        this.lateFeeRate = lateFeeRate;
    }

    public int getDefaultRate() {
        return defaultRate;
    }

    public void setDefaultRate(int defaultRate) {
        this.defaultRate = defaultRate;
    }

    public int getLongTermOverdueRate() {
        return longTermOverdueRate;
    }

    public void setLongTermOverdueRate(int longTermOverdueRate) {
        this.longTermOverdueRate = longTermOverdueRate;
    }
}