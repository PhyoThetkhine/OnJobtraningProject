package com.prj.LoneHPManagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hp_term")
public class HpTerm extends TermBaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "principal_of_overdue", precision = 32, scale = 2, nullable = false)
    private BigDecimal principalOfOverdue;
    @Column(name = "late_principal_fee", precision = 32, scale = 2, nullable = false)
    private BigDecimal principalLateFee;
    @Column(name = "late_principal_days", nullable = false)
    private int latePrincipalDays;
    @ManyToOne
    @JoinColumn(name = "hp_loan_id", nullable = false)
    private HpLoan hpLoan;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getPrincipalOfOverdue() {
        return principalOfOverdue;
    }

    public void setPrincipalOfOverdue(BigDecimal principalOfOverdue) {
        this.principalOfOverdue = principalOfOverdue;
    }

    public int getLatePrincipalDays() {
        return latePrincipalDays;
    }

    public void setLatePrincipalDays(int latePrincipalDays) {
        this.latePrincipalDays = latePrincipalDays;
    }

    public BigDecimal getPrincipalLateFee() {
        return principalLateFee;
    }

    public void setPrincipalLateFee(BigDecimal principalLateFee) {
        this.principalLateFee = principalLateFee;
    }

    public HpLoan getHpLoan() {
        return hpLoan;
    }

    public void setHpLoan(HpLoan hpLoan) {
        this.hpLoan = hpLoan;
    }
}
