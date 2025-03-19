package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.CIFCurrentAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CIFCurrentAccountRepository extends JpaRepository<CIFCurrentAccount, Integer> {

    // In CIFCurrentAccountRepository
    @Query("SELECT cca FROM CIFCurrentAccount cca WHERE cca.cif.id = :cifId")
    Optional<CIFCurrentAccount> findByCifId(@Param("cifId") int cifId);
    @Query("SELECT c FROM CIFCurrentAccount c WHERE c.accCode LIKE CONCAT('CAC', :branchCode, '%')")
    Page<CIFCurrentAccount> findByBranchCode(@Param("branchCode") String branchCode, Pageable pageable);

}
