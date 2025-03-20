package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.Service.TransactionService;
import com.prj.LoneHPManagement.model.dto.TransactionHistoryResponse;
import com.prj.LoneHPManagement.model.dto.TransferRequest;
import com.prj.LoneHPManagement.model.entity.*;
import com.prj.LoneHPManagement.model.exception.AccountNotFoundException;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import com.prj.LoneHPManagement.model.repo.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private BranchCurrentAccountRepository branchCurrentAccountRepository;
    @Autowired
    private CIFCurrentAccountRepository cifCurrentAccountRepository;
    @Autowired
    private AutoPaymentServiceImpl autoPaymentService;
    @Autowired
    private UserCurrentAccountRepository userCurrentAccountRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private CIFRepository cifRepository;

    @Override
    public Page<Transaction> getTransactionsByUserId(
            int userId,
            int page,
            int size,
            String sortBy
    ) {
        // Sort by transactionDate in descending order
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "transactionDate")
        );
        return transactionRepository.findTransactionsByUserId(userId, pageable);
    }
    @Override
    public Page<Transaction> getTransactionsByCifId(
            int userId,
            int page,
            int size,
            String sortBy
    ) {
        // Sort by transactionDate in descending order
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "transactionDate")
        );
        return transactionRepository.findTransactionByCifId(userId, pageable);
    }
    @Override
    public Page<Transaction> getTransactionsByBranchId(
            int branchId,
            int page,
            int size,
            String sortBy
    ) {
        // Sort by transactionDate in descending order
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "transactionDate")
        );
        return transactionRepository.findTransactionByBranchId(branchId, pageable);
    }
    @Override
    public Page<Transaction> getTransactionsByAccount(int accountId, Pageable pageable) {
     //   Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "transactionDate"));
        return transactionRepository.findByFromAccountIdOrToAccountId(accountId, accountId, pageable);
    }

    @Override
    @Transactional
    public Transaction transferFunds(TransferRequest request) {
        BigDecimal transferAmount = request.getAmount();

        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId()) .orElseThrow(() -> new ServiceException("Payment not found"));
        // Handle BRANCH to CIF transaction
        if (request.getFromAccountType() == Transaction.AccountType.BRANCH &&
                request.getToAccountType() == Transaction.AccountType.CIF) {
            CIFCurrentAccount cifAccount = cifCurrentAccountRepository.findById(request.getToAccountId())
                    .orElseThrow(() -> new AccountNotFoundException("CIF account not found"));

            // Credit CIF account
            cifAccount.setBalance(cifAccount.getBalance().add(request.getAmount()));
            cifCurrentAccountRepository.save(cifAccount);
            //Debit Branch Account
            BranchCurrentAccount branchCurrentAccount = branchCurrentAccountRepository.findById(request.getFromAccountId()).orElseThrow(() -> new AccountNotFoundException("Branch account not found"));

            branchCurrentAccount.setBalance(branchCurrentAccount.getBalance().subtract(request.getAmount()));
            branchCurrentAccountRepository.save(branchCurrentAccount);
        }
        // Handle CIF to BRANCH transaction
        else if (request.getFromAccountType() == Transaction.AccountType.CIF &&
                request.getToAccountType() == Transaction.AccountType.BRANCH) {
            CIFCurrentAccount cifAccount = cifCurrentAccountRepository.findById(request.getFromAccountId())
                    .orElseThrow(() -> new AccountNotFoundException("CIF account not found"));

            // Validate CIF account
            validateCIFSender(cifAccount, request.getAmount());

            // Debit CIF account
            cifAccount.setBalance(cifAccount.getBalance().subtract(request.getAmount()));
            cifCurrentAccountRepository.save(cifAccount);


            BranchCurrentAccount branchCurrentAccount = branchCurrentAccountRepository.findById(request.getToAccountId()).orElseThrow(() -> new AccountNotFoundException("Branch account not found"));
            branchCurrentAccount.setBalance(branchCurrentAccount.getBalance().add(request.getAmount()));
            branchCurrentAccountRepository.save(branchCurrentAccount);
        }else if (request.getFromAccountType() == Transaction.AccountType.BRANCH &&
                request.getToAccountType() == Transaction.AccountType.BRANCH) {

            // Retrieve the source branch account
            BranchCurrentAccount sourceBranchAccount = branchCurrentAccountRepository
                    .findByBranch_Id(request.getFromAccountId())
                    .orElseThrow(() -> new AccountNotFoundException("Source branch account not found"));

            // Validate sufficient funds in the source account (assuming validateBranchSender exists, or you can perform inline validation)
            if (sourceBranchAccount.getBalance().compareTo(request.getAmount()) < 0) {
                throw new ServiceException("Insufficient funds in source branch account");
            }

            // Debit source branch account
            sourceBranchAccount.setBalance(sourceBranchAccount.getBalance().subtract(request.getAmount()));
            branchCurrentAccountRepository.save(sourceBranchAccount);

            // Retrieve the destination branch account
            BranchCurrentAccount destinationBranchAccount = branchCurrentAccountRepository
                    .findById(request.getToAccountId())
                    .orElseThrow(() -> new AccountNotFoundException("Destination branch account not found"));

            // Credit destination branch account
            destinationBranchAccount.setBalance(destinationBranchAccount.getBalance().add(request.getAmount()));
            branchCurrentAccountRepository.save(destinationBranchAccount);
        }


        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setFromAccountId(request.getFromAccountId());
        transaction.setToAccountId(request.getToAccountId());
        transaction.setFromAccountType(request.getFromAccountType());
        transaction.setToAccountType(request.getToAccountType());
        transaction.setAmount(request.getAmount());
        transaction.setPaymentMethod(paymentMethod);
        transaction.setTransactionDate(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }



    @Override
    public Transaction processTransaction(Transaction transaction) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(transaction.getPaymentMethod().getId())
                .orElseThrow(() -> new ServiceException("Payment method not found"));
        transaction.setPaymentMethod(paymentMethod);
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Handle BRANCH to CIF transaction
        if (transaction.getFromAccountType() == Transaction.AccountType.BRANCH && 
            transaction.getToAccountType() == Transaction.AccountType.CIF) {
            CIFCurrentAccount cifAccount = cifCurrentAccountRepository.findById(transaction.getToAccountId())
                    .orElseThrow(() -> new AccountNotFoundException("CIF account not found"));

            // Credit CIF account
            cifAccount.setBalance(cifAccount.getBalance().add(savedTransaction.getAmount()));
            cifCurrentAccountRepository.save(cifAccount);

            // Trigger auto-payment process
            autoPaymentService.processTransaction(savedTransaction);
        }
        // Handle CIF to BRANCH transaction
        else if (transaction.getFromAccountType() == Transaction.AccountType.CIF && 
                 transaction.getToAccountType() == Transaction.AccountType.BRANCH) {
            CIFCurrentAccount cifAccount = cifCurrentAccountRepository.findById(transaction.getFromAccountId())
                    .orElseThrow(() -> new AccountNotFoundException("CIF account not found"));

            // Validate CIF account
            validateCIFSender(cifAccount, savedTransaction.getAmount());

            // Debit CIF account
            cifAccount.setBalance(cifAccount.getBalance().subtract(savedTransaction.getAmount()));
            cifCurrentAccountRepository.save(cifAccount);
        }
        // Handle USER to CIF transaction
        else if (transaction.getFromAccountType() == Transaction.AccountType.USER && 
                 transaction.getToAccountType() == Transaction.AccountType.CIF) {
            UserCurrentAccount userAccount = userCurrentAccountRepository.findById(transaction.getFromAccountId())
                    .orElseThrow(() -> new AccountNotFoundException("User account not found"));
            CIFCurrentAccount cifAccount = cifCurrentAccountRepository.findById(transaction.getToAccountId())
                    .orElseThrow(() -> new AccountNotFoundException("CIF account not found"));

            // Validate user account
            validateUserSender(userAccount, savedTransaction.getAmount());

            // Debit user account
            userAccount.setBalance(userAccount.getBalance().subtract(savedTransaction.getAmount()));
            userCurrentAccountRepository.save(userAccount);

            // Credit CIF account
            cifAccount.setBalance(cifAccount.getBalance().add(savedTransaction.getAmount()));
            cifCurrentAccountRepository.save(cifAccount);

            // Trigger auto-payment process
            autoPaymentService.processTransaction(savedTransaction);
        }
        // Handle CIF to USER transaction
        else if (transaction.getFromAccountType() == Transaction.AccountType.CIF && 
                 transaction.getToAccountType() == Transaction.AccountType.USER) {
            CIFCurrentAccount cifAccount = cifCurrentAccountRepository.findById(transaction.getFromAccountId())
                    .orElseThrow(() -> new AccountNotFoundException("CIF account not found"));
            UserCurrentAccount userAccount = userCurrentAccountRepository.findById(transaction.getToAccountId())
                    .orElseThrow(() -> new AccountNotFoundException("User account not found"));

            // Validate CIF account
            validateCIFSender(cifAccount, savedTransaction.getAmount());

            // Debit CIF account
            cifAccount.setBalance(cifAccount.getBalance().subtract(savedTransaction.getAmount()));
            cifCurrentAccountRepository.save(cifAccount);

            // Credit user account
            userAccount.setBalance(userAccount.getBalance().add(savedTransaction.getAmount()));
            userCurrentAccountRepository.save(userAccount);
        }

        return savedTransaction;
    }

    @Override
    public List<TransactionHistoryResponse> getTransactionHistory(String accountType, int accountId) {
        List<Transaction> transactions = new ArrayList<>();

        if ("user".equalsIgnoreCase(accountType)) {
            // Get user account ID from user ID
            UserCurrentAccount userAccount = userCurrentAccountRepository.findByUserId(accountId)
                    .orElseThrow(() -> new AccountNotFoundException("User account not found"));

            // Get all transactions involving this user account
            transactions.addAll(transactionRepository
                    .findByFromAccountIdAndFromAccountType(userAccount.getId(), Transaction.AccountType.USER));
            transactions.addAll(transactionRepository
                    .findByToAccountIdAndToAccountType(userAccount.getId(), Transaction.AccountType.USER));

        } else if ("cif".equalsIgnoreCase(accountType)) {
            // Get CIF account ID from CIF ID
            CIFCurrentAccount cifAccount = cifCurrentAccountRepository.findByCifId(accountId)
                    .orElseThrow(() -> new AccountNotFoundException("CIF account not found"));

            // Get all transactions involving this CIF account
            transactions.addAll(transactionRepository
                    .findByFromAccountIdAndFromAccountType(cifAccount.getId(), Transaction.AccountType.CIF));
            transactions.addAll(transactionRepository
                    .findByToAccountIdAndToAccountType(cifAccount.getId(), Transaction.AccountType.CIF));

        } else {
            throw new IllegalArgumentException("Invalid account type. Use 'user' or 'cif'.");
        }

        return transactions.stream()
                .map(this::mapToHistoryResponse)
                .sorted(Comparator.comparing(TransactionHistoryResponse::getDate).reversed())
                .collect(Collectors.toList());
    }


    private TransactionHistoryResponse mapToHistoryResponse(Transaction transaction) {
        TransactionHistoryResponse response = new TransactionHistoryResponse();
        response.setDate(transaction.getTransactionDate());
        response.setAmount(transaction.getAmount());

        // Determine transaction type and counterparty
        if (transaction.getFromAccountType() == Transaction.AccountType.USER) {
            response.setType("Debit");
            CIFCurrentAccount cifAccount = cifCurrentAccountRepository.findById(transaction.getToAccountId())
                    .orElseThrow(() -> new AccountNotFoundException("CIF account not found"));
            response.setCounterparty(cifAccount.getAccCode());
        } else if (transaction.getToAccountType() == Transaction.AccountType.USER) {
            response.setType("Credit");
            UserCurrentAccount userAccount = userCurrentAccountRepository.findById(transaction.getFromAccountId())
                    .orElseThrow(() -> new AccountNotFoundException("User  account not found"));
            response.setCounterparty(userAccount.getAccCode());
        } else if (transaction.getFromAccountType() == Transaction.AccountType.CIF) {
            response.setType("Debit");
            UserCurrentAccount userAccount = userCurrentAccountRepository.findById(transaction.getToAccountId())
                    .orElseThrow(() -> new AccountNotFoundException("User  account not found"));
            response.setCounterparty(userAccount.getAccCode());
        } else {
            response.setType("Credit");
            CIFCurrentAccount cifAccount = cifCurrentAccountRepository.findById(transaction.getFromAccountId())
                    .orElseThrow(() -> new AccountNotFoundException("CIF account not found"));
            response.setCounterparty(cifAccount.getAccCode());
        }
        response.setPaymentMethod(transaction.getPaymentMethod().getPaymentType());
        return response;
    }

    private void validateUserSender(UserCurrentAccount userAccount, BigDecimal amount) {
        if (userAccount.getIsFreeze() == ConstraintEnum.IS_FREEZE.getCode()) {
            throw new ServiceException("User account is frozen");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("Transfer amount must be positive");
        }
        if (userAccount.getBalance().compareTo(amount) < 0) {
            throw new ServiceException("Insufficient balance in user account");
        }
    }

    private void validateCIFSender(CIFCurrentAccount cifAccount, BigDecimal amount) {
        if (cifAccount.getIsFreeze() == ConstraintEnum.IS_FREEZE.getCode()) {
            throw new ServiceException("CIF account is frozen");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("Transfer amount must be positive");
        }
//        if (amount.compareTo(cifAccount.getMaxAmount()) > 0) {
//            throw new ServiceException("Transfer amount cannot exceed " + cifAccount.getMaxAmount());
//        }
        BigDecimal newBalance = cifAccount.getBalance().subtract(amount);
        if (newBalance.compareTo(cifAccount.getMinAmount()) < 0) {
            throw new ServiceException("Transfer would reduce balance below minimum required " + cifAccount.getMinAmount());
        }
    }
}



