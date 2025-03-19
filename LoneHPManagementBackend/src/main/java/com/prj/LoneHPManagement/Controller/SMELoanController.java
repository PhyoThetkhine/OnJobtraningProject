package com.prj.LoneHPManagement.Controller;

import com.prj.LoneHPManagement.Service.SMELoanService;
import com.prj.LoneHPManagement.model.dto.*;
import com.prj.LoneHPManagement.model.entity.SMETerm;
import org.springframework.beans.factory.annotation.Autowired;
import com.prj.LoneHPManagement.model.entity.SMELoan;
import com.prj.LoneHPManagement.model.entity.UserCurrentAccount;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/smeLoan")
public class SMELoanController {
    @PostMapping("/confirm/{loanId}")
    public ResponseEntity<ApiResponse<String>> confirmSMELoan(
            @PathVariable int loanId,
            @RequestBody ConfirmLoanData confirmData) {
        try {
            smeLoanService.confirm_Loan(loanId, confirmData);
            return ResponseEntity.ok(ApiResponse.success(200, "SME Loan confirmed successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(500, "Failed to confirm SME loan: " + e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<SMELoan>> createLoan(
             @RequestBody CreateSMELoanRequest request) {
        System.out.println("Received request: " + request);

        SMELoan createdLoan = smeLoanService.createLoan(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.OK.value(), "Loan created successfully", createdLoan));
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<SMELoan>> updateLoan(
            @PathVariable int id,
            @RequestBody UpdateSMELoanRequest request) {
        System.out.println("Received request: " + request);

        SMELoan updatedLoan = smeLoanService.updateLoan(id, request);
        ApiResponse<SMELoan> response = new ApiResponse<>(HttpStatus.OK.value(), "Sucessfully",updatedLoan);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/principalStatus/{loanId}")
    public ResponseEntity<ApiResponse<String>> updatePrincipalStatus(
            @PathVariable int loanId,
            @RequestParam String status) { // Accepting status as a request parameter
        try {
            smeLoanService.updatePrincipalStatus(loanId, status);
            return ResponseEntity.ok(ApiResponse.success(200, "Principal status updated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(500, "Failed to update principal status: " + e.getMessage()));
        }
    }


    private final SMELoanService smeLoanService;
    @GetMapping("/terms/{loanId}")
    public ResponseEntity<List<SMETerm>> getTermsByLoanId(@PathVariable int loanId) {
        List<SMETerm> terms = smeLoanService.getTermsByLoanId(loanId);
        return ResponseEntity.ok(terms);
    }
    @GetMapping("/byCif/{cifId}")
    public ResponseEntity<ApiResponse<PagedResponse<SMELoan>>> getSMELoansByCif(
            @PathVariable int cifId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Page<SMELoan> loans = smeLoanService.getSMELoansByCif(cifId, page, size, sortBy);

        PagedResponse<SMELoan> pagedResponse = new PagedResponse<>(
                loans.getContent(),
                loans.getTotalPages(),
                loans.getTotalElements(),
                loans.getSize(),
                loans.getNumber(),
                loans.getNumberOfElements(),
                loans.isFirst(),
                loans.isLast(),
                loans.isEmpty()
        );

        ApiResponse<PagedResponse<SMELoan>> response = ApiResponse.success(
                200,
                "SME Loans for CIF " + cifId + " retrieved successfully",
                pagedResponse
        );

        return ResponseEntity.ok(response);
    }

    public SMELoanController(SMELoanService smeLoanService) {
        this.smeLoanService = smeLoanService;
    }
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<PagedResponse<SMELoan>>> getSMELoans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Page<SMELoan> loans = smeLoanService.getSMELoans(page, size, sortBy);

        // Map Page<SMELoan> to PagedResponse<SMELoan>
        PagedResponse<SMELoan> pagedResponse = new PagedResponse<>(
                loans.getContent(),          // List of loans
                loans.getTotalPages(),      // Total number of pages
                loans.getTotalElements(),    // Total number of elements
                loans.getSize(),            // Size of the current page
                loans.getNumber(),          // Current page number
                loans.getNumberOfElements(), // Number of elements in the current page
                loans.isFirst(),            // Is this the first page?
                loans.isLast(),             // Is this the last page?
                loans.isEmpty()             // Is this page empty?
        );

        ApiResponse<PagedResponse<SMELoan>> response = ApiResponse.success(
                200,
                "SME Loans retrieved successfully",
                pagedResponse
        );

        return ResponseEntity.ok(response);
    }
    @GetMapping("/getBy/{id}")
    public ResponseEntity<ApiResponse<SMELoan>> getSMELoanById(@PathVariable int id) {
        SMELoan smeLoan = smeLoanService.getLoanById(id);
        ApiResponse<SMELoan> response = ApiResponse.success(200, "SME Loan retrieved successfully", smeLoan);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/list")
    public ResponseEntity<List<SMELoan>> getAllLoans() {
        return ResponseEntity.ok(smeLoanService.getAllLoans());
    }

    @GetMapping("/getLoanByLoanCode/{smeLoanCode}")
    public ResponseEntity<SMELoan> getLoanByLoanCode(@PathVariable String smeLoanCode) {
       SMELoan smeLoans = smeLoanService.findLoanBySmeLoanCode(smeLoanCode);
        return ResponseEntity.ok(smeLoans);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveLoan(@RequestBody SMELoan smeLoan) {
        SMELoan savedLoan = smeLoanService.save((smeLoan));
        ApiResponse<SMELoan> response = ApiResponse.success(HttpStatus.OK.value(), "SMELoan saved successfully", savedLoan);
        return ResponseEntity.ok(response);
    }

//    @PutMapping("/update/{id}")
//    public ResponseEntity<SMELoan> updateLoan(@PathVariable Integer id , @RequestBody SMELoan smeLoan) {
//        SMELoan updatedLoan = smeLoanService.updateLoanById(id , smeLoan);
//
//        if (updatedLoan != null) {
//            return ResponseEntity.ok(updatedLoan);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
    @PostMapping("/{loanId}/confirm")
    public ResponseEntity<?> confirmLoan(@PathVariable int loanId) {
        try {
            smeLoanService.confirmLoan(loanId);
            return ResponseEntity.ok().body(
                    Map.of(
                            "status", "SUCCESS",
                            "message", "Loan confirmed successfully",
                            "loanId", loanId
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "error", "Confirmation failed",
                            "message", e.getMessage(),
                            "loanId", loanId
                    )
            );
        }
    }


}
