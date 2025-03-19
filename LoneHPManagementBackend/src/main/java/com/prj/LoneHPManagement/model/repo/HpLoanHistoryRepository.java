package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.HpLoanHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HpLoanHistoryRepository extends JpaRepository<HpLoanHistory,Integer> {

}
