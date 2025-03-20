package com.prj.LoneHPManagement.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cif_current_account")
public class CIFCurrentAccount extends CurrentAccountBaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "min_amount", precision = 32, scale = 2, nullable = false)
    private BigDecimal minAmount;
    @Column(name = "max_amount", precision = 32, scale = 2, nullable = false)
    private BigDecimal maxAmount;
    @Column(name = "hold_amount", precision = 32, scale = 2, nullable = true)
    private BigDecimal holdAmount;
    @Column(name = "is_freeze", nullable = false)
    @JsonIgnore
    private int isFreeze;
    @OneToOne
    @JoinColumn(name = "cif_id", nullable = false)
    private CIF cif;
    @JsonProperty("isFreeze")
    public String getIsFreezeDescription() {
        ConstraintEnum constraint = ConstraintEnum.fromCode(isFreeze);
        return constraint != null ? constraint.getDescription() : "unknown";
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public BigDecimal getHoldAmount() {
        return holdAmount;
    }

    public void setHoldAmount(BigDecimal holdAmount) {
        this.holdAmount = holdAmount;
    }

    public CIF getCif() {
        return cif;
    }

    public void setCif(CIF cif) {
        this.cif = cif;
    }
}
