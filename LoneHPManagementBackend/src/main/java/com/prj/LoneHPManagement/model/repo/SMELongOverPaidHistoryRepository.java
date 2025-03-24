package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.SMELongOverPaidHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SMELongOverPaidHistoryRepository extends JpaRepository<SMELongOverPaidHistory, Integer> {
    Page<SMELongOverPaidHistory> findByLoanId(Integer loanId, Pageable pageable);
}