package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.SMELoanHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SMELoanHistoryRepository extends JpaRepository<SMELoanHistory,Integer> {
}
