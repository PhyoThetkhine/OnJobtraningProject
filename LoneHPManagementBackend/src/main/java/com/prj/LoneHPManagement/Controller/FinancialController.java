package com.prj.LoneHPManagement.Controller;

import com.prj.LoneHPManagement.Service.FinancialService;  // ✅ Import the interface, not the implementation
import com.prj.LoneHPManagement.model.dto.ApiResponse;
import com.prj.LoneHPManagement.model.entity.Financial;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/financials")
public class FinancialController {
    @Autowired
    private FinancialService financialService;

    @GetMapping("/company/{companyId}")
    public ResponseEntity<ApiResponse<Financial>> getFinancialByCompanyId(
            @PathVariable int companyId) {

        Financial financial = financialService.getFinancialByCompanyId(companyId);

        ApiResponse<Financial> response = ApiResponse.success(
                200,
                "Financial data retrieved successfully for Company ID " + companyId,
                financial
        );

        return ResponseEntity.ok(response);
    }


    // ✅ Corrected method to include companyId in the request
    @PostMapping("/save/{companyId}")
    public ResponseEntity<?> createFinancial(@PathVariable int companyId, @RequestBody Financial financial) {
        Financial savedFinancial = financialService.createFinancial(companyId, financial);
        ApiResponse<Financial> response = ApiResponse.success(HttpStatus.CREATED.value(), "Financial created successfully", savedFinancial);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAllFinancials() {
        List<Financial> financials = financialService.getAllFinancials();
        ApiResponse<List<Financial>> response = ApiResponse.success(HttpStatus.OK.value(), "Financials fetched successfully", financials);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<?> getFinancialById(@PathVariable int id) {
        try {
            Financial financial = financialService.getFinancialById(id);
            ApiResponse<Financial> response = ApiResponse.success(HttpStatus.OK.value(), "Financial fetched successfully", financial);
            return ResponseEntity.ok(response);
        } catch (ServiceException ex) {
            ApiResponse<Financial> response = ApiResponse.success(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateFinancial(@PathVariable int id, @RequestBody Financial financialDetails) {
        Financial updatedFinancial = financialService.updateFinancial(id, financialDetails);
        ApiResponse<Financial> response = ApiResponse.success(HttpStatus.OK.value(), "Financial updated successfully", updatedFinancial);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFinancial(@PathVariable int id) {
        financialService.deleteFinancial(id);
        ApiResponse<Void> response = ApiResponse.success(HttpStatus.NO_CONTENT.value(), "Financial deleted successfully", null);
        return ResponseEntity.ok(response);
    }


}
