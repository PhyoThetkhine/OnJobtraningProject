package com.prj.LoneHPManagement.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public abstract class CurrentAccountBaseEntity {
    @Column(name = "acc_code", nullable = false, unique = true, length = 50)
    private String accCode; //CAC00100200123
    @Column(name = "balance", precision = 32, scale = 2, nullable = false)
    private BigDecimal balance;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    public String getAccCode() {
        return accCode;
    }

    public void setAccCode(String accCode) {
        this.accCode = accCode;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    @PrePersist
    protected void prePersist() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }

    }

    @PreUpdate
    protected void preUpdate() {
        updatedDate = LocalDateTime.now();
    }


}
