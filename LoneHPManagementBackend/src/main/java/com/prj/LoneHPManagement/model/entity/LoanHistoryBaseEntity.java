package com.prj.LoneHPManagement.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
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
public abstract class LoanHistoryBaseEntity {
    @Column(name = "paid_amount", precision = 32, scale = 2, nullable = false)
    private BigDecimal paidAmount;
    @Column(name = "paid_date", nullable = false)
    private LocalDateTime paidDate;
    @Column(name = "outstanding_amount", precision = 32, scale = 2, nullable = false)
    private BigDecimal outstanding;
    @Column(name = "principal_paid", precision = 32, scale = 2, nullable = false)
    private BigDecimal principalPaid;
    @Column(name = "interest_paid", precision = 32, scale = 2, nullable = false)
    private BigDecimal interestPaid;
    @Column(name = "iod_paid", precision = 32, scale = 2, nullable = false)
    private BigDecimal iodPaid; // Interest on overdue
    @Column(name = "interest_late_fee_paid", precision = 32, scale = 2, nullable = false)
    private BigDecimal interestLateFeePaid;
    @Column(name = "principal_late_fee_paid", precision = 32, scale = 2, nullable = false)
    private BigDecimal principalLateFeePaid;
    @Column(name = "total_paid", precision = 32, scale = 2, nullable = false)
    private BigDecimal totalPaid; // principal + interest + late fee + IOD
    @Column(name = "term_status", nullable = false)
    private int termStatus;

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public LocalDateTime getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDateTime paidDate) {
        this.paidDate = paidDate;
    }

    public BigDecimal getOutstanding() {
        return outstanding;
    }

    public void setOutstanding(BigDecimal outstanding) {
        this.outstanding = outstanding;
    }

    public BigDecimal getPrincipalPaid() {
        return principalPaid;
    }

    public void setPrincipalPaid(BigDecimal principalPaid) {
        this.principalPaid = principalPaid;
    }

    public BigDecimal getInterestPaid() {
        return interestPaid;
    }

    public void setInterestPaid(BigDecimal interestPaid) {
        this.interestPaid = interestPaid;
    }

    public BigDecimal getIodPaid() {
        return iodPaid;
    }

    public void setIodPaid(BigDecimal iodPaid) {
        this.iodPaid = iodPaid;
    }

    public BigDecimal getInterestLateFeePaid() {
        return interestLateFeePaid;
    }

    public void setInterestLateFeePaid(BigDecimal interestLateFeePaid) {
        this.interestLateFeePaid = interestLateFeePaid;
    }

    public BigDecimal getPrincipalLateFeePaid() {
        return principalLateFeePaid;
    }

    public void setPrincipalLateFeePaid(BigDecimal principalLateFeePaid) {
        this.principalLateFeePaid = principalLateFeePaid;
    }

    public BigDecimal getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(BigDecimal totalPaid) {
        this.totalPaid = totalPaid;
    }

    public int getTermStatus() {
        return termStatus;
    }

    public void setTermStatus(int termStatus) {
        this.termStatus = termStatus;
    }

    @PrePersist
    protected void prePersist() {
        if (paidAmount == null) paidAmount = BigDecimal.ZERO;
        if (paidDate == null) paidDate = LocalDateTime.now();
        if (outstanding == null) outstanding = BigDecimal.ZERO;
        if (principalPaid == null) principalPaid = BigDecimal.ZERO;
        if (interestPaid == null) interestPaid = BigDecimal.ZERO;
        if (iodPaid == null) iodPaid = BigDecimal.ZERO;
        if (interestLateFeePaid == null) interestLateFeePaid = BigDecimal.ZERO;
        if (principalLateFeePaid == null) principalLateFeePaid = BigDecimal.ZERO;
        if (totalPaid == null) totalPaid = BigDecimal.ZERO;
    }
}
