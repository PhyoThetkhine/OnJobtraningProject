package com.prj.LoneHPManagement.model.repo;
import com.prj.LoneHPManagement.model.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {
   public Company findByPhoneNumber(String phoneNumber);
   Page<Company> findByCifId(int cifId, Pageable pageable);

}
