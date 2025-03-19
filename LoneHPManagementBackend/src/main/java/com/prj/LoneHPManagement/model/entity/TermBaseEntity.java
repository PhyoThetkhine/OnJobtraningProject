package com.prj.LoneHPManagement.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public abstract class TermBaseEntity {
    @Column(name = "principal", precision = 32, scale = 2, nullable = false)
    private BigDecimal principal;
    @Column(name = "interest", precision = 32, scale = 2, nullable = false)
    private BigDecimal interest;
    @Column(name = "due_date", nullable = false)
    private Date dueDate;
    @Column(name = "days", nullable = false)
    private int days;
    @Column(name = "interest_of_overdue", precision = 32, scale = 2, nullable = false)
    private BigDecimal interestOfOverdue;
    @Column(name = "late_interest_days", nullable = false)
    private int interestLateDays;
    @Column(name = "late_interest_fee", precision = 32, scale = 2, nullable = false)
    private BigDecimal interestLateFee;
    @Column(name = "total_repayment_amount", precision = 32, scale = 2, nullable = false)
    private BigDecimal totalRepaymentAmount;
    @Column(name = "outstanding_amount", precision = 32, scale = 2, nullable = false)
    private BigDecimal outstandingAmount;
    @Column(name = "last_repayment_amount", precision = 32, scale = 2, nullable = false)
    private BigDecimal lastRepayment;
    @Column(name = "last_repay_date", nullable = true)
    private Date lastRepayDate;
    @Column(name = "status", nullable = true)
    @JsonIgnore
    private int status;

    public BigDecimal getPrincipal() {
        return principal;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public BigDecimal getInterestOfOverdue() {
        return interestOfOverdue;
    }

    public void setInterestOfOverdue(BigDecimal interestOfOverdue) {
        this.interestOfOverdue = interestOfOverdue;
    }

    public int getInterestLateDays() {
        return interestLateDays;
    }

    public void setInterestLateDays(int interestLateDays) {
        this.interestLateDays = interestLateDays;
    }

    public BigDecimal getInterestLateFee() {
        return interestLateFee;
    }

    public void setInterestLateFee(BigDecimal interestLateFee) {
        this.interestLateFee = interestLateFee;
    }

    public BigDecimal getTotalRepaymentAmount() {
        return totalRepaymentAmount;
    }

    public void setTotalRepaymentAmount(BigDecimal totalRepaymentAmount) {
        this.totalRepaymentAmount = totalRepaymentAmount;
    }

    public BigDecimal getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(BigDecimal outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public BigDecimal getLastRepayment() {
        return lastRepayment;
    }

    public void setLastRepayment(BigDecimal lastRepayment) {
        this.lastRepayment = lastRepayment;
    }

    public Date getLastRepayDate() {
        return lastRepayDate;
    }

    public void setLastRepayDate(Date lastRepayDate) {
        this.lastRepayDate = lastRepayDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @JsonProperty("status")
    public String getStatusDescription() {
        ConstraintEnum constraint = ConstraintEnum.fromCode(status);
        return constraint != null ? constraint.getDescription() : "unknown";
    }
    @PrePersist
    protected void prePersist() {
        if (principal == null) principal = BigDecimal.ZERO;
        if (interest == null) interest = BigDecimal.ZERO;
        if (interestOfOverdue == null) interestOfOverdue = BigDecimal.ZERO;
        if (interestLateFee == null) interestLateFee = BigDecimal.ZERO;
        if (totalRepaymentAmount == null) totalRepaymentAmount = BigDecimal.ZERO;
        if (outstandingAmount == null) outstandingAmount = BigDecimal.ZERO;
        if (lastRepayment == null) lastRepayment = BigDecimal.ZERO;
        if (days == 0) days = 0;
        if (interestLateDays == 0) interestLateDays = 0;
        if (status == 0) status = ConstraintEnum.UNDER_SCHEDULE.getCode(); // Default status
    }
    public void updateRepaymentDetails(BigDecimal amount) {
        this.lastRepayment = amount;
        this.lastRepayDate = new Date(System.currentTimeMillis());
        this.totalRepaymentAmount = this.totalRepaymentAmount.add(amount);
    }
}
