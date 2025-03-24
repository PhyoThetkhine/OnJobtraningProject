package com.prj.LoneHPManagement.model.repo;


import com.prj.LoneHPManagement.model.entity.HpLongOverPaidHistory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HpLongOverPaidHistoryRepository extends JpaRepository<HpLongOverPaidHistory,Integer> {
    Page<HpLongOverPaidHistory> findByLoanId(Integer loanId, Pageable pageable);
}
