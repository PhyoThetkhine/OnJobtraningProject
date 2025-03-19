package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.BranchCurrentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BranchCurrentAccountRepository extends JpaRepository<BranchCurrentAccount, Integer> {
    
    @Query("SELECT bca FROM BranchCurrentAccount bca WHERE bca.accCode LIKE CONCAT('BAC', :branchCode, '%')")
    Optional<BranchCurrentAccount> findByBranchCode(@Param("branchCode") String branchCode);
    Optional<BranchCurrentAccount> findByBranch_Id(int id);
}
