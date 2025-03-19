package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory,Integer> {

    Optional<SubCategory> findByCategory(String category);

//    @Query("select s from SubCategory s where s.isDeleted= false")
//    public List<SubCategory> selectAllActiveSubCat();
//
//    public List<SubCategory> findByisDeleted(boolean isDelete);
}
