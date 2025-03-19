package com.prj.LoneHPManagement.Controller;

import com.prj.LoneHPManagement.Service.CollateralService;
import com.prj.LoneHPManagement.Service.impl.CollateralServiceImpl;
import com.prj.LoneHPManagement.model.dto.ApiResponse;
import com.prj.LoneHPManagement.model.dto.PagedResponse;
import com.prj.LoneHPManagement.model.entity.Collateral;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping  ("/api/collaterals")
public class CollateralController {

    @Autowired
    private CollateralService collateralService;

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<ApiResponse<PagedResponse<Collateral>>> getCollateralsByLoanId(
            @PathVariable int loanId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Page<Collateral> collaterals = collateralService.getCollateralsByLoanId(loanId, page, size, sortBy);

        PagedResponse<Collateral> pagedResponse = new PagedResponse<>(
                collaterals.getContent(),
                collaterals.getTotalPages(),
                collaterals.getTotalElements(),
                collaterals.getSize(),
                collaterals.getNumber(),
                collaterals.getNumberOfElements(),
                collaterals.isFirst(),
                collaterals.isLast(),
                collaterals.isEmpty()
        );

        ApiResponse<PagedResponse<Collateral>> response = ApiResponse.success(
                200,
                "Collaterals for Loan ID " + loanId + " retrieved successfully",
                pagedResponse
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/cif/{cifId}")
    public ResponseEntity<ApiResponse<PagedResponse<Collateral>>> getCollateralsByCifId(
            @PathVariable int cifId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Page<Collateral> collaterals = collateralService.getCollateralsByCifId(cifId, page, size, sortBy);

        PagedResponse<Collateral> pagedResponse = new PagedResponse<>(
                collaterals.getContent(),
                collaterals.getTotalPages(),
                collaterals.getTotalElements(),
                collaterals.getSize(),
                collaterals.getNumber(),
                collaterals.getNumberOfElements(),
                collaterals.isFirst(),
                collaterals.isLast(),
                collaterals.isEmpty()
        );

        ApiResponse<PagedResponse<Collateral>> response = ApiResponse.success(
                200,
                "Collaterals for CIF ID " + cifId + " retrieved successfully",
                pagedResponse
        );

        return ResponseEntity.ok(response);
    }
//    // Create or update a Collateral
//    @PostMapping("/save")
//    public ResponseEntity<?> createCollateral(@RequestBody Collateral collateral) {
//        Collateral savedCollateral = collateralService.saveCollateral(collateral);
//        ApiResponse<Collateral> response = ApiResponse.success(HttpStatus.OK.value(), "User created successfully", savedCollateral);
//        return ResponseEntity.ok(response);
//    }


    @GetMapping("/list")
    public ResponseEntity<?> getAllCollaterals() {
        List<Collateral> collaterals = collateralService.getAllCollaterals();
        ApiResponse<List<Collateral>> response = ApiResponse.success(HttpStatus.OK.value(), "Collaterals fetched successfully", collaterals);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/cif/{cifId}/toSelect")
    public ResponseEntity<?> getCollateralsByCIF(@PathVariable int cifId) {
        List<Collateral> collaterals = collateralService.getCollateralByCIFId(cifId);
        ApiResponse<List<Collateral>> response = ApiResponse.success(HttpStatus.OK.value(), "Collaterals fetched successfully", collaterals);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<?> getCollateralById(@PathVariable int id) {
        ApiResponse<Collateral> response;

        try {
            Collateral collateral = collateralService.getCollateralById(id);
            response = ApiResponse.success(HttpStatus.OK.value(), "Collateral fetched successfully", collateral);
            return ResponseEntity.ok(response);
        } catch (ServiceException ex) {
            response = ApiResponse.success(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCollateral(@PathVariable int id) {
        collateralService.deleteCollateral(id);
        ApiResponse<Void> response = ApiResponse.success(HttpStatus.NO_CONTENT.value(), "Collateral deleted successfully", null);
        return ResponseEntity.ok(response);
    }



}
