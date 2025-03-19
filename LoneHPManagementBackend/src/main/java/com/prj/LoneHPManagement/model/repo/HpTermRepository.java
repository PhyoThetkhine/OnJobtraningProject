package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.CIF;
import com.prj.LoneHPManagement.model.entity.HpTerm;
import com.prj.LoneHPManagement.model.entity.SMETerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface HpTermRepository extends JpaRepository<HpTerm,Integer> {
    @Query("SELECT t FROM HpTerm t WHERE t.hpLoan.cif = :cif")
    List<HpTerm> findByCif(@Param("cif") CIF cif);
    List<HpTerm> findByHpLoan_Id(int loanId);
    List<HpTerm> findByHpLoan_CifAndStatus(CIF cif, int status);

    @Procedure(name = "HPCalculateLateFee")
    void calculateLateFee(
            @Param("p_due_date") Date dueDate,
            @Param("p_grace_period") int gracePeriod,
            @Param("p_late_fee_rate") BigDecimal lateFeeRate,
            @Param("p_principal") BigDecimal principal,
            @Param("p_last_repay_date") Date lastRepayDate,
            @Param("p_late_days") Integer lateDays,
            @Param("p_late_fee") BigDecimal lateFee
    );

    @Procedure(procedureName = "HPCalculateLateFee")
    void calculateHPLateFee(
            @Param("p_due_date") Date dueDate,
            @Param("p_grace_period") Integer gracePeriod,
            @Param("p_late_fee_rate") BigDecimal lateFeeRate,
            @Param("p_principal_of_overdue") BigDecimal principalOfOverdue,
            @Param("p_interest_of_overdue") BigDecimal interestOfOverdue,
            @Param("p_last_repay_date") Date lastRepayDate,
            @Param("p_principal_late_days") Integer principalLateDays,
            @Param("p_principal_late_fee") BigDecimal principalLateFee,
            @Param("p_interest_late_days") Integer interestLateDays,
            @Param("p_interest_late_fee") BigDecimal interestLateFee
    );

    @Query("SELECT t FROM HpTerm t WHERE t.hpLoan.id = :loanId AND t.status = :status")
    List<HpTerm> findByLoanIdAndStatus(@Param("loanId") int loanId, @Param("status") int status);

    List<HpTerm> findByStatus(int status);

    @Query("SELECT t FROM HpTerm t JOIN t.hpLoan l WHERE l.cif.id = :cifId AND t.status = :status")
    List<HpTerm> findByCifIdAndStatus(@Param("cifId") int cifId, @Param("status") int status);


}
