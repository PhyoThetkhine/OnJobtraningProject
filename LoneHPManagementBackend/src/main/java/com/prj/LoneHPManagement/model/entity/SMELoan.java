package com.prj.LoneHPManagement.model.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@Table(name = "sme_loan")
public class SMELoan extends LoanBaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "loan_code", nullable = false, unique = true, length = 50)
    private String smeLoanCode;
    @Column(name = "purpose", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String purpose;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", nullable = false)
    private FREQUENCY frequency;

    public enum FREQUENCY{
        MONTHLY,YEARLY
    }
    @Column(name = "paid_Principal_status", nullable = false)
    @JsonIgnore
    private int paidPrincipalStatus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSmeLoanCode() {
        return smeLoanCode;
    }

    public void setSmeLoanCode(String smeLoanCode) {
        this.smeLoanCode = smeLoanCode;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public FREQUENCY getFrequency() {
        return frequency;
    }

    public void setFrequency(FREQUENCY frequency) {
        this.frequency = frequency;
    }

    public int getPaidPrincipalStatus() {
        return paidPrincipalStatus;
    }

    public void setPaidPrincipalStatus(int paidPrincipalStatus) {
        this.paidPrincipalStatus = paidPrincipalStatus;
    }

    @PrePersist
    public void prePersist() {
        if (paidPrincipalStatus == 0) paidPrincipalStatus = ConstraintEnum.PAID.getCode();

    }
    @JsonProperty("paidPrincipalStatus")
    public String getPaidPrincipalStatusDescription() {
        ConstraintEnum constraint = ConstraintEnum.fromCode(paidPrincipalStatus);
        return constraint != null ? constraint.getDescription() : "unknown";
    }
}
