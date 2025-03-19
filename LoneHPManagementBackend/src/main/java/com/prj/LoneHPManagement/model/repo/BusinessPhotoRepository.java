package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.BusinessPhoto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessPhotoRepository extends JpaRepository<BusinessPhoto, Integer> {
    List<BusinessPhoto> findByCompanyIdOrderByIdAsc(int companyId);
}