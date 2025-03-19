package com.prj.LoneHPManagement.Service.impl;
import com.prj.LoneHPManagement.model.entity.ConstraintEnum;
import com.prj.LoneHPManagement.model.entity.MainCategory;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import com.prj.LoneHPManagement.model.repo.MainCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MainCatogoryService {

    @Autowired
    private MainCategoryRepository mainCategoryRepostirory;

    public MainCategory createMainCat(MainCategory mainCategory){

       return mainCategoryRepostirory.save(mainCategory);
    }
    public Page<MainCategory> getAllMainCategories(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return mainCategoryRepostirory.findAll(pageable);
    }

//    public List<MainCategory> getAllCategory() {
//
//        return mainCategoryRepostirory.selectAllActiveMainCat();
//    }

    public MainCategory getCategoryById(Integer id){
        return mainCategoryRepostirory.findById(id).orElseThrow(() -> new ServiceException("Category not found"));
    }

    @Transactional
    public MainCategory updateCategory(Integer id,MainCategory mainCategory){
            MainCategory mainCat = getCategoryById(id);
            mainCat.setId(mainCategory.getId());
            mainCat.setCategory(mainCategory.getCategory());
            return mainCategoryRepostirory.save(mainCategory);
    }

//    @Transactional
//    public void deleteMainCat(Integer id){
//        MainCategory mainCat = getCategoryById(id);
//       mainCat.setIsDelete(ConstraintEnum.TERMINATED.getCode());
//        mainCategoryRepostirory.save(mainCat);
//    }

//    public List<MainCategory>selectAllActiveMainCat(){
//
//        return mainCategoryRepostirory.selectAllActiveMainCat();
//    }

//    public List<MainCategory> findByisDeleted(boolean isDeleted) {
//
//        return mainCategoryRepostirory.findByisDeleted(isDeleted);
//    }
}

