package com.prj.LoneHPManagement.model.entity;

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
@Table(name = "cash_in_out_transaction")
public class CashInOutTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type Type;
    @Column(name = "description", nullable = false, unique = true, length = 200)
    private String description;
    @Column(name = "amount", precision = 32, scale = 2, nullable = false)
    private BigDecimal amount;
    @ManyToOne
    @JoinColumn(name = "branchCurrentAccount_id", nullable = false)
    private BranchCurrentAccount branchCurrentAccount;
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    public enum Type {
        Cash_In, Cash_Out
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CashInOutTransaction.Type getType() {
        return Type;
    }

    public void setType(CashInOutTransaction.Type type) {
        Type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BranchCurrentAccount getBranchCurrentAccount() {
        return branchCurrentAccount;
    }

    public void setBranchCurrentAccount(BranchCurrentAccount branchCurrentAccount) {
        this.branchCurrentAccount = branchCurrentAccount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}
