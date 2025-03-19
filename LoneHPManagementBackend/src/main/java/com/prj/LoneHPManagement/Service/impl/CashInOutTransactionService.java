package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.model.dto.CashTransactionDTO;
import com.prj.LoneHPManagement.model.entity.BranchCurrentAccount;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import com.prj.LoneHPManagement.model.repo.BranchCurrentAccountRepository;
import com.prj.LoneHPManagement.model.repo.CashInOutTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.prj.LoneHPManagement.model.entity.CashInOutTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CashInOutTransactionService {
    @Autowired
    private BranchCurrentAccountRepository branchCurrentAccountRepository;

    private final CashInOutTransactionRepository cashInOutTransactionRepository;

    public CashInOutTransactionService(CashInOutTransactionRepository cashInOutTransactionRepository) {
        this.cashInOutTransactionRepository = cashInOutTransactionRepository;
    }
    public CashInOutTransaction createTransaction(CashTransactionDTO dto) {
        // 1. Validate and retrieve account
        BranchCurrentAccount account = branchCurrentAccountRepository
                .findById(dto.getBranchCurrentAccount().getId())
                .orElseThrow(() -> new ServiceException("Branch account not found"));

        // 2. Validate balance for cash-out
        if (dto.getType() == CashInOutTransaction.Type.Cash_Out) {
            if (account.getBalance().compareTo(dto.getAmount()) < 0) {
                throw new ServiceException(
                        "Insufficient balance for cash out operation");
            }
        }

        // 3. Update account balance
        BigDecimal newBalance = dto.getType() == CashInOutTransaction.Type.Cash_In
                ? account.getBalance().add(dto.getAmount())
                : account.getBalance().subtract(dto.getAmount());

        account.setBalance(newBalance);
        branchCurrentAccountRepository.save(account);

        // 4. Create and save transaction
        CashInOutTransaction transaction = new CashInOutTransaction();
        transaction.setType(dto.getType());
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setBranchCurrentAccount(account);
        transaction.setTransactionDate(LocalDateTime.now());

        return cashInOutTransactionRepository.save(transaction);
    }
    public Page<CashInOutTransaction> getTransactionsByAccount(int accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "transactionDate"));
        return cashInOutTransactionRepository.findByBranchCurrentAccount_Id(accountId, pageable);
    }
}