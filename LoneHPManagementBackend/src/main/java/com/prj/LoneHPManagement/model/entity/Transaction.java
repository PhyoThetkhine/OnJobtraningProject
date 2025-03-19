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
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "from_account_id", nullable = false)
    private int fromAccountId;
    @Column(name = "to_account_id", nullable = false)
    private int toAccountId;
    @Enumerated(EnumType.STRING)
    @Column(name = "from_account_type", nullable = false)
    private AccountType fromAccountType;
    @Enumerated(EnumType.STRING)
    @Column(name = "to_account_type", nullable = false)
    private AccountType toAccountType;
    @Column(name = "amount", precision = 32, scale = 2, nullable = false)
    private BigDecimal amount;
    @ManyToOne
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethod paymentMethod;
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(int fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public int getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(int toAccountId) {
        this.toAccountId = toAccountId;
    }

    public AccountType getFromAccountType() {
        return fromAccountType;
    }

    public void setFromAccountType(AccountType fromAccountType) {
        this.fromAccountType = fromAccountType;
    }

    public AccountType getToAccountType() {
        return toAccountType;
    }

    public void setToAccountType(AccountType toAccountType) {
        this.toAccountType = toAccountType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public enum AccountType {
        USER,
        CIF,
        BRANCH
    }
    @PrePersist
    protected void prePersist() {
        transactionDate = LocalDateTime.now();
    }



}
