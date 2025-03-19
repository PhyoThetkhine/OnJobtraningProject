package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.Service.TransactionService;
import com.prj.LoneHPManagement.model.entity.*;
import com.prj.LoneHPManagement.model.repo.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HPAutoPaymentServiceImpl {
    @Autowired
    private CIFCurrentAccountRepository cifCurrentAccountRepository;
    @Autowired
    private HpLoanRepository hpLoanRepository;
    @Autowired
    private HpTermRepository hpTermRepository;
    @Autowired
    private HpLoanHistoryRepository hpLoanHistoryRepository;
    @Autowired
    private SMELoanHistoryRepository smeLoanHistoryRepository;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private BranchCurrentAccountRepository branchCurrentAccountRepository;
    // ... other repositories

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void processAutoPayments() {
        List<CIFCurrentAccount> accountsWithBalance = cifCurrentAccountRepository.findAll().stream()
                .filter(acc -> acc.getBalance().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());

        for (CIFCurrentAccount cifAccount : accountsWithBalance) {
            processAccountPayments(cifAccount);
        }
    }
    private void processAccountPayments(CIFCurrentAccount cifAccount) {
        // Get all under schedule SME loans for this CIF
        List<HpLoan> activeLoans = hpLoanRepository.findByCifIdAndStatus(
                cifAccount.getCif().getId(),
                ConstraintEnum.UNDER_SCHEDULE.getCode()
        );

        for (HpLoan loan : activeLoans) {
            if (getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) <= 0) {
                break; // Stop if no more funds available
            }
            processLoanPayment(loan, cifAccount);
        }
    }

    private void processLoanPayment(HpLoan loan, CIFCurrentAccount cifAccount) {
        List<HpTerm> allTerms = hpTermRepository.findByHpLoan_Id(loan.getId());

        // Get both past due and grace period terms
        List<HpTerm> termsToProcess = allTerms.stream()
                .filter(term -> term.getStatus() == ConstraintEnum.PAST_DUE.getCode()
                        || term.getStatus() == ConstraintEnum.GRACE_PERIOD.getCode())
                .collect(Collectors.toList());

        if (termsToProcess.isEmpty()) {
            return;
        }

        // Find max late days (considering both interest and principal late days)
        long maxLateDays = termsToProcess.stream()
                .filter(term -> term.getStatus() == ConstraintEnum.PAST_DUE.getCode())
                .mapToLong(term -> Math.max(
                        term.getInterestLateDays(),
                        term.getLatePrincipalDays()
                ))
                .max()
                .orElse(0);

        if (maxLateDays > 180) {
            processLongTermOverdue(loan, allTerms, cifAccount);
        } else if (maxLateDays > 90) {
            processDefaultedLoan(loan, allTerms, cifAccount);
        } else {
            processNormalOverdue(loan, termsToProcess, cifAccount);
        }
    }

    private void processNormalOverdue(HpLoan loan, List<HpTerm> termsToProcess, CIFCurrentAccount cifAccount) {
        // Sort terms by due date
        termsToProcess.sort((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate()));

        // First pass: Process late fees for all terms
        for (HpTerm term : termsToProcess) {
            if (getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) <= 0) {
                return;
            }
            processTermLateFees(term, cifAccount);
        }

        // Second pass: Process IOD and POD for all terms
        for (HpTerm term : termsToProcess) {
            if (getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) <= 0) {
                return;
            }
            processTermIOD(term, cifAccount);
            processTermPOD(term, cifAccount);
        }

        // Third pass: Process interest and principal for all terms
        for (HpTerm term : termsToProcess) {
            if (getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) <= 0) {
                return;
            }
            processTermInterest(term, cifAccount);
            processTermPrincipal(term, cifAccount);
            updateTermStatus(term);
        }
    }

    private void processDefaultedLoan(HpLoan loan, List<HpTerm> allTerms, CIFCurrentAccount cifAccount) {
        // Calculate total outstanding including principal and POD for each term
        BigDecimal totalOutstanding = calculateTotalOutstandingWithLateFees(allTerms);
        
        // Get the maximum late days
        long maxLateDays = Math.max(
            getMaxInterestLateDays(allTerms),
            getMaxPrincipalLateDays(allTerms)
        );
        
        // Calculate and process default late fee
        BigDecimal defaultLateFee = calculateLateFee(totalOutstanding, BigDecimal.valueOf(loan.getDefaultedRate()))
                .multiply(BigDecimal.valueOf(maxLateDays));
        
        if (processLateFeePayment(defaultLateFee, cifAccount)) {
            // Reset late days after default late fee payment
            resetLateDaysForAllTerms(allTerms);
        }

        if (cifAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            List<HpTerm> termsToProcess = getTermsToProcess(allTerms);
            
            // First: Process late fees for all terms
            for (HpTerm term : termsToProcess) {
                if (getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) <= 0) {
                    return;
                }
                processTermLateFees(term, cifAccount);
            }

            // Second: Process IOD and POD for all terms
            for (HpTerm term : termsToProcess) {
                if (getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) <= 0) {
                    return;
                }
                processTermIOD(term, cifAccount);
                processTermPOD(term, cifAccount);
            }

            // Third: Process interest and principal for all terms except last
            HpTerm lastTerm = findLastTerm(termsToProcess);
            for (HpTerm term : termsToProcess) {
                if (getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) <= 0) {
                    return;
                }
                if (!term.equals(lastTerm)) {
                    processTermInterest(term, cifAccount);
                    processTermPrincipal(term, cifAccount);
                    updateTermStatus(term);
                }
            }

            // Finally: Process last term with forced principal payment
            if (lastTerm != null && getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) > 0) {
                processTermInterest(lastTerm, cifAccount);
                processLastTermPrincipal(lastTerm, cifAccount, loan);
                updateTermStatus(lastTerm);
            }

            // Check if loan is complete
            if (isAllTermsPaid(allTerms)) {
                loan.setStatus(ConstraintEnum.PAID_OFF.getCode());
                hpLoanRepository.save(loan);
            }
        }
    }

    private void processLongTermOverdue(HpLoan loan, List<HpTerm> allTerms, CIFCurrentAccount cifAccount) {
        // Similar structure to processDefaultedLoan but with long term rate
        BigDecimal totalOutstanding = calculateTotalOutstandingWithLateFees(allTerms);
        
        long maxLateDays = Math.max(
            getMaxInterestLateDays(allTerms),
            getMaxPrincipalLateDays(allTerms)
        );
        
        BigDecimal longTermLateFee = calculateLateFee(totalOutstanding, BigDecimal.valueOf(loan.getLongTermOverdueRate()))
                .multiply(BigDecimal.valueOf(maxLateDays));
        
        if (processLateFeePayment(longTermLateFee, cifAccount)) {
            resetLateDaysForAllTerms(allTerms);
        }

        if (cifAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            List<HpTerm> termsToProcess = getTermsToProcess(allTerms);
            
            // Process payments in the same order as defaulted loan
            // First: Late fees for all terms
            for (HpTerm term : termsToProcess) {
                if (getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) <= 0) {
                    return;
                }
                processTermLateFees(term, cifAccount);
            }

            // Second: IOD and POD for all terms
            for (HpTerm term : termsToProcess) {
                if (getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) <= 0) {
                    return;
                }
                processTermIOD(term, cifAccount);
                processTermPOD(term, cifAccount);
            }

            // Third: Interest and principal for all terms except last
            HpTerm lastTerm = findLastTerm(termsToProcess);
            for (HpTerm term : termsToProcess) {
                if (getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) <= 0) {
                    return;
                }
                if (!term.equals(lastTerm)) {
                    processTermInterest(term, cifAccount);
                    processTermPrincipal(term, cifAccount);
                    updateTermStatus(term);
                }
            }

            // Finally: Process last term
            if (lastTerm != null && getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) > 0) {
                processTermInterest(lastTerm, cifAccount);
                processLastTermPrincipal(lastTerm, cifAccount, loan);
                updateTermStatus(lastTerm);
            }

            if (isAllTermsPaid(allTerms)) {
                loan.setStatus(ConstraintEnum.PAID_OFF.getCode());
                hpLoanRepository.save(loan);
            }
        }
    }

    // Helper methods
    private List<HpTerm> getTermsToProcess(List<HpTerm> allTerms) {
        return allTerms.stream()
                .filter(term -> term.getStatus() == ConstraintEnum.PAST_DUE.getCode()
                        || term.getStatus() == ConstraintEnum.GRACE_PERIOD.getCode())
                .sorted((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate()))
                .collect(Collectors.toList());
    }

    private HpTerm findLastTerm(List<HpTerm> terms) {
        return terms.stream()
                .max((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate()))
                .orElse(null);
    }

    private void resetLateDaysForAllTerms(List<HpTerm> terms) {
        for (HpTerm term : terms) {
            if (term.getInterestLateDays() > 0 || term.getLatePrincipalDays() > 0) {
                term.setInterestLateDays(0);
                term.setLatePrincipalDays(0);
                hpTermRepository.save(term);
            }
        }
    }

    private void processTermLateFees(HpTerm term, CIFCurrentAccount cifAccount) {
        BigDecimal totalAvailable = getTotalAvailableAmount(cifAccount);
        BigDecimal totalPaid = BigDecimal.ZERO;

        // Process interest late fee payment
        if (term.getInterestLateFee().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal interestLateFee = term.getInterestLateFee();
            if (totalAvailable.compareTo(interestLateFee) >= 0) {
                // Full payment
                totalAvailable = totalAvailable.subtract(interestLateFee);
                term.setInterestLateFee(BigDecimal.ZERO);
                totalPaid = totalPaid.add(interestLateFee);
            } else {
                // Move all available amount to hold
                cifAccount.setHoldAmount(cifAccount.getHoldAmount().add(totalAvailable));
                cifAccount.setBalance(BigDecimal.ZERO);
                cifCurrentAccountRepository.save(cifAccount);
                return;
            }
        }

        // Process principal late fee payment
        if (term.getPrincipalLateFee().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal principalLateFee = term.getPrincipalLateFee();
            if (totalAvailable.compareTo(principalLateFee) >= 0) {
                // Full payment
                totalAvailable = totalAvailable.subtract(principalLateFee);
                term.setPrincipalLateFee(BigDecimal.ZERO);
                totalPaid = totalPaid.add(principalLateFee);
            } else {
                // Move all available amount to hold
                cifAccount.setHoldAmount(cifAccount.getHoldAmount().add(totalAvailable));
                cifAccount.setBalance(BigDecimal.ZERO);
                cifCurrentAccountRepository.save(cifAccount);
                return;
            }
        }

        if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
            updateAccountBalances(cifAccount, totalPaid);
            createPaymentHistory(term, totalPaid);
            returnMoneyToBranch(cifAccount, totalPaid, term.getHpLoan());
            hpTermRepository.save(term);
        }
    }

    private void processTermIOD(HpTerm term, CIFCurrentAccount cifAccount) {
        BigDecimal totalAvailable = getTotalAvailableAmount(cifAccount);
        BigDecimal totalPaid = BigDecimal.ZERO;
        
        if (term.getInterestOfOverdue().compareTo(BigDecimal.ZERO) > 0 && 
            totalAvailable.compareTo(BigDecimal.ZERO) > 0) {
            // Pay as much as possible
            BigDecimal iodPaid = processPayment(term.getInterestOfOverdue(), totalAvailable);
            term.setInterestOfOverdue(term.getInterestOfOverdue().subtract(iodPaid));
            totalPaid = totalPaid.add(iodPaid);
            
            updateTermOutstanding(term);
            
            if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
                updateAccountBalances(cifAccount, totalPaid);
                createPaymentHistory(term, totalPaid);
                returnMoneyToBranch(cifAccount, totalPaid, term.getHpLoan());
                hpTermRepository.save(term);
            }
        }
    }

    private void processTermInterest(HpTerm term, CIFCurrentAccount cifAccount) {
        BigDecimal totalAvailable = getTotalAvailableAmount(cifAccount);
        BigDecimal totalPaid = BigDecimal.ZERO;
        
        if (term.getInterest().compareTo(BigDecimal.ZERO) > 0 && 
            totalAvailable.compareTo(BigDecimal.ZERO) > 0) {
            // Pay as much as possible
            BigDecimal interestPaid = processPayment(term.getInterest(), totalAvailable);
            term.setInterest(term.getInterest().subtract(interestPaid));
            totalPaid = totalPaid.add(interestPaid);
            
            updateTermOutstanding(term);
            
            if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
                updateAccountBalances(cifAccount, totalPaid);
                createPaymentHistory(term, totalPaid);
                returnMoneyToBranch(cifAccount, totalPaid, term.getHpLoan());
                hpTermRepository.save(term);
            }
        }
    }

    private void processTermPrincipal(HpTerm term, CIFCurrentAccount cifAccount) {
        BigDecimal totalAvailable = getTotalAvailableAmount(cifAccount);
        BigDecimal totalPaid = BigDecimal.ZERO;

        if (term.getPrincipal().compareTo(BigDecimal.ZERO) > 0 &&
                totalAvailable.compareTo(BigDecimal.ZERO) > 0) {
            // Pay as much as possible
            BigDecimal principalPaid = processPayment(term.getPrincipal(), totalAvailable);
            term.setPrincipal(term.getPrincipal().subtract(principalPaid));
            totalPaid = totalPaid.add(principalPaid);

            updateTermOutstanding(term);

            if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
                updateAccountBalances(cifAccount, totalPaid);
                createPaymentHistory(term, totalPaid);
                returnMoneyToBranch(cifAccount, totalPaid, term.getHpLoan());
                hpTermRepository.save(term);
            }
        }
    }

    private void processLastTermPrincipal(HpTerm lastTerm, CIFCurrentAccount cifAccount, HpLoan loan) {
        // Simply process the principal payment with available balance
        BigDecimal available = getTotalAvailableAmount(cifAccount);
        if (available.compareTo(BigDecimal.ZERO) > 0) {
            processTermPrincipal(lastTerm, cifAccount);
        }
    }

    private void updateAccountBalances(CIFCurrentAccount cifAccount, BigDecimal amountPaid) {
        BigDecimal remainingAmount = amountPaid;
        BigDecimal holdAmount = cifAccount.getHoldAmount();
        
        if (holdAmount.compareTo(BigDecimal.ZERO) > 0) {
            if (holdAmount.compareTo(remainingAmount) >= 0) {
                cifAccount.setHoldAmount(holdAmount.subtract(remainingAmount));
                remainingAmount = BigDecimal.ZERO;
            } else {
                remainingAmount = remainingAmount.subtract(holdAmount);
                cifAccount.setHoldAmount(BigDecimal.ZERO);
            }
        }
        
        if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
            cifAccount.setBalance(cifAccount.getBalance().subtract(remainingAmount));
        }
        
        cifCurrentAccountRepository.save(cifAccount);
    }

    private void returnMoneyToBranch(CIFCurrentAccount cifAccount, BigDecimal amount, HpLoan loan) {
        try {
            String cifCode = loan.getCif().getCifCode();
            String branchCode = extractBranchCode(cifCode);
            
            Transaction transaction = new Transaction();
            transaction.setFromAccountId(cifAccount.getId());
            transaction.setFromAccountType(Transaction.AccountType.CIF);
            transaction.setToAccountType(Transaction.AccountType.BRANCH);
            transaction.setAmount(amount);
            
            int branchAccountId = getBranchAccountIdByCode(branchCode);
            transaction.setToAccountId(branchAccountId);
            
            transaction.setPaymentMethod(paymentMethodRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Payment Method not found")));
            
          //  transactionService.processTransaction(transaction);
        } catch (Exception e) {
            System.err.println("Failed to return money to branch: " + e.getMessage());
        }
    }

    private String extractBranchCode(String cifCode) {
        return cifCode.substring(0, 3);
    }

    private int getBranchAccountIdByCode(String branchCode) {
        return branchCurrentAccountRepository.findByBranchCode(branchCode)
            .orElseThrow(() -> new RuntimeException("Branch account not found for code: " + branchCode))
            .getId();
    }

    private BigDecimal processPayment(BigDecimal amount, BigDecimal availableBalance) {
        return availableBalance.compareTo(amount) >= 0 ? amount : availableBalance;
    }

    private boolean processLateFeePayment(BigDecimal lateFee, CIFCurrentAccount cifAccount) {
        BigDecimal totalAvailable = getTotalAvailableAmount(cifAccount);
        
        if (totalAvailable.compareTo(lateFee) >= 0) {
            // Full payment
            updateAccountBalances(cifAccount, lateFee);
            
//            hpTermRepository.save(cifAccount);
            return true;
        }
        return false;
    }

    private BigDecimal calculateLateFee(BigDecimal amount, BigDecimal rate) {
        return amount.multiply(rate)
                .divide(BigDecimal.valueOf(36500), 2, RoundingMode.HALF_UP);
    }

    private void processRemainingPayments(List<HpTerm> terms, CIFCurrentAccount cifAccount, HpLoan loan) {
        // Filter and sort terms that need processing (PAST_DUE or GRACE_PERIOD)
        List<HpTerm> termsToProcess = terms.stream()
                .filter(term -> term.getStatus() == ConstraintEnum.PAST_DUE.getCode()
                        || term.getStatus() == ConstraintEnum.GRACE_PERIOD.getCode())
                .sorted((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate()))
                .collect(Collectors.toList());

        for (HpTerm term : termsToProcess) {
            if (getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            if (term.getStatus() != ConstraintEnum.PAID_OFF.getCode()) {
                processTermPayment(term, cifAccount, loan);
            }
        }
    }

    private void processTermPayment(HpTerm term, CIFCurrentAccount cifAccount, HpLoan loan) {
        BigDecimal totalAvailable = getTotalAvailableAmount(cifAccount);
        BigDecimal totalPaid = BigDecimal.ZERO;

        // 1. Process late fees first
        if (term.getInterestLateFee().compareTo(BigDecimal.ZERO) > 0 || 
            term.getPrincipalLateFee().compareTo(BigDecimal.ZERO) > 0) {
            processTermLateFees(term, cifAccount);
            totalAvailable = getTotalAvailableAmount(cifAccount);
        }

        // 2. Process IOD
        if (term.getInterestOfOverdue().compareTo(BigDecimal.ZERO) > 0 && 
            totalAvailable.compareTo(BigDecimal.ZERO) > 0) {
            processTermIOD(term, cifAccount);
            totalAvailable = getTotalAvailableAmount(cifAccount);
        }

        // 3. Process POD
        if (term.getPrincipalOfOverdue().compareTo(BigDecimal.ZERO) > 0 && 
            totalAvailable.compareTo(BigDecimal.ZERO) > 0) {
            processTermPOD(term, cifAccount);
            totalAvailable = getTotalAvailableAmount(cifAccount);
        }

        // 4. Process interest
        if (term.getInterest().compareTo(BigDecimal.ZERO) > 0 && 
            totalAvailable.compareTo(BigDecimal.ZERO) > 0) {
            processTermInterest(term, cifAccount);
            totalAvailable = getTotalAvailableAmount(cifAccount);
        }

        // 5. Process principal
        if (term.getPrincipal().compareTo(BigDecimal.ZERO) > 0 && 
            totalAvailable.compareTo(BigDecimal.ZERO) > 0) {
            processTermPrincipal(term, cifAccount);
        }

        // Update term status after all payments
        updateTermStatus(term);
    }

    private void processTermPOD(HpTerm term, CIFCurrentAccount cifAccount) {
        BigDecimal totalAvailable = getTotalAvailableAmount(cifAccount);
        BigDecimal totalPaid = BigDecimal.ZERO;

        if (term.getPrincipalOfOverdue().compareTo(BigDecimal.ZERO) > 0 &&
                totalAvailable.compareTo(BigDecimal.ZERO) > 0) {
            // Pay as much as possible
            BigDecimal podPaid = processPayment(term.getPrincipalOfOverdue(), totalAvailable);
            term.setPrincipalOfOverdue(term.getPrincipalOfOverdue().subtract(podPaid));
            totalPaid = totalPaid.add(podPaid);

            updateTermOutstanding(term);

            if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
                updateAccountBalances(cifAccount, totalPaid);
                createPaymentHistory(term, totalPaid);
                returnMoneyToBranch(cifAccount, totalPaid, term.getHpLoan());
                hpTermRepository.save(term);
            }
        }
    }

    private void updateTermOutstanding(HpTerm term) {
        term.setOutstandingAmount(
                term.getPrincipal()
                        .add(term.getInterest())
                        .add(term.getInterestOfOverdue())
                        .add(term.getPrincipalOfOverdue())
        );
    }

    private BigDecimal calculateTotalOutstanding(List<HpTerm> terms) {
        return terms.stream()
                .map(term -> term.getPrincipal()
                        .add(term.getInterest())
                        .add(term.getInterestOfOverdue())
                        .add(term.getPrincipalOfOverdue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void createPaymentHistory(HpTerm term, BigDecimal totalPaid) {
        HpLoanHistory history = new HpLoanHistory();
        history.setHpTerm(term);
        history.setPaidAmount(totalPaid);
        history.setPaidDate(LocalDateTime.now());
        history.setOutstanding(term.getOutstandingAmount());
        history.setPrincipalPaid(term.getPrincipal());
        history.setInterestPaid(term.getInterest());
        history.setIodPaid(term.getInterestOfOverdue());
//        history.setPodPaid(term.getPrincipalOfOverdue());
//        history.setInterestLateFee(term.getInterestLateFee());
//        history.setPrincipalLateFee(term.getPrincipalLateFee());
        history.setTotalPaid(totalPaid);
        history.setTermStatus(term.getStatus());

        hpLoanHistoryRepository.save(history);
    }

    private BigDecimal getTotalAvailableAmount(CIFCurrentAccount cifAccount) {
        return cifAccount.getBalance().add(cifAccount.getHoldAmount());
    }

    private long getMaxInterestLateDays(List<HpTerm> terms) {
        return terms.stream()
                .filter(term -> term.getStatus() == ConstraintEnum.PAST_DUE.getCode())
                .mapToLong(HpTerm::getInterestLateDays)
                .max()
                .orElse(0);
    }

    private long getMaxPrincipalLateDays(List<HpTerm> terms) {
        return terms.stream()
                .filter(term -> term.getStatus() == ConstraintEnum.PAST_DUE.getCode())
                .mapToLong(HpTerm::getLatePrincipalDays)
                .max()
                .orElse(0);
    }

    private void updateTermStatus(HpTerm term) {
        // Check if term is fully paid
        if (term.getPrincipal().add(term.getInterest())
                .add(term.getInterestOfOverdue())
                .add(term.getPrincipalOfOverdue())
                .add(term.getInterestLateFee())
                .add(term.getPrincipalLateFee())
                .compareTo(BigDecimal.ZERO) == 0) {
            term.setStatus(ConstraintEnum.PAID_OFF.getCode());
            
            // If this was the last term and it's fully paid, update loan status
            if (isLastTerm(term)) {
                HpLoan loan = term.getHpLoan();
                loan.setStatus(ConstraintEnum.PAID_OFF.getCode());
                hpLoanRepository.save(loan);
            }
            hpTermRepository.save(term);
        }
    }

    private boolean isLastTerm(HpTerm currentTerm) {
        // Get all terms for this loan
        List<HpTerm> allTerms = hpTermRepository.findByHpLoan_Id(currentTerm.getHpLoan().getId());
        
        // Find the term with the latest due date
        HpTerm lastTerm = allTerms.stream()
                .max((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate()))
                .orElse(null);
        
        // Check if current term is the last term
        return lastTerm != null && lastTerm.getId() == currentTerm.getId();
    }

    private BigDecimal calculateTotalOutstandingWithLateFees(List<HpTerm> terms) {
        return terms.stream()
                .map(term -> term.getPrincipal()
                        .add(term.getInterest())
                        .add(term.getInterestOfOverdue())
                        .add(term.getPrincipalOfOverdue())
                        .add(term.getInterestLateFee())
                        .add(term.getPrincipalLateFee()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isAllTermsPaid(List<HpTerm> terms) {
        return terms.stream().allMatch(term -> 
            term.getPrincipal().add(term.getInterest())
                .add(term.getInterestOfOverdue())
                .add(term.getPrincipalOfOverdue())
                .add(term.getInterestLateFee())
                .add(term.getPrincipalLateFee())
                .compareTo(BigDecimal.ZERO) == 0);
    }
}
