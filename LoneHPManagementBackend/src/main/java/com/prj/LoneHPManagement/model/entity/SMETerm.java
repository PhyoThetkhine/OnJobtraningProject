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
@Table(name = "sme_term")
public class SMETerm extends TermBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private SMELoan smeLoan;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SMELoan getSmeLoan() {
        return smeLoan;
    }

    public void setSmeLoan(SMELoan smeLoan) {
        this.smeLoan = smeLoan;
    }
}
