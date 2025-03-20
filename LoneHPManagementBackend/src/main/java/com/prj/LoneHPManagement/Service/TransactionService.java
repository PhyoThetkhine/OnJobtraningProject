package com.prj.LoneHPManagement.Service;

import com.prj.LoneHPManagement.model.dto.TransactionHistoryResponse;
import com.prj.LoneHPManagement.model.dto.TransferRequest;
import com.prj.LoneHPManagement.model.entity.CIFCurrentAccount;
import com.prj.LoneHPManagement.model.entity.Transaction;
import com.prj.LoneHPManagement.model.entity.UserCurrentAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {
      Page<Transaction> getTransactionsByAccount(int accountId, Pageable pageable);
    Transaction transferFunds(TransferRequest request);
    Transaction processTransaction(Transaction transaction) ;
    List<TransactionHistoryResponse> getTransactionHistory(String accountType, int accountId);
     Page<Transaction> getTransactionsByUserId(
            int userId,
            int page,
            int size,
            String sortBy
    );
    Page<Transaction> getTransactionsByCifId(
            int userId,
            int page,
            int size,
            String sortBy
    );
    Page<Transaction> getTransactionsByBranchId(
            int branchId,
            int page,
            int size,
            String sortBy
    );


}
