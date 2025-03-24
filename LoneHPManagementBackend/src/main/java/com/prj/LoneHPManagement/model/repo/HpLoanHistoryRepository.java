package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.HpLoanHistory;
import com.prj.LoneHPManagement.model.entity.SMELoanHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HpLoanHistoryRepository extends JpaRepository<HpLoanHistory,Integer> {
    @Query("SELECT h FROM HpLoanHistory  h JOIN h.hpTerm t WHERE t.hpLoan.id = :loanId")
    Page<HpLoanHistory> findByLoanId(@Param("loanId") Integer loanId, Pageable pageable);
}
