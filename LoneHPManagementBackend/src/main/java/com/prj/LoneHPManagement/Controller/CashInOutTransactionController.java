package com.prj.LoneHPManagement.Controller;

import com.prj.LoneHPManagement.Service.impl.CashInOutTransactionService;
import com.prj.LoneHPManagement.model.dto.ApiResponse;
import com.prj.LoneHPManagement.model.dto.CashTransactionDTO;
import com.prj.LoneHPManagement.model.entity.Branch;
import com.prj.LoneHPManagement.model.entity.CashInOutTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cash-transactions")
public class CashInOutTransactionController {

    private final CashInOutTransactionService cashInOutTransactionService;

    public CashInOutTransactionController(CashInOutTransactionService cashInOutTransactionService) {
        this.cashInOutTransactionService = cashInOutTransactionService;
    }


    @PostMapping("/transfer")
    public ResponseEntity<?> createCashTransaction(@RequestBody CashTransactionDTO dto) {
        System.out.println("recive dto"+dto);
        CashInOutTransaction transaction = cashInOutTransactionService.createTransaction(dto);
        ApiResponse<CashInOutTransaction> response = ApiResponse.success(HttpStatus.OK.value(),"successfully",transaction);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/account/{accountId}")
    public ResponseEntity<?> getTransactionsByAccountId(
            @PathVariable int accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        sortBy = sortBy.trim();
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        Page<CashInOutTransaction> transactions = cashInOutTransactionService.getTransactionsByAccount(accountId, page, size);

        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
        Map<String, Object> responseData = new HashMap<>();

        responseData.put("content", transactions.getContent());
        responseData.put("totalPages", transactions.getTotalPages());
        responseData.put("totalElements", transactions.getTotalElements());
        responseData.put("size", transactions.getSize());
        responseData.put("number", transactions.getNumber());
        responseData.put("numberOfElements", transactions.getNumberOfElements());
        responseData.put("first", transactions.isFirst());
        responseData.put("last", transactions.isLast());
        responseData.put("empty", transactions.isEmpty());

        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Transaction List");
        response.setData(responseData);

        return ResponseEntity.ok(response);
    }
}