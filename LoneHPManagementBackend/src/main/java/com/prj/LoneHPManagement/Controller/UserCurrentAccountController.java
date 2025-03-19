package com.prj.LoneHPManagement.Controller;

import com.prj.LoneHPManagement.model.dto.ApiResponse;
import com.prj.LoneHPManagement.model.entity.ConstraintEnum;
import com.prj.LoneHPManagement.model.entity.User;
import com.prj.LoneHPManagement.model.entity.UserCurrentAccount;
import com.prj.LoneHPManagement.Service.UserCurrentAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/user-current-account")
public class UserCurrentAccountController {

    private final UserCurrentAccountService userCurrentAccountService;

    public UserCurrentAccountController(UserCurrentAccountService userCurrentAccountService) {
        this.userCurrentAccountService = userCurrentAccountService;
    }

    @GetMapping("/getByUserId/{userId}")
    public ResponseEntity<ApiResponse<UserCurrentAccount>> getAccountByUserId(@PathVariable int userId) {
        UserCurrentAccount account = userCurrentAccountService.getAccountByUserId(userId);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Account not found"));
        }
        ApiResponse<UserCurrentAccount> response = ApiResponse.success(
                HttpStatus.OK.value(), "Account fetched successfully", account);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/changeStatus/{accountId}/{status}")
    public ResponseEntity<UserCurrentAccount> changeFreezeStatus(@PathVariable int accountId, @PathVariable String status) {
        UserCurrentAccount updatedAccount = userCurrentAccountService.changeFreezeStatus(accountId, status);
        return ResponseEntity.ok(updatedAccount);
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserCurrentAccount>> getAllCurrentAccounts() {
        return  ResponseEntity.ok(userCurrentAccountService.getAllAccount());
    }
//    @GetMapping("/availableAccounts")
//    public ResponseEntity<List<UserCurrentAccount>> getAllAvailableCurrentAccounts() {
//        return ResponseEntity.ok(userCurrentAccountService.getAvailableAccounts());
//    }

    @GetMapping("/frozeAccountList")
    public ResponseEntity<List<UserCurrentAccount>> getFreezeCurrentAccounts() {
        List<UserCurrentAccount> frozeCurrentAccounts = userCurrentAccountService.getFrozenAccounts(ConstraintEnum.IS_FREEZE.getCode());
        return ResponseEntity.ok(frozeCurrentAccounts);
    }

    @GetMapping("/getByAccCode/{accCode}")
    public ResponseEntity<List<UserCurrentAccount>> getAccountByAccCode(@PathVariable String accCode) {
        List<UserCurrentAccount> userCurrentAccounts = userCurrentAccountService.getAllAccountByAccCode(accCode);
        return ResponseEntity.ok(userCurrentAccounts);
    }

    @GetMapping("/getbyuserd/{id}")
    public ResponseEntity<List<UserCurrentAccount>> getAccountByUserId(@PathVariable Integer userId) {
        List<UserCurrentAccount> userCurrentAccounts = userCurrentAccountService.getAllAccountByUserId(userId);
        return  ResponseEntity.ok(userCurrentAccounts);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveCurrentAccount(@RequestBody UserCurrentAccount userCurrentAccount) {
        userCurrentAccount.setIsFreeze(ConstraintEnum.NOT_FREEZE.getCode());
        UserCurrentAccount savedAccount = userCurrentAccountService.saveCurrentAccount(userCurrentAccount);
        ApiResponse<UserCurrentAccount> response = ApiResponse.success(HttpStatus.OK.value(), "User  current account created successfully", savedAccount);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserCurrentAccount> updateCurrentAccount(
            @PathVariable Integer id,
            @RequestBody UserCurrentAccount userCurrentAccount) {

        UserCurrentAccount updatedAccount = userCurrentAccountService.updateAccountById(id, userCurrentAccount);

        if (updatedAccount != null) {
            return ResponseEntity.ok(updatedAccount);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/freeze/{id}")
    public ResponseEntity<UserCurrentAccount> freezeCurrentAccount(@PathVariable Integer id) {
         UserCurrentAccount frozeAccount = userCurrentAccountService.getAccountFreezeStatus(id);
        return ResponseEntity.ok(frozeAccount);
    }

    @PutMapping("/unFreeze/{id}")
    public ResponseEntity<UserCurrentAccount> unFreezeCurrentAccount(@PathVariable Integer id) {
        UserCurrentAccount unfreezeAccount = userCurrentAccountService.getAccountUnFreezeStatus(id);
        return ResponseEntity.ok(unfreezeAccount);
    }

//    @GetMapping("/findAccByBalance/{balance}")
//    public ResponseEntity<List<UserCurrentAccount>> getAccountByBalance(@PathVariable BigDecimal balance) {
//        List<UserCurrentAccount> userCurrentAccounts = userCurrentAccountService.findByBalanceGreaterThan(balance);
//        return ResponseEntity.ok(userCurrentAccounts);
//    }


}
