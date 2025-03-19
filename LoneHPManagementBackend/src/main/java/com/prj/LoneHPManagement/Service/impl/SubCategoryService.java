package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.model.entity.MainCategory;
import com.prj.LoneHPManagement.model.entity.SubCategory;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import com.prj.LoneHPManagement.model.repo.MainCategoryRepository;
import com.prj.LoneHPManagement.model.repo.SubCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubCategoryService {

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private MainCategoryRepository mainCategoryRepository;
    public Page<SubCategory> getAllSubCategories(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return subCategoryRepository.findAll(pageable);
    }

    public SubCategory createSubCategory(Integer mainCategoryId, SubCategory subCategory) {
        MainCategory mainCategory = mainCategoryRepository.findById(mainCategoryId)
                .orElseThrow(() -> new RuntimeException("Main Category not found"));
        subCategory.setMainCategory(mainCategory);

        if(subCategoryRepository.findByCategory(subCategory.getCategory()) != null){
            throw new ServiceException("Subcategory already exit");
        }

//        if(subCategoryRepository.findByCategoryIgnoreCase(subCategory.getCategory()) != null){
//            throw new ServiceException("Subcategory already exit");
//        }

        return subCategoryRepository.save(subCategory);
    }

    public List<SubCategory> getAllCategory(){
        return subCategoryRepository.findAll();
    }

    public SubCategory getCategoryById(Integer id){
        return subCategoryRepository.findById(id).orElseThrow(() -> new ServiceException("Category not found"));
    }

    @Transactional
    public SubCategory updateSubCategory(Integer id, SubCategory updatedSubCategory) {
        SubCategory existingSubCategory = getCategoryById(id);
        existingSubCategory.setCategory(updatedSubCategory.getCategory());
        if (updatedSubCategory.getMainCategory() != null) {

            MainCategory mainCategory = mainCategoryRepository.findById(updatedSubCategory.getMainCategory().getId())
                    .orElseThrow(() -> new ServiceException("Main Category not found"));


            existingSubCategory.setMainCategory(mainCategory);
        } else {

            throw new IllegalArgumentException("Main Category must not be null");
        }        return subCategoryRepository.save(existingSubCategory);
    }

//    @Transactional
//    public void deleteSubCategory(Integer id) {
//        SubCategory subCategory = getCategoryById(id);
//        subCategoryRepository.save(subCategory);
//    }

//    @Transactional
//    public void restoreSubCategory(Integer id) {
//        SubCategory subCategory = getCategoryById(id);
//        if (subCategory != null) {
//            subCategoryRepository.save(subCategory);
//        } else {
//            throw new ServiceException("SubCategory not found with id " + id);
//        }
//    }

//    public List<SubCategory>selectAllActiveSubCat(){
//
//        return subCategoryRepository.selectAllActiveSubCat();
//    }

//    public List<SubCategory> findByisDeleted(boolean isDeleted) {
//
//        return subCategoryRepository.findByisDeleted(isDeleted);
//    }

}
