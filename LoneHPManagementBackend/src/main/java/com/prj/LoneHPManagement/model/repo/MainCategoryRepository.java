package com.prj.LoneHPManagement.model.repo;


import com.prj.LoneHPManagement.model.entity.MainCategory;
import com.prj.LoneHPManagement.model.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MainCategoryRepository extends JpaRepository<MainCategory,Integer> {

    MainCategory findByCategory(String category);

    @Query("SELECT m FROM MainCategory m WHERE LOWER(m.category) = LOWER(:category)")
    MainCategory findByCategoryIgnoreCase(String category);

//    @Query("select m from MainCategory m where m.isDeleted=false")
//    public List<MainCategory> selectAllActiveMainCat();



}
