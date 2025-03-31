package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.SubCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory,Integer> {
    Optional<SubCategory> findByCategory(String category);
    SubCategory findByCategoryIgnoreCaseAndStatus(String category, int status);
    SubCategory findByCategoryIgnoreCaseAndStatusAndIdNot(String category, int status, Integer id);
    Page<SubCategory> findByStatus(int status, Pageable pageable);
    List<SubCategory> findByStatus(int status);
}
