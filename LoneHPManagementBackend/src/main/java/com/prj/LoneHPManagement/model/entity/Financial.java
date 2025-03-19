package com.prj.LoneHPManagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "financial")
public class Financial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "average_income", nullable =false, length = 50)
    private String averageIncome;
    @Column(name = "average_employees", nullable =false, length = 50)
    private String averageEmployees;
    @Column(name = "average_salary_paid", nullable =false, length = 50)
    private String averageSalaryPaid;
    @Column(name = "revenue_proof", nullable =false, length = 50)
    private String revenueProof;
    @Column(name = "average_expenses", nullable =false, length = 50)
    private String averageExpenses;
    @Column(name = "expected_income", nullable =false, length = 50)
    private String expectedIncome;
    @Column(name = "average_investment", nullable =false, length = 50)
    private String averageInvestment;
    @OneToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;
    @ManyToOne
    @JoinColumn(name = "created_user_id", nullable = false)
    private User createdUser;

    @PrePersist
    protected void prePersist() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void preUpdate() {
        updatedDate = LocalDateTime.now();
    }


}
