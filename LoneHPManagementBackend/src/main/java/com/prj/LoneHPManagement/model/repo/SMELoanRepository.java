package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.CIF;
import com.prj.LoneHPManagement.model.entity.HpLoan;
import com.prj.LoneHPManagement.model.entity.SMELoan;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SMELoanRepository extends JpaRepository<SMELoan,Integer> {
    @Query("SELECT l FROM SMELoan l WHERE l.cif.id = :cifId AND l.status = :status")
    List<SMELoan> findByCifIdAndStatus(@Param("cifId") int cifId, @Param("status") int status);
    @Query("SELECT s FROM SMELoan s ORDER BY s.applicationDate DESC")
    Page<SMELoan> findAllSortedByApplicationDate(Pageable pageable);
    SMELoan findLoanBySmeLoanCode(String smeLoanCode);
    @Modifying
    @Transactional
    @Query("UPDATE SMELoan s SET s.paidPrincipalStatus = ?1 WHERE s.id = ?2")
    void updatePaidPrincipalStatus(int status, int loanId);
    List<SMELoan> findByCifId(Integer cifId);
    @Query("SELECT MAX(s.smeLoanCode) FROM SMELoan s WHERE s.cif = :cif")
    String findMaxSMELoanCodeByCif(@Param("cif") CIF cif);
        Page<SMELoan> findByCifId(Integer cifId, Pageable pageable);

//    @Query("SELECT l FROM SMELoan l WHERE l.applicationDate >= CURRENT_DATE - 30")
//    List<SMELoan> findRecentLoans();

    @Query("SELECT l FROM SMELoan l WHERE l.endDate < CURRENT_DATE")
    List<SMELoan> findExpiredLoans();

    @Query("SELECT COUNT(l) FROM SMELoan l")
    long countTotalLoans();

    @Query("SELECT l FROM SMELoan l WHERE FUNCTION('YEAR', l.startDate) = :year")
    List<SMELoan> findByStartYear(@Param("year") int year);

//    @Query("SELECT l FROM SMELoan l WHERE l.purpose LIKE %:keyword%")
//    List<SMELoan> findByPurposeLike(@Param("keyword") String keyword);

    List<SMELoan> findByInterestRateGreaterThan(Double interestRate);

    @Query("SELECT l FROM SMELoan l WHERE l.duration = :duration")
    List<SMELoan> findLoansByDuration(@Param("duration") Integer duration);



}
