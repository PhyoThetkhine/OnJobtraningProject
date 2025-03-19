package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByFromAccountIdAndFromAccountType(int fromAccountId, Transaction.AccountType type);
    List<Transaction> findByToAccountIdAndToAccountType(int toAccountId, Transaction.AccountType type);
    @Query("SELECT t FROM Transaction t WHERE " +
            "(t.fromAccountType = 'USER' AND t.fromAccountId = :userId) OR " +
            "(t.toAccountType = 'USER' AND t.toAccountId = :userId)")
    Page<Transaction> findTransactionsByUserId(
            @Param("userId") int userId,
            Pageable pageable
    );

    @Query("SELECT t FROM Transaction t WHERE " +
            "(t.fromAccountType = 'CIF' AND t.fromAccountId = :cifId) OR " +
            "(t.toAccountType = 'CIF' AND t.toAccountId = :cifId)")
    Page<Transaction> findTransactionByCifId(@Param("cifId") int cifId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE " +
            "(t.fromAccountType = 'BRANCH' AND t.fromAccountId = :branchId) OR " +
            "(t.toAccountType = 'BRANCH' AND t.toAccountId = :branchId)")
    Page<Transaction> findTransactionByBranchId(int branchId, Pageable pageable);


    Page<Transaction> findByFromAccountIdOrToAccountId(int fromAccountId, int toAccountId, Pageable pageable);


}
