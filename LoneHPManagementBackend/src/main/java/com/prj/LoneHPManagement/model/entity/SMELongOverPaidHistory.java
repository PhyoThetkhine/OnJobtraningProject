package com.prj.LoneHPManagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sme_long_overpaid_history")
public class SMELongOverPaidHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private SMELoan loan;
    @Column(name = "paid_amount", precision = 32, scale = 2, nullable = false)
    private BigDecimal paidAmount;
    @Column(name = "paid_date", nullable = false)
    private LocalDateTime paidDate;
    @Column(name = "outstanding_amount", precision = 32, scale = 2, nullable = false)
    private BigDecimal OutstandingAmount;
    @Column(name = "latefee_amount", precision = 32, scale = 2, nullable = false)
    private BigDecimal lateFeeAmount;
    @Column(name = "late_days", nullable = false)
    private int lateDays;
    @PrePersist
    protected void setDefaults() {
        // Default paidDate to current time if not set
        if (this.paidDate == null) {
            this.paidDate = LocalDateTime.now();
        }

        // Initialize BigDecimal fields to ZERO if null
        if (this.paidAmount == null) {
            this.paidAmount = BigDecimal.ZERO;
        }
        if (this.OutstandingAmount == null) {
            this.OutstandingAmount = BigDecimal.ZERO;
        }
        if (this.lateFeeAmount == null) {
            this.lateFeeAmount = BigDecimal.ZERO;
        }

        // Ensure lateDays is non-negative
        this.lateDays = Math.max(this.lateDays, 0);
    }
}
