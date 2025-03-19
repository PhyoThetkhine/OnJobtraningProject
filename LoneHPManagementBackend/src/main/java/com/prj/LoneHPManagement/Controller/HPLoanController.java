package com.prj.LoneHPManagement.Controller;

import com.prj.LoneHPManagement.Service.HpLoanService;
import com.prj.LoneHPManagement.model.dto.*;
import com.prj.LoneHPManagement.model.entity.HpLoan;
import com.prj.LoneHPManagement.model.entity.HpTerm;
import com.prj.LoneHPManagement.model.entity.SMELoan;
import com.prj.LoneHPManagement.model.repo.HpLoanRepository;
import com.prj.LoneHPManagement.model.repo.HpTermRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/HPLoan")
public class HPLoanController {

    @Autowired
  private   HpLoanService hpLoanService;
    @Autowired
    private HpLoanRepository hpLoanRepository;
    @Autowired
    private HpTermRepository hpTermRepository;
    @GetMapping("/terms/{loanId}")
    public ResponseEntity<List<HpTerm>> getTermsByLoanId(@PathVariable int loanId) {
        List<HpTerm> terms = hpLoanService.getTermsByLoanId(loanId);
        return ResponseEntity.ok(terms);
    }
    @GetMapping ("/testTerm/{loanId}")
    public ResponseEntity<ApiResponse<String>> lestgenerateterm(
            @PathVariable int loanId
            ){
        HpLoan loan = hpLoanRepository.findById(loanId).orElseThrow(null);
        List<HpTerm> terms = hpLoanService.generateTerms(loan, LocalDate.now(), loan.getDuration());
        hpTermRepository.saveAll(terms);
        return ResponseEntity.ok(ApiResponse.success(200, "Loan term successfully", null));
    }
    @GetMapping("/byCif/{cifId}")
    public ResponseEntity<ApiResponse<PagedResponse<HpLoan>>> getHPLoansByCif(
            @PathVariable int cifId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Page<HpLoan> loans = hpLoanService.getHPLoansByCif(cifId, page, size, sortBy);

        PagedResponse<HpLoan> pagedResponse = new PagedResponse<>(
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

        ApiResponse<PagedResponse<HpLoan>> response = ApiResponse.success(
                200,
                "HP Loans for CIF " + cifId + " retrieved successfully",
                pagedResponse
        );

        return ResponseEntity.ok(response);
    }
    @PostMapping("/confirm/{loanId}")
    public ResponseEntity<ApiResponse<String>> confirmLoan(
            @PathVariable int loanId,
            @RequestBody ConfirmLoanData confirmData) {
        try {
            hpLoanService.confirm_Loan(loanId, confirmData);
            return ResponseEntity.ok(ApiResponse.success(200, "Loan confirmed successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(500, "Failed to confirm loan: " + e.getMessage()));
        }
    }
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<HpLoan>> create(@RequestBody CreateHpLoanDto dto){
        System.out.println("Request body: " + dto);
        HpLoan loan = hpLoanService.createLoan(dto);
        return ResponseEntity.ok(ApiResponse.success(200, "Loan created successfully", loan));
    }
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<PagedResponse<HpLoan>>> getLoans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Page<HpLoan> loans = hpLoanService.getLoans(page, size, sortBy);

        // Map Page<HpLoan> to PagedResponse<HpLoan>
        PagedResponse<HpLoan> pagedResponse = new PagedResponse<>(
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

        ApiResponse<PagedResponse<HpLoan>> response = ApiResponse.success(
                200,
                "Loans retrieved successfully",
                pagedResponse
        );

        return ResponseEntity.ok(response);
    }
    @GetMapping("/getBy/{id}")
    public ResponseEntity<ApiResponse<HpLoan>> getLoanById(@PathVariable int id) {
        HpLoan loan = hpLoanService.getLoanById(id);
        ApiResponse<HpLoan> response = ApiResponse.success(200, "Loan retrieved successfully", loan);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<List<HpLoan>> getAllLoans() {
        return ResponseEntity.ok(hpLoanService.getAllLoans());
    }

    @GetMapping("getLoanByLoanCode/{loanCode}")
    public  ResponseEntity<HpLoan> getLoanByLoanCode(@PathVariable String hpLoanCode) {
        HpLoan hpLoan = hpLoanService.findByHpLoanCode(hpLoanCode);
        return ResponseEntity.ok(hpLoan);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveLoan(@RequestBody HpLoan hpLoan) {
        HpLoan savedLoan = hpLoanService.save((hpLoan));
        ApiResponse<HpLoan> response = ApiResponse.success(HttpStatus.OK.value(), "HPLoan saved successfully", savedLoan);
        return ResponseEntity.ok(response);
    }

//    @PutMapping("/update/{id}")
//    public ResponseEntity<HpLoan> updateLoan(@PathVariable Integer id , @RequestBody HpLoan hpLoan) {
//        HpLoan updatedLoan = hpLoanService.updateLoanById(id , hpLoan);
//
//        if (updatedLoan != null) {
//            return ResponseEntity.ok(updatedLoan);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<HpLoan>> updateLoan(
            @PathVariable int id,
            @RequestBody UpdateHPLoanRequest request) {

        HpLoan updatedLoan = hpLoanService.updateLoan(id, request);
        ApiResponse<HpLoan> response = new ApiResponse<>(HttpStatus.OK.value(), "sucessfully",updatedLoan);
        return ResponseEntity.ok(response);
    }



}
