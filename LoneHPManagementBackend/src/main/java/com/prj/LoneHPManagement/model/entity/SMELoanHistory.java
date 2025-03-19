package com.prj.LoneHPManagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sme_loan_history")
public class SMELoanHistory extends LoanHistoryBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @ManyToOne
    @JoinColumn(name = "sme_term_id", nullable = false)
    private SMETerm smeTerm;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SMETerm getSmeTerm() {
        return smeTerm;
    }

    public void setSmeTerm(SMETerm smeTerm) {
        this.smeTerm = smeTerm;
    }
}
