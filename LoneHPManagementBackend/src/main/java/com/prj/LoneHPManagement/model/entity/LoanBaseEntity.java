package com.prj.LoneHPManagement.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public abstract class LoanBaseEntity {
    @Column(name = "loan_amount", precision = 32, scale = 2, nullable = false)
    private BigDecimal loanAmount;
    @Column(name = "disbursement_amount", precision = 32, scale = 2, nullable = false)
    private BigDecimal DisbursementAmount;

    @Column(name = "interest_rate", nullable = false)
    private int interestRate;
    @Column(name = "grace_period", nullable = false)
    private int gracePeriod;
    @Column(name = "application_date", nullable = false)
    private LocalDateTime applicationDate;
    @Column(name = "start_date", nullable = true)
    private Date startDate;
    @Column(name = "end_date", nullable = true)
    private Date endDate;
    @Column(name = "late_fee_rate", nullable = false)
    private int lateFeeRate;
    @Column(name = "defaulted_rate",nullable = false)
    private int defaultedRate;
    @Column(name = "long_term_overdue_rate",nullable = false)
    private int longTermOverdueRate;
    @Column(name = "duration", nullable = false)
    private int duration;
    @Column(name = "status", nullable = false)
    @JsonIgnore
    private int status;
    @Column(name = "document_fee_rate", nullable = false)
    private int documentFeeRate;
    @Column(name = "document_fee", precision = 32, scale = 2, nullable = false)
    private BigDecimal documentFee;
    @Column(name = "service_fee_rate", nullable = false)
    private int serviceChargeFeeRate;
    @Column(name = "service_fee", precision = 32, scale = 2, nullable = false)
    private BigDecimal serviceCharge;
    @Column(name="updated_date", nullable = false)
    private LocalDateTime updatedDate;
    @Column(name="confirm_date", nullable = true)
    private LocalDateTime confirmDate;
    @ManyToOne
    @JoinColumn(name = "cif_id", nullable = false)
    private CIF cif;
    @ManyToOne
    @JoinColumn(name = "created_user_id", nullable = false)
    private User createdUser;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "confirm_user_id", nullable = true)
    private User confirmUser;

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public BigDecimal getDisbursementAmount() {
        return DisbursementAmount;
    }

    public void setDisbursementAmount(BigDecimal disbursementAmount) {
        DisbursementAmount = disbursementAmount;
    }

    public int getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(int interestRate) {
        this.interestRate = interestRate;
    }

    public int getGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(int gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getDefaultedRate() {
        return defaultedRate;
    }

    public void setDefaultedRate(int defaultedRate) {
        this.defaultedRate = defaultedRate;
    }

    public int getLateFeeRate() {
        return lateFeeRate;
    }

    public void setLateFeeRate(int lateFeeRate) {
        this.lateFeeRate = lateFeeRate;
    }

    public int getLongTermOverdueRate() {
        return longTermOverdueRate;
    }

    public void setLongTermOverdueRate(int longTermOverdueRate) {
        this.longTermOverdueRate = longTermOverdueRate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDocumentFeeRate() {
        return documentFeeRate;
    }

    public void setDocumentFeeRate(int documentFeeRate) {
        this.documentFeeRate = documentFeeRate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public BigDecimal getDocumentFee() {
        return documentFee;
    }

    public void setDocumentFee(BigDecimal documentFee) {
        this.documentFee = documentFee;
    }

    public int getServiceChargeFeeRate() {
        return serviceChargeFeeRate;
    }

    public void setServiceChargeFeeRate(int serviceChargeFeeRate) {
        this.serviceChargeFeeRate = serviceChargeFeeRate;
    }

    public BigDecimal getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(BigDecimal serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public LocalDateTime getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(LocalDateTime confirmDate) {
        this.confirmDate = confirmDate;
    }

    public CIF getCif() {
        return cif;
    }

    public void setCif(CIF cif) {
        this.cif = cif;
    }

    public User getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(User createdUser) {
        this.createdUser = createdUser;
    }

    public User getConfirmUser() {
        return confirmUser;
    }

    public void setConfirmUser(User confirmUser) {
        this.confirmUser = confirmUser;
    }

    @PrePersist
    public void prePersist() {
        if (DisbursementAmount == null) DisbursementAmount = BigDecimal.ZERO;
        if(loanAmount == null) loanAmount = BigDecimal.ZERO;
        if (documentFee == null) documentFee = BigDecimal.ZERO;
        if (serviceCharge == null) serviceCharge = BigDecimal.ZERO;
        if (status == 0) status = ConstraintEnum.UNDER_REVIEW.getCode();

        applicationDate = LocalDateTime.now(); // Set application date when created
        updatedDate = LocalDateTime.now(); // Ensure updatedDate is set initially
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now(); // Auto-update the timestamp before saving changes
    }
    @JsonProperty("status")
    public String getStatusDescription() {
        ConstraintEnum constraint = ConstraintEnum.fromCode(status);
        return constraint != null ? constraint.getDescription() : "unknown";
    }
}
