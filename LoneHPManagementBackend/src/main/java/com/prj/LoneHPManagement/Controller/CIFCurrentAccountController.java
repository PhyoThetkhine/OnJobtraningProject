package com.prj.LoneHPManagement.Controller;

import com.prj.LoneHPManagement.model.dto.ApiResponse;
import com.prj.LoneHPManagement.model.entity.CIFCurrentAccount;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import com.prj.LoneHPManagement.Service.CIFCurrentAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cifCurrentAccounts")
public class CIFCurrentAccountController {

    @Autowired
    private CIFCurrentAccountService cifCurrentAccountService;

    @GetMapping("/getByCifId/{cifId}")
    public ResponseEntity<ApiResponse<CIFCurrentAccount>> getAccountByCifId(@PathVariable int cifId) {
        CIFCurrentAccount account = cifCurrentAccountService.getAccountByCifId(cifId);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Account not found"));
        }
        ApiResponse<CIFCurrentAccount> response = ApiResponse.success(
                HttpStatus.OK.value(), "Account fetched successfully", account);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/changeFreezeStatus/{accountId}/{status}")
    public ResponseEntity<ApiResponse<CIFCurrentAccount>> changeFreezeStatus(
            @PathVariable int accountId,
            @PathVariable String status) {

        // Call the service to change the freeze status
        CIFCurrentAccount updatedAccount = cifCurrentAccountService.changeFreezeStatus(accountId, status);

        if (updatedAccount == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Account not found or status change failed"));
        }

        ApiResponse<CIFCurrentAccount> response = ApiResponse.success(
                HttpStatus.OK.value(), "Account status updated successfully", updatedAccount);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/branch/{branchCode}")
    public ResponseEntity<Page<CIFCurrentAccount>> findByBranch(@PathVariable String branchCode, Pageable pageable) {
        return ResponseEntity.ok(cifCurrentAccountService.findByBranch(branchCode, pageable));
    }
    // Create or Update CIF Current Account
    @PostMapping("/save")
    public ResponseEntity<?> createOrUpdateCIFCurrentAccount(@RequestBody CIFCurrentAccount account) {
        if (account.getCif() == null || account.getCif().getId() == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "CIF ID is required"));
        }

        // If ID exists, update the existing account
        if (account.getId() != 0) {
            CIFCurrentAccount updatedAccount = cifCurrentAccountService.saveCIFCurrentAccount(account.getCif().getId(), account);
            ApiResponse<CIFCurrentAccount> response = ApiResponse.success(HttpStatus.OK.value(), "CIF Current Account updated successfully", updatedAccount);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            // If no ID exists, create a new account
            CIFCurrentAccount savedAccount = cifCurrentAccountService.saveCIFCurrentAccount(account.getCif().getId(), account);
            ApiResponse<CIFCurrentAccount> response = ApiResponse.success(HttpStatus.CREATED.value(), "CIF Current Account created successfully", savedAccount);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }

    // Get all CIF Current Accounts
    @GetMapping("/list")
    public ResponseEntity<?> getAllCIFCurrentAccounts() {
        List<CIFCurrentAccount> accounts = cifCurrentAccountService.getAllCIFCurrentAccounts();
        ApiResponse<List<CIFCurrentAccount>> response = ApiResponse.success(HttpStatus.OK.value(), "CIF Current Accounts fetched successfully", accounts);
        return ResponseEntity.ok(response);
    }

    // Get CIF Current Account by ID
    @GetMapping("/list/{id}")
    public ResponseEntity<?> getCIFCurrentAccountById(@PathVariable int id) {
        try {
            CIFCurrentAccount account = cifCurrentAccountService.getCIFCurrentAccountById(id);
            ApiResponse<CIFCurrentAccount> response = ApiResponse.success(HttpStatus.OK.value(), "CIF Current Account fetched successfully", account);
            return ResponseEntity.ok(response);
        } catch (ServiceException ex) {
            ApiResponse<String> response = ApiResponse.error(HttpStatus.NOT_FOUND.value(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    // Get CIF Current Accounts by CIF ID
    @GetMapping("/cif/{cifId}")
    public ResponseEntity<?> getCIFCurrentAccountsByCifId(@PathVariable int cifId) {
        List<CIFCurrentAccount> accounts = cifCurrentAccountService.getCIFCurrentAccountsByCifId(cifId);
        if (accounts.isEmpty()) {
            ApiResponse<List<CIFCurrentAccount>> response = ApiResponse.success(HttpStatus.NOT_FOUND.value(), "No CIF Current Accounts found for CIF ID: " + cifId, accounts);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ApiResponse<List<CIFCurrentAccount>> response = ApiResponse.success(HttpStatus.OK.value(), "CIF Current Accounts fetched successfully", accounts);
        return ResponseEntity.ok(response);
    }

    // Delete CIF Current Account by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCIFCurrentAccount(@PathVariable int id) {
        cifCurrentAccountService.deleteCIFCurrentAccount(id);
        ApiResponse<Void> response = ApiResponse.success(HttpStatus.NO_CONTENT.value(), "CIF Current Account deleted successfully", null);
        return ResponseEntity.ok(response);
    }
}
