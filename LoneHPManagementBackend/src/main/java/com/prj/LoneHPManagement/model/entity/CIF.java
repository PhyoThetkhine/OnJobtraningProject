package com.prj.LoneHPManagement.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customer_information_file")
public class CIF extends UserCIFBaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "cif_code", nullable = false, unique = true, length = 50)
    private String cifCode;
    @Enumerated(EnumType.STRING)
    @Column(name = "cif_type", nullable = false)
    private CIFType cifType;
    @Column(name = "status", nullable = false)
    @JsonIgnore
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCifCode() {
        return cifCode;
    }

    public void setCifCode(String cifCode) {
        this.cifCode = cifCode;
    }

    public CIFType getCifType() {
        return cifType;
    }

    public void setCifType(CIFType cifType) {
        this.cifType = cifType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @JsonProperty("status")
    public String getStatusDescription() {
        ConstraintEnum constraint = ConstraintEnum.fromCode(status);
        return constraint != null ? constraint.getDescription() : "unknown";
    }

    public enum CIFType{
        DEVELOPED_COMPANY,SETUP_COMPANY,PERSONAL,Dealer
    }
    @PrePersist
    protected void prePersist() {
        if (status == 0) status = ConstraintEnum.ACTIVE.getCode(); // Default status
    }

}
