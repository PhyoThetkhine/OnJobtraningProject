package com.prj.LoneHPManagement.model.repo;


import com.prj.LoneHPManagement.model.entity.Branch;
import com.prj.LoneHPManagement.model.entity.CIF;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch,Integer> {
    List<Branch> findByStatus(int status);
   Branch findByBranchCode(String branchCode);
    Page<Branch> findAll(Pageable pageable);

    @Query("SELECT MAX(b.branchCode) FROM Branch b")
    String findMaxBranchCode();

}
