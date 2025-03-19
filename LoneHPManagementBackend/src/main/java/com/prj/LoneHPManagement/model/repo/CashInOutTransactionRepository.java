package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.CashInOutTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashInOutTransactionRepository extends JpaRepository<CashInOutTransaction, Integer> {

    Page<CashInOutTransaction> findByBranchCurrentAccount_Branch_Id(int branchId, Pageable pageable);

    Page<CashInOutTransaction> findByBranchCurrentAccount_Id(int branchCurrentAccountId, Pageable pageable);
}