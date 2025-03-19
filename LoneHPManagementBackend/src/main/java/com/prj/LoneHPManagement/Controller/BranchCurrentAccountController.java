package com.prj.LoneHPManagement.Controller;

import com.prj.LoneHPManagement.Service.BranchCurrentAccountService;
import com.prj.LoneHPManagement.model.dto.ApiResponse;
import com.prj.LoneHPManagement.model.entity.BranchCurrentAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/branchAccounts")
public class BranchCurrentAccountController {
    @Autowired
    private BranchCurrentAccountService accountService;
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<ApiResponse<BranchCurrentAccount>> getAccountByBranchId(
            @PathVariable int branchId) {

        BranchCurrentAccount account = accountService.getAccountByBranchId(branchId);
        ApiResponse<BranchCurrentAccount> response = new ApiResponse<>();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Account found for branch ID: " + branchId);
        response.setData(account);
        return ResponseEntity.ok(response);
    }
}
