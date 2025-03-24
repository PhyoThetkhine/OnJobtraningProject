package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.CIF;
import com.prj.LoneHPManagement.model.entity.HpLoan;
import com.prj.LoneHPManagement.model.entity.SMELoan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HpLoanRepository extends JpaRepository<HpLoan,Integer> {
    @Query("SELECT l FROM HpLoan l WHERE l.cif.id = :cifId AND l.status = :status")
    List<HpLoan> findByCifIdAndStatus(@Param("cifId") int cifId, @Param("status") int status);
    HpLoan findByHpLoanCode(String hpLoanCode);
    @Query("SELECT h FROM HpLoan h ORDER BY h.applicationDate DESC")
    Page<HpLoan> findAllSortedByApplicationDate(Pageable pageable);

    // For /all/branch/{branchId}
    @Query("SELECT h FROM HpLoan h WHERE h.hpLoanCode LIKE CONCAT(:branchCode, '%') ORDER BY h.applicationDate DESC")
    Page<HpLoan> findByBranchCode(String branchCode, Pageable pageable);

    // For /all/branch/{branchId}/status/{status}
    @Query("SELECT h FROM HpLoan h WHERE h.hpLoanCode LIKE CONCAT(:branchCode, '%') AND h.status = :status ORDER BY h.applicationDate DESC")
    Page<HpLoan> findByBranchCodeAndStatus(String branchCode, Pageable pageable, int status);

    // For /all/status/{status}
    Page<HpLoan> findByStatus(int status, Pageable pageable);
    Page<HpLoan> findByCifId(int cifId, Pageable pageable);
    @Query("SELECT MAX(h.hpLoanCode) FROM HpLoan h WHERE h.cif = :cif")
    String findMaxHpLoanCodeByCif(@Param("cif") CIF cif);


}