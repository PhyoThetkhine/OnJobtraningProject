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
@Table(name = "hp_loan_history")
public class HpLoanHistory extends LoanHistoryBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @ManyToOne
    @JoinColumn(name = "hp_term_id", nullable = false)
    private HpTerm hpTerm;
    @Column(name = "principal_late_fee_paid", precision = 32, scale = 2, nullable = false)
    private BigDecimal principalLateFeePaid;
    @Column(name = "pod_paid", precision = 32, scale = 2, nullable = false)
    private BigDecimal iodPaid; // Interest on overdue
    @Column(name = "principa_late_day", nullable = false)
    private int principalLateDays;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public HpTerm getHpTerm() {
        return hpTerm;
    }

    public void setHpTerm(HpTerm hpTerm) {
        this.hpTerm = hpTerm;
    }
}
