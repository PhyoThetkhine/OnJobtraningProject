package com.prj.LoneHPManagement.Controller;


import com.prj.LoneHPManagement.Service.impl.SubCategoryService;
import com.prj.LoneHPManagement.model.dto.ApiResponse;
import com.prj.LoneHPManagement.model.dto.PagedResponse;
import com.prj.LoneHPManagement.model.entity.SubCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subCategory")
public class SubCategoryController {

        @Autowired
        private SubCategoryService subCategoryService;


    // SubCategoryController.java
    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<PagedResponse<SubCategory>>> getAllSubCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Page<SubCategory> categories = subCategoryService.getAllSubCategories(page, size, sortBy);

        PagedResponse<SubCategory> pagedResponse = new PagedResponse<>(
                categories.getContent(),
                categories.getTotalPages(),
                categories.getTotalElements(),
                categories.getSize(),
                categories.getNumber(),
                categories.getNumberOfElements(),
                categories.isFirst(),
                categories.isLast(),
                categories.isEmpty()
        );

        return ResponseEntity.ok(ApiResponse.success(
                200,
                "Sub-categories retrieved successfully",
                pagedResponse
        ));
    }

    @PostMapping("/createSubCat/{mainCategoryId}")
    public ResponseEntity<SubCategory> createSubCategory(
            @PathVariable Integer mainCategoryId,
            @RequestBody SubCategory subCategory) {

        SubCategory createdSubCategory = subCategoryService.createSubCategory(
               mainCategoryId, subCategory
        );
        return new ResponseEntity<>(createdSubCategory, HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<List<SubCategory>> getAllSubCategories() {
        return ResponseEntity.ok(subCategoryService.getAllCategory());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubCategory> getSubCategoryById(@PathVariable Integer id) {
        return ResponseEntity.ok(subCategoryService.getCategoryById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubCategory> updateSubCategory(@PathVariable Integer id, @RequestBody SubCategory subCategory) {
        return ResponseEntity.ok(subCategoryService.updateSubCategory(id, subCategory));
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deleteSubCategory(@PathVariable Integer id) {
//        subCategoryService.deleteSubCategory(id);
//        return ResponseEntity.ok("SubCategory deleted successfully");
//    }
//
//    @PutMapping("/restore/{id}")
//    public ResponseEntity<String> restoreSubCategory(@PathVariable Integer id) {
//        subCategoryService.restoreSubCategory(id);
//        return ResponseEntity.ok("SubCategory restored successfully");
//    }
}
