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
@Table(name = "hp_loan")
public class HpLoan extends LoanBaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "hp_loan_code", nullable = false, unique = true, length = 50)
    private String hpLoanCode;
    @Column(name = "down_payment", precision = 32, scale = 2, nullable = false)
    private BigDecimal downPayment;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private DealerProduct product;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHpLoanCode() {
        return hpLoanCode;
    }

    public void setHpLoanCode(String hpLoanCode) {
        this.hpLoanCode = hpLoanCode;
    }

    public BigDecimal getDownPayment() {
        return downPayment;
    }

    public void setDownPayment(BigDecimal downPayment) {
        this.downPayment = downPayment;
    }

    public DealerProduct getProduct() {
        return product;
    }

    public void setProduct(DealerProduct product) {
        this.product = product;
    }
}
