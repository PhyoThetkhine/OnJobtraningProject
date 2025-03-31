package com.prj.LoneHPManagement.Controller;

import com.prj.LoneHPManagement.Service.impl.BranchService;
import com.prj.LoneHPManagement.model.dto.ApiResponse;
import com.prj.LoneHPManagement.model.dto.BranchDTO;
import com.prj.LoneHPManagement.model.dto.BranchUpdateDTO;
import com.prj.LoneHPManagement.model.entity.Branch;
import com.prj.LoneHPManagement.model.repo.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/branch")
public class BranchController {

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private BranchService branchService;


    @GetMapping("/{id}")
    public ResponseEntity<Branch> getBranchById(@PathVariable int id) {
        Branch branch = branchService.getBranchById(id);
        return ResponseEntity.ok(branch);
    }


    @PutMapping("/{id}/status")
    public ResponseEntity<Branch> changeBranchStatus(
            @PathVariable int id,
            @RequestBody Map<String, String> statusRequest) {

        String newStatus = statusRequest.get("status");
        Branch updatedBranch = branchService.changeBranchStatus(id, newStatus);
        return ResponseEntity.ok(updatedBranch);
    }
    @GetMapping("/branches")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllbranches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        // Create pageable with sorting
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy.trim()));

        // Get paginated results
        Page<Branch> branches = branchService.getAllbranches(pageable);

        // Build response structure
        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
        Map<String, Object> responseData = new HashMap<>();

        responseData.put("content", branches.getContent());
        responseData.put("totalPages", branches.getTotalPages());
        responseData.put("totalElements", branches.getTotalElements());
        responseData.put("size", branches.getSize());
        responseData.put("number", branches.getNumber());
        responseData.put("numberOfElements", branches.getNumberOfElements());
        responseData.put("first", branches.isFirst());
        responseData.put("last", branches.isLast());
        responseData.put("empty", branches.isEmpty());

        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Branch List");
        response.setData(responseData);

        return ResponseEntity.ok(response);
    }
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Branch>> createBranch(@RequestBody Branch branch){
        Branch createBranch = branchService.createBranch(branch);
        ApiResponse<Branch> response = ApiResponse.success(HttpStatus.OK.value(),"Branch create successfully",createBranch);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/allBranches")
    public List<Branch> getAllBranches() {
        return branchService.getAllBranches();
    }

    @GetMapping("/code/{branchCode}")
    public ResponseEntity<ApiResponse<Branch>> getBranchByCode(@PathVariable String branchCode) {
        Branch branch = branchService.getBranchByCode(branchCode);

        if (branch != null) {
            ApiResponse<Branch> response = ApiResponse.success(HttpStatus.OK.value(), "Branch Code found", branch);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<Branch> response = ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Branch not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateBranch(@PathVariable int id, @RequestBody BranchUpdateDTO branchUpdateDTO){
        System.out.println("revice object"+branchUpdateDTO);
        Branch updatedBranch = branchService.updateBranch(id, branchUpdateDTO);
        return ResponseEntity.ok(updatedBranch);
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Branch>>> getActiveBranches() {
        List<Branch> activeBranches = branchService.getActiveBranches();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(),
                "Active branches retrieved successfully", activeBranches));
    }


}
