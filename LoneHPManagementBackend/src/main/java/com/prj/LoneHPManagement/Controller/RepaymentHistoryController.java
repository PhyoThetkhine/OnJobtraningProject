package com.prj.LoneHPManagement.Controller;

import com.prj.LoneHPManagement.Service.impl.RepaymentHistoryService;
import com.prj.LoneHPManagement.model.dto.ApiResponse;
import com.prj.LoneHPManagement.model.entity.HpLoanHistory;
import com.prj.LoneHPManagement.model.entity.HpLongOverPaidHistory;
import com.prj.LoneHPManagement.model.entity.SMELongOverPaidHistory;
import com.prj.LoneHPManagement.model.entity.SMELoanHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class RepaymentHistoryController {
    private final RepaymentHistoryService historyService;

    @GetMapping("/{loanId}/repayment-history/under-90")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSMEUnder90History(
            @PathVariable Integer loanId,
            @PageableDefault(size = 5) Pageable pageable) {

        Page<SMELoanHistory> historyPage = historyService.getSMEUnder90History(loanId, pageable);


        // Build response structure
        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
        Map<String, Object> responseData = new HashMap<>();

        responseData.put("content", historyPage.getContent());
        responseData.put("totalPages", historyPage.getTotalPages());
        responseData.put("totalElements", historyPage.getTotalElements());
        responseData.put("size", historyPage.getSize());
        responseData.put("number", historyPage.getNumber());
        responseData.put("numberOfElements", historyPage.getNumberOfElements());
        responseData.put("first", historyPage.isFirst());
        responseData.put("last", historyPage.isLast());
        responseData.put("empty", historyPage.isEmpty());

        response.setStatus(HttpStatus.OK.value());
        response.setMessage("history List");
        response.setData(responseData);

        return ResponseEntity.ok(response);

    }
    @GetMapping("/{loanId}/hp-repayment-history/under-90")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHpUnder90History(
            @PathVariable Integer loanId,
            @PageableDefault(size = 5) Pageable pageable) {

        Page<HpLoanHistory> historyPage = historyService.getHpUnder90History(loanId, pageable);


        // Build response structure
        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
        Map<String, Object> responseData = new HashMap<>();

        responseData.put("content", historyPage.getContent());
        responseData.put("totalPages", historyPage.getTotalPages());
        responseData.put("totalElements", historyPage.getTotalElements());
        responseData.put("size", historyPage.getSize());
        responseData.put("number", historyPage.getNumber());
        responseData.put("numberOfElements", historyPage.getNumberOfElements());
        responseData.put("first", historyPage.isFirst());
        responseData.put("last", historyPage.isLast());
        responseData.put("empty", historyPage.isEmpty());

        response.setStatus(HttpStatus.OK.value());
        response.setMessage("history List");
        response.setData(responseData);

        return ResponseEntity.ok(response);

    }


    @GetMapping("/{loanId}/repayment-history/over-90")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSMEOver90History(
            @PathVariable Integer loanId,

            @PageableDefault(size = 5) Pageable pageable) {
        Page<SMELongOverPaidHistory> historyPage = historyService.getSMEOver90History(loanId,pageable);

        // Build response structure
        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
        Map<String, Object> responseData = new HashMap<>();

        responseData.put("content", historyPage.getContent());
        responseData.put("totalPages", historyPage.getTotalPages());
        responseData.put("totalElements", historyPage.getTotalElements());
        responseData.put("size", historyPage.getSize());
        responseData.put("number", historyPage.getNumber());
        responseData.put("numberOfElements", historyPage.getNumberOfElements());
        responseData.put("first", historyPage.isFirst());
        responseData.put("last", historyPage.isLast());
        responseData.put("empty", historyPage.isEmpty());

        response.setStatus(HttpStatus.OK.value());
        response.setMessage("history List");
        response.setData(responseData);

        return ResponseEntity.ok(response);

    }
    @GetMapping("/{loanId}/hp-repayment-history/over-90")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHpOver90History(
            @PathVariable Integer loanId,

            @PageableDefault(size = 5) Pageable pageable) {
        Page<HpLongOverPaidHistory> historyPage = historyService.getHpOver90History(loanId,pageable);

        // Build response structure
        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
        Map<String, Object> responseData = new HashMap<>();

        responseData.put("content", historyPage.getContent());
        responseData.put("totalPages", historyPage.getTotalPages());
        responseData.put("totalElements", historyPage.getTotalElements());
        responseData.put("size", historyPage.getSize());
        responseData.put("number", historyPage.getNumber());
        responseData.put("numberOfElements", historyPage.getNumberOfElements());
        responseData.put("first", historyPage.isFirst());
        responseData.put("last", historyPage.isLast());
        responseData.put("empty", historyPage.isEmpty());

        response.setStatus(HttpStatus.OK.value());
        response.setMessage("history List");
        response.setData(responseData);

        return ResponseEntity.ok(response);

    }

}