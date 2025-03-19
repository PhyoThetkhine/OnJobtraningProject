package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.CIF;
import com.prj.LoneHPManagement.model.entity.SMETerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface SMETermRepository extends JpaRepository<SMETerm,Integer> {
    List<SMETerm> findBySmeLoan_IdAndDueDateAfter(Integer loanId, Date dueDate);
    @Query("SELECT t FROM SMETerm t WHERE t.smeLoan.cif = :cif")
    List<SMETerm> findByCif(@Param("cif") CIF cif);
    List<SMETerm> findBySmeLoan_CifAndStatus(CIF cif, int status);
    List<SMETerm> findBySmeLoan_Id(Integer loanId);


    @Procedure(name = "SMECalculateLateFee")
    void calculateLateFee(
            @Param("p_due_date") Date dueDate,
            @Param("p_grace_period") int gracePeriod,
            @Param("p_late_fee_rate") BigDecimal lateFeeRate,
            @Param("p_principal") BigDecimal principal,
            @Param("p_last_repay_date") Date lastRepayDate,
            @Param("p_late_days") Integer lateDays,
            @Param("p_late_fee") BigDecimal lateFee
    );

    @Procedure(name = "SmeAdjustPrincipalAndRecalculateInterest")
    void smeAdjustPrincipalAndRecalculateInterest(@Param("termId") int termId,
                                                  @Param("remainingPrincipal") BigDecimal remainingPrincipal);
    @Query("SELECT t FROM SMETerm t WHERE t.smeLoan.id = :loanId AND t.status = :status")
    List<SMETerm> findByLoanIdAndStatus(@Param("loanId") int loanId, @Param("status") int status);

    List<SMETerm> findByStatus(int status);

    @Query("SELECT t FROM SMETerm t JOIN t.smeLoan l WHERE l.cif.id = :cifId AND t.status = :status")
    List<SMETerm> findByCifIdAndStatus(@Param("cifId") int cifId, @Param("status") int status);

}
