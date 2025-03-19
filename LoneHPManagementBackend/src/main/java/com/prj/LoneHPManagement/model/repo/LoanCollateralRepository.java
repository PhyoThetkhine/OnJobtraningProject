package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.LoanCollateral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanCollateralRepository extends JpaRepository<LoanCollateral, LoanCollateral.LoanCollateralPK> {
    void deleteByLoanId(Integer loanId);
}
