package com.prj.LoneHPManagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "loan_conllateral")
public class LoanCollateral {
    @EmbeddedId
    private LoanCollateral.LoanCollateralPK id;
    @ManyToOne
    @MapsId("loanId")
    @JoinColumn(name = "loan_id")
    private SMELoan loan;
    @ManyToOne
    @MapsId("collateralId")
    @JoinColumn(name = "collateral_id")
    private Collateral collateral;

    public LoanCollateralPK getId() {
        return id;
    }

    public void setId(LoanCollateralPK id) {
        this.id = id;
    }

    public SMELoan getLoan() {
        return loan;
    }

    public void setLoan(SMELoan loan) {
        this.loan = loan;
    }

    public Collateral getCollateral() {
        return collateral;
    }

    public void setCollateral(Collateral collateral) {
        this.collateral = collateral;
    }

    @Embeddable
    public static class LoanCollateralPK implements Serializable {
        private Integer loanId;
        private Integer collateralId;
        public LoanCollateralPK() {
        }
        public LoanCollateralPK(Integer loanId, Integer collateralId) {
            this.loanId = loanId;
            this.collateralId = collateralId;
        }

        public Integer getLoanId() {
            return loanId;
        }

        public void setLoanId(Integer loanId) {
            this.loanId = loanId;
        }

        public Integer getCollateralId() {
            return collateralId;
        }

        public void setCollateralId(Integer collateralId) {
            this.collateralId = collateralId;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LoanCollateral that = (LoanCollateral) o;
        return Objects.equals(id, that.id) && Objects.equals(loan, that.loan) && Objects.equals(collateral, that.collateral);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, loan, collateral);
    }
}
