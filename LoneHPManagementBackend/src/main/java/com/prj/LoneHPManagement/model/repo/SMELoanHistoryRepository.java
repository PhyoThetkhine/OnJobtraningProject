package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.SMELoanHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SMELoanHistoryRepository extends JpaRepository<SMELoanHistory,Integer> {
    @Query("SELECT h FROM SMELoanHistory h JOIN h.smeTerm t WHERE t.smeLoan.id = :loanId")
    Page<SMELoanHistory> findByLoanId(@Param("loanId") Integer loanId, Pageable pageable);
}
