package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.Collateral;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollateralRepository extends JpaRepository<Collateral, Integer> {
    //List<Collateral> findByLoanId(int loanId);
    @Query("SELECT lc.collateral FROM LoanCollateral lc WHERE lc.loan.id = :loanId")
    Page<Collateral> findByLoanId(@Param("loanId") int loanId, Pageable pageable);
    Page<Collateral> findByCIF_Id(int cifId, Pageable pageable);
    List<Collateral> findByCIF_Id(int cifId);
}
