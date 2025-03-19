package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.DealerProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealerProductRepository extends JpaRepository<DealerProduct, Integer> {
    List<DealerProduct> findByCompanyId(int companyId);
    List<DealerProduct> findByNameContainingIgnoreCase(String searchTerm);
}
