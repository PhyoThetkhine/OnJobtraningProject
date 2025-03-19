package com.prj.LoneHPManagement.Controller;

import com.prj.LoneHPManagement.Service.AutoPaymentService;
import com.prj.LoneHPManagement.Service.TransactionService;
import com.prj.LoneHPManagement.model.dto.ApiResponse;
import com.prj.LoneHPManagement.model.entity.PaymentMethod;
import com.prj.LoneHPManagement.model.entity.Transaction;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import com.prj.LoneHPManagement.model.repo.PaymentMethodRepository;
import com.prj.LoneHPManagement.model.repo.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private AutoPaymentService autoPaymentService;
    @Autowired
    private TransactionService transactionService;

    @PostMapping("/make")
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(1).orElseThrow(() -> new ServiceException("Payment method not found"));
        transaction.setPaymentMethod(paymentMethod);
        return ResponseEntity.ok(transactionService.processTransaction(transaction));
    }


    @GetMapping("/user/{userId}")
    public Page<Transaction> getTransactionsByUser(
            @PathVariable int userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return transactionService.getTransactionsByUserId(
                userId,
                page,
                size,
                "transactionDate"
        );
    }
    @GetMapping("/cif/{cifId}")
    public Page<Transaction> getTransactionsByCIF(
            @PathVariable int cifId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return transactionService.getTransactionsByCifId(
                cifId,
                page,
                size,
                "transactionDate"
        );
    }
    @GetMapping("/account/{accountId}")
    public ResponseEntity<?> getTransactionsByAccountId(
            @PathVariable int accountId,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @PageableDefault(size = 10) Pageable pageable) {

        // Create a new Pageable object with sorting
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortBy));

        Page<Transaction> transactions = transactionService.getTransactionsByAccount(accountId, sortedPageable);

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
