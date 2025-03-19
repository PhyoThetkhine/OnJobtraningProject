package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.Financial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialRepository extends JpaRepository<Financial, Integer> {
    Optional<Financial> findByCompany_Id(int companyId);
    List<Financial> findByCompanyId(int companyId);
}
