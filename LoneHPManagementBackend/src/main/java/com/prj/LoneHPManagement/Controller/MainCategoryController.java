package com.prj.LoneHPManagement.Controller;


import com.prj.LoneHPManagement.Service.impl.MainCatogoryService;
import com.prj.LoneHPManagement.model.dto.ApiResponse;
import com.prj.LoneHPManagement.model.dto.PagedResponse;
import com.prj.LoneHPManagement.model.entity.MainCategory;
import com.prj.LoneHPManagement.model.repo.MainCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mainCategory")
public class MainCategoryController {

//    @Autowired
//    private MainCategoryRepository mainCategoryRepostirory;

    @Autowired
    private MainCatogoryService mainCatogoryService;
    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<PagedResponse<MainCategory>>> getAllMainCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Page<MainCategory> categories = mainCatogoryService.getAllMainCategories(page, size, sortBy);

        PagedResponse<MainCategory> pagedResponse = new PagedResponse<>(
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
                "Main categories retrieved successfully",
                pagedResponse
        ));
    }

    @PostMapping("/createMainCat")
    public ResponseEntity<MainCategory> createMaincat(@RequestBody MainCategory mainCategory){
        MainCategory createMaincat = mainCatogoryService.createMainCat(mainCategory);
        return new ResponseEntity<>(mainCategory, HttpStatus.CREATED);
    }

//    @GetMapping("/list")
//    public ResponseEntity<List<MainCategory>> getAllCategories() {
//       MainCategory mainCategory = new MainCategory();
//        List<MainCategory> MainCatList = mainCatogoryService.selectAllActiveMainCat();
//        return new ResponseEntity<List<MainCategory>>(HttpStatus.OK).ok(MainCatList);
//    }


    @PutMapping("/{id}")
    public MainCategory updateMainCat(@PathVariable Integer id, @RequestBody MainCategory mainCategory) {
        return mainCatogoryService.updateCategory(id, mainCategory);
    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deleteMainCat(@PathVariable Integer id) {
//        mainCatogoryService.deleteMainCat(id);
//        return ResponseEntity.ok("Category deleted successfully");
//    }



}
