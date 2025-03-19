package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.Service.SMEAutoPaymentService;
import com.prj.LoneHPManagement.Service.TransactionService;
import com.prj.LoneHPManagement.model.entity.*;
import com.prj.LoneHPManagement.model.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SMEAutoPaymentServiceImpl implements SMEAutoPaymentService {

    @Autowired
    private CIFCurrentAccountRepository cifCurrentAccountRepository;

    @Autowired
    private SMELoanRepository smeLoanRepository;

    @Autowired
    private SMETermRepository smeTermRepository;

    @Autowired
    private SMELoanHistoryRepository smeLoanHistoryRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

//    @Autowired
//    private TransactionService transactionService;

    @Autowired
    private BranchCurrentAccountRepository branchCurrentAccountRepository;

    @Scheduled(cron = "0 0 0 * * *") // Run at midnight every day
    @Transactional
    @Override
    public void processAutoPayments() {
        // Get all CIF accounts with balance
        List<CIFCurrentAccount> accountsWithBalance = cifCurrentAccountRepository.findAll().stream()
                .filter(acc -> acc.getBalance().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());

        for (CIFCurrentAccount cifAccount : accountsWithBalance) {
            processAccountPayments(cifAccount);
        }
    }

    private void processAccountPayments(CIFCurrentAccount cifAccount) {
        // Get all under schedule SME loans for this CIF
        List<SMELoan> activeLoans = smeLoanRepository.findByCifIdAndStatus(
                cifAccount.getCif().getId(),
                ConstraintEnum.UNDER_SCHEDULE.getCode()
        );

        for (SMELoan loan : activeLoans) {
            if (getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) <= 0) {
                break; // Stop if no more funds available
            }
            processLoanPayment(loan, cifAccount);
        }
    }

    private void processLoanPayment(SMELoan loan, CIFCurrentAccount cifAccount) {
        List<SMETerm> allTerms = smeTermRepository.findBySmeLoan_Id(loan.getId());
        
        // Get both past due and grace period terms
        List<SMETerm> termsToProcess = allTerms.stream()
                .filter(term -> term.getStatus() == ConstraintEnum.PAST_DUE.getCode() 
                        || term.getStatus() == ConstraintEnum.GRACE_PERIOD.getCode())
                .collect(Collectors.toList());

        if (termsToProcess.isEmpty()) {
            return; // No terms to process
        }

        // Find the maximum late days among past due terms only
        long maxLateDays = termsToProcess.stream()
                .filter(term -> term.getStatus() == ConstraintEnum.PAST_DUE.getCode())
                .mapToLong(term -> ChronoUnit.DAYS.between(
                        term.getDueDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime(),
                        LocalDateTime.now()
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

    private void processLongTermOverdue(SMELoan loan, List<SMETerm> allTerms, CIFCurrentAccount cifAccount) {
        // Calculate total outstanding for late terms
        BigDecimal totalOutstanding = calculateTotalOutstanding(allTerms);
        
        // Get the maximum late days for calculation
        long maxLateDays = getMaxLateDays(allTerms);
        
        // Calculate late fee using longTermOverdueRate for each late day
        BigDecimal lateFee = calculateLateFee(totalOutstanding, BigDecimal.valueOf(loan.getLongTermOverdueRate()))
                .multiply(BigDecimal.valueOf(maxLateDays));

        // Process late fee payment
        if (processLateFeePayment(lateFee, cifAccount)) {
            // Reset late days for all terms after successful payment
            for (SMETerm term : allTerms) {
                if (term.getInterestLateDays() > 0) {
                    term.setInterestLateDays(0);
                    smeTermRepository.save(term);
                }
            }
        }

        if (cifAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            processRemainingPayments(allTerms, cifAccount, loan);
            // Force principal payment for the last term
            SMETerm lastTerm = allTerms.stream()
                    .filter(term -> term.getStatus() == ConstraintEnum.PAST_DUE.getCode()
                            || term.getStatus() == ConstraintEnum.GRACE_PERIOD.getCode())
                    .max(Comparator.comparing(SMETerm::getDueDate))
                    .orElse(null);

            if (lastTerm != null) {
                processLastTermPrincipal(lastTerm, cifAccount, loan);
            }
        }
    }

    private void processDefaultedLoan(SMELoan loan, List<SMETerm> allTerms, CIFCurrentAccount cifAccount) {
        // Calculate total outstanding (Interest + IOD for each term + Principal once)
        BigDecimal totalOutstanding = calculateTotalOutstanding(allTerms);
        
        // Get the maximum late days for calculation
        long maxLateDays = getMaxLateDays(allTerms);
        
        // Calculate late fee using defaulted rate for each late day
        BigDecimal lateFee = calculateLateFee(totalOutstanding, BigDecimal.valueOf(loan.getDefaultedRate()))
                .multiply(BigDecimal.valueOf(maxLateDays));

        // Process late fee payment
        if (processLateFeePayment(lateFee, cifAccount)) {
            // Reset late days for all terms after successful payment
            resetLateDaysForAllTerms(allTerms);
        }

        if (cifAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            List<SMETerm> termsToProcess = getTermsToProcess(allTerms);
            
            // First: Process late fees for all terms
            for (SMETerm term : termsToProcess) {
                if (getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) <= 0) {
                    return;
                }
                processTermLateFees(term, cifAccount);
            }

            // Second: Process all IOD
            for (SMETerm term : termsToProcess) {
                if (getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) <= 0) {
                    return;
                }
                processTermIOD(term, cifAccount);
            }

            // Third: Process all interest
            for (SMETerm term : termsToProcess) {
                if (getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) <= 0) {
                    return;
                }
                processTermInterest(term, cifAccount);
            }

            // Finally: Process principal for last term if allowed
            if (loan.getPaidPrincipalStatus() == ConstraintEnum.PAID.getCode() && !termsToProcess.isEmpty()) {
                SMETerm lastTerm = findLastTerm(termsToProcess);
                if (lastTerm != null) {
                    processTermPrincipal(lastTerm, cifAccount, loan);
                }
            }

            // Update term statuses
            for (SMETerm term : termsToProcess) {
                updateTermStatus(term, loan);
            }
        }
    }

    private void resetLateDaysForAllTerms(List<SMETerm> terms) {
        for (SMETerm term : terms) {
            if (term.getInterestLateDays() > 0) {
                term.setInterestLateDays(0);
                smeTermRepository.save(term);
            }
        }
    }

    private void processNormalOverdue(SMELoan loan, List<SMETerm> termsToProcess, CIFCurrentAccount cifAccount) {
        // Sort terms by due date to ensure proper order
        termsToProcess.sort((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate()));

        // First pass: Process all late fees
        for (SMETerm term : termsToProcess) {
            if (getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) <= 0) {
                return;
            }
            processTermLateFees(term, cifAccount);
        }

        // Second pass: Process all IOD
        for (SMETerm term : termsToProcess) {
            if (getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) <= 0) {
                return;
            }
            processTermIOD(term, cifAccount);
        }

        // Third pass: Process all interest
        for (SMETerm term : termsToProcess) {
            if (getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) <= 0) {
                return;
            }
            processTermInterest(term, cifAccount);
        }

        // Fourth pass: Process principal if allowed
        if (loan.getPaidPrincipalStatus() == ConstraintEnum.PAID.getCode() && !termsToProcess.isEmpty()) {
            SMETerm lastTerm = termsToProcess.get(termsToProcess.size() - 1);
            if (isLastTerm(lastTerm, loan)) {
                processTermPrincipal(lastTerm, cifAccount, loan);
            }
        }

        // Update term statuses
        for (SMETerm term : termsToProcess) {
            updateTermStatus(term, loan);
        }
    }

    private void processTermLateFees(SMETerm term, CIFCurrentAccount cifAccount) {
        if (term.getInterestLateDays() <= 0) {
            return;
        }

        BigDecimal lateFee = calculateLateFee(term.getInterestOfOverdue(), BigDecimal.valueOf(term.getSmeLoan().getInterestRate()))
                .multiply(BigDecimal.valueOf(term.getInterestLateDays()));
        
        BigDecimal availableBalance = getTotalAvailableAmount(cifAccount);
        if (availableBalance.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        
        if (availableBalance.compareTo(lateFee) < 0) {
            // Not enough for full late fee, put all in hold
            cifAccount.setHoldAmount(cifAccount.getHoldAmount().add(availableBalance));
            cifAccount.setBalance(BigDecimal.ZERO);
            cifCurrentAccountRepository.save(cifAccount);
            return;
        }
        
        // Pay late fee
        updateAccountBalances(cifAccount, lateFee);
        returnMoneyToBranch(cifAccount, lateFee, term.getSmeLoan());
        
        // Reset late days after successful payment
        term.setInterestLateDays(0);
        smeTermRepository.save(term);
    }

    private void processTermIOD(SMETerm term, CIFCurrentAccount cifAccount) {
        BigDecimal totalAvailable = getTotalAvailableAmount(cifAccount);
        BigDecimal totalPaid = BigDecimal.ZERO;
        
        // Pay IOD (as much as possible)
        if (term.getInterestOfOverdue().compareTo(BigDecimal.ZERO) > 0 && totalAvailable.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal iodPaid = processPayment(term.getInterestOfOverdue(), totalAvailable);
            totalAvailable = totalAvailable.subtract(iodPaid);
            term.setInterestOfOverdue(term.getInterestOfOverdue().subtract(iodPaid));
            totalPaid = totalPaid.add(iodPaid);
            
            // Recalculate term's outstanding amount
            term.setOutstandingAmount(term.getInterest().add(term.getInterestOfOverdue()));
            
            // Update account balances and save term
            if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
                updateAccountBalances(cifAccount, getTotalAvailableAmount(cifAccount).subtract(totalAvailable));
                createPaymentHistory(term, totalPaid);
                SMELoan loan = term.getSmeLoan();
                returnMoneyToBranch(cifAccount, totalPaid, loan);
                smeTermRepository.save(term);
            }
        }
    }

    private void processTermInterest(SMETerm term, CIFCurrentAccount cifAccount) {
        BigDecimal totalAvailable = getTotalAvailableAmount(cifAccount);
        BigDecimal totalPaid = BigDecimal.ZERO;
        
        // Pay interest (as much as possible)
        if (term.getInterest().compareTo(BigDecimal.ZERO) > 0 && totalAvailable.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal interestPaid = processPayment(term.getInterest(), totalAvailable);
            totalAvailable = totalAvailable.subtract(interestPaid);
            term.setInterest(term.getInterest().subtract(interestPaid));
            totalPaid = totalPaid.add(interestPaid);
            
            // Recalculate term's outstanding amount
            term.setOutstandingAmount(term.getInterest().add(term.getInterestOfOverdue()));
            
            // Update account balances and save term
            if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
                updateAccountBalances(cifAccount, getTotalAvailableAmount(cifAccount).subtract(totalAvailable));
                createPaymentHistory(term, totalPaid);
                SMELoan loan = term.getSmeLoan();
                returnMoneyToBranch(cifAccount, totalPaid, loan);
                smeTermRepository.save(term);
            }
        }
    }

    private void processTermPrincipal(SMETerm term, CIFCurrentAccount cifAccount, SMELoan loan) {
        BigDecimal totalAvailable = getTotalAvailableAmount(cifAccount);
        BigDecimal totalPaid = BigDecimal.ZERO;
        
        // Pay principal (as much as possible)
        if (term.getPrincipal().compareTo(BigDecimal.ZERO) > 0 && totalAvailable.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal principalPaid = processPayment(term.getPrincipal(), totalAvailable);
            totalAvailable = totalAvailable.subtract(principalPaid);
            totalPaid = totalPaid.add(principalPaid);
            
            // Update account balances
            updateAccountBalances(cifAccount, getTotalAvailableAmount(cifAccount).subtract(totalAvailable));
            
            // Create payment history
            createPaymentHistory(term, totalPaid);
            
            // Return money to branch
            returnMoneyToBranch(cifAccount, totalPaid, loan);

            // Get all future terms (after current term)
            List<SMETerm> futureTerms = smeTermRepository.findBySmeLoan_IdAndDueDateAfter(
                loan.getId(),
                term.getDueDate()
            ).stream()
            .sorted((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate()))
            .collect(Collectors.toList());
            
            // Calculate remaining principal
            BigDecimal remainingPrincipal = term.getPrincipal().subtract(principalPaid);
            
            // Clear current term's principal
            term.setPrincipal(BigDecimal.ZERO);
            term.setOutstandingAmount(term.getInterest().add(term.getInterestOfOverdue()));
            smeTermRepository.save(term);

            // Set the same remaining principal for each future term
            for (SMETerm futureTerm : futureTerms) {
                futureTerm.setPrincipal(remainingPrincipal);
                futureTerm.setOutstandingAmount(
                    remainingPrincipal.add(futureTerm.getInterest()).add(futureTerm.getInterestOfOverdue())
                );
                smeTermRepository.save(futureTerm);
            }
        }
    }

    private void updateTermStatus(SMETerm term, SMELoan loan) {
        // Check if term is fully paid
        if (term.getPrincipal().add(term.getInterest()).add(term.getInterestOfOverdue())
                .compareTo(BigDecimal.ZERO) == 0) {
            term.setStatus(ConstraintEnum.PAID_OFF.getCode());
            
            // If this was the last term and it's fully paid, update loan status
            if (isLastTerm(term, loan)) {
                loan.setStatus(ConstraintEnum.PAID_OFF.getCode());
                smeLoanRepository.save(loan);
            }
            smeTermRepository.save(term);
        }
    }

    private BigDecimal calculateTotalOutstanding(List<SMETerm> terms) {
        BigDecimal totalOutstanding = BigDecimal.ZERO;
        for (SMETerm term : terms) {
            // Outstanding = Interest + IOD for each term
            BigDecimal termOutstanding = term.getInterest().add(term.getInterestOfOverdue());
            totalOutstanding = totalOutstanding.add(termOutstanding);
        }
        // Add principal once (as it's shared across terms)
        if (!terms.isEmpty()) {
            totalOutstanding = totalOutstanding.add(terms.get(0).getPrincipal());
        }
        return totalOutstanding;
    }

    private BigDecimal calculateLateFee(BigDecimal totalOutstanding, BigDecimal annualRate) {
        // Convert annual rate to daily rate
        // Daily rate = Annual rate / 365
        BigDecimal dailyRate = annualRate.divide(BigDecimal.valueOf(365), 10, RoundingMode.HALF_UP);
        
        // Calculate late fee based on daily rate
        return totalOutstanding
                .multiply(dailyRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private boolean processLateFeePayment(BigDecimal lateFee, CIFCurrentAccount cifAccount) {
        BigDecimal totalAvailable = getTotalAvailableAmount(cifAccount);
        
        if (totalAvailable.compareTo(lateFee) >= 0) {
            // Full payment
            // First use hold amount if available
            BigDecimal remainingLateFee = lateFee;
            BigDecimal holdAmount = cifAccount.getHoldAmount();
            
            if (holdAmount.compareTo(BigDecimal.ZERO) > 0) {
                if (holdAmount.compareTo(remainingLateFee) >= 0) {
                    cifAccount.setHoldAmount(holdAmount.subtract(remainingLateFee));
                    remainingLateFee = BigDecimal.ZERO;
                } else {
                    remainingLateFee = remainingLateFee.subtract(holdAmount);
                    cifAccount.setHoldAmount(BigDecimal.ZERO);
                }
            }
            
            // Use balance for any remaining amount
            if (remainingLateFee.compareTo(BigDecimal.ZERO) > 0) {
                cifAccount.setBalance(cifAccount.getBalance().subtract(remainingLateFee));
            }
            
            cifCurrentAccountRepository.save(cifAccount);
            return true; // Late fee fully paid
        } else {
            // Partial payment - use all available funds
            cifAccount.setHoldAmount(BigDecimal.ZERO);
            cifAccount.setBalance(BigDecimal.ZERO);
            cifCurrentAccountRepository.save(cifAccount);
            return false; // Late fee not fully paid
        }
    }

    private boolean isLastTerm(SMETerm currentTerm, SMELoan loan) {
        // Get all terms for this loan
        List<SMETerm> allTerms = smeTermRepository.findBySmeLoan_Id(loan.getId());
        
        // Find the term with the latest due date
        SMETerm lastTerm = allTerms.stream()
                .max((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate()))
                .orElse(null);
        
        // Check if current term is the last term
        return lastTerm != null && lastTerm.getId() == currentTerm.getId();
    }

    private BigDecimal processPayment(BigDecimal amount, BigDecimal availableBalance) {
        return availableBalance.compareTo(amount) >= 0 ? amount : availableBalance;
    }

    private void recalculateFutureTerms(SMETerm currentTerm, SMELoan loan) {
        // Get all future terms ordered by due date
        List<SMETerm> futureTerms = smeTermRepository.findBySmeLoan_IdAndDueDateAfter(
                loan.getId(),
                currentTerm.getDueDate()
        );
        
        // If no future terms, nothing to recalculate
        if (futureTerms.isEmpty()) {
            return;
        }

        // Sort terms by due date to ensure proper order
        futureTerms.sort((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate()));
        
        // Get the remaining principal after current term payment
        BigDecimal remainingPrincipal = currentTerm.getPrincipal();
        
        // Recalculate each future term
        for (SMETerm term : futureTerms) {
            // Set the new principal for this term
            term.setPrincipal(remainingPrincipal);
            
            // Recalculate interest based on new principal
            BigDecimal newInterest = recalculateInterest(term, loan, remainingPrincipal);
            term.setInterest(newInterest);
            
            // Update outstanding amount
            term.setOutstandingAmount(remainingPrincipal.add(newInterest).add(term.getInterestOfOverdue()));
            
            // Save the updated term
            smeTermRepository.save(term);
        }
    }

    private BigDecimal recalculateInterest(SMETerm term, SMELoan loan, BigDecimal principal) {
        // Get the frequency from the loan
        SMELoan.FREQUENCY frequency = loan.getFrequency();
        
        // Calculate number of days in the term based on frequency
        int daysInTerm;
        if (frequency == SMELoan.FREQUENCY.MONTHLY) {
            daysInTerm = 30; // Assuming 30 days per month
        } else { // YEARLY
            daysInTerm = 365;
        }
        
        // Convert annual interest rate to the period rate
        BigDecimal interestRate =  new BigDecimal(loan.getInterestRate())
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP) // Convert percentage to decimal
                .divide(BigDecimal.valueOf(365), 10, RoundingMode.HALF_UP) // Get daily rate
                .multiply(BigDecimal.valueOf(daysInTerm)); // Multiply by days in term
        
        // Calculate interest: Principal * Rate
        return principal.multiply(interestRate).setScale(2, RoundingMode.HALF_UP);
    }

    private void createPaymentHistory(SMETerm term, BigDecimal totalPaid) {
        SMELoanHistory history = new SMELoanHistory();
        history.setSmeTerm(term);
        history.setPaidAmount(totalPaid);
        history.setPaidDate(LocalDateTime.now());
        history.setOutstanding(term.getOutstandingAmount());
        history.setPrincipalPaid(term.getPrincipal());
        history.setInterestPaid(term.getInterest());
        history.setIodPaid(term.getInterestOfOverdue());
        history.setTotalPaid(totalPaid);
        history.setTermStatus(term.getStatus());
        
        smeLoanHistoryRepository.save(history);
    }

    private void processRemainingPayments(List<SMETerm> terms, CIFCurrentAccount cifAccount, SMELoan loan) {
        // Filter and sort terms that need processing (PAST_DUE or GRACE_PERIOD)
        List<SMETerm> termsToProcess = terms.stream()
                .filter(term -> term.getStatus() == ConstraintEnum.PAST_DUE.getCode() 
                        || term.getStatus() == ConstraintEnum.GRACE_PERIOD.getCode())
                .sorted((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate()))
                .collect(Collectors.toList());

        for (SMETerm term : termsToProcess) {
            if (getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            if (term.getStatus() != ConstraintEnum.PAID_OFF.getCode()) {
                processTermPayment(term, cifAccount, loan);
            }
        }
    }

    private long getMaxLateDays(List<SMETerm> terms) {
        return terms.stream()
                .filter(term -> term.getStatus() == ConstraintEnum.PAST_DUE.getCode())
                .mapToLong(term -> ChronoUnit.DAYS.between(
                        term.getDueDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime(),
                        LocalDateTime.now()
                ))
                .max()
                .orElse(0);
    }

    private BigDecimal getTotalAvailableAmount(CIFCurrentAccount cifAccount) {
        return cifAccount.getBalance().add(cifAccount.getHoldAmount());
    }

    private void updateAccountBalances(CIFCurrentAccount cifAccount, BigDecimal amountPaid) {
        // First use hold amount if available
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
        
        // Use balance for any remaining amount
        if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
            cifAccount.setBalance(cifAccount.getBalance().subtract(remainingAmount));
        }
        
        cifCurrentAccountRepository.save(cifAccount);
    }

    private void processTermPayment(SMETerm term, CIFCurrentAccount cifAccount, SMELoan loan) {
        BigDecimal totalAvailable = getTotalAvailableAmount(cifAccount);
        BigDecimal totalPaid = BigDecimal.ZERO;
        
        // Process interest payment
        if (term.getInterest().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal interestPaid = processPayment(term.getInterest(), totalAvailable);
            totalAvailable = totalAvailable.subtract(interestPaid);
            term.setInterest(term.getInterest().subtract(interestPaid));
            totalPaid = totalPaid.add(interestPaid);
        }
        
        // Process IOD payment
        if (term.getInterestOfOverdue().compareTo(BigDecimal.ZERO) > 0 && totalAvailable.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal iodPaid = processPayment(term.getInterestOfOverdue(), totalAvailable);
            totalAvailable = totalAvailable.subtract(iodPaid);
            term.setInterestOfOverdue(term.getInterestOfOverdue().subtract(iodPaid));
            totalPaid = totalPaid.add(iodPaid);
        }
        
        // Process principal payment if allowed
        if (loan.getPaidPrincipalStatus() == ConstraintEnum.PAID.getCode() && 
            term.getPrincipal().compareTo(BigDecimal.ZERO) > 0 && 
            totalAvailable.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal principalPaid = processPayment(term.getPrincipal(), totalAvailable);
            totalAvailable = totalAvailable.subtract(principalPaid);
            term.setPrincipal(term.getPrincipal().subtract(principalPaid));
            totalPaid = totalPaid.add(principalPaid);
        }
        
        // If any payment was made
        if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
            // Update CIF account balance
            updateAccountBalances(cifAccount, totalPaid);
            
            // Create payment history
            createPaymentHistory(term, totalPaid);
            
            // Return money to branch
            returnMoneyToBranch(cifAccount, totalPaid, loan);
            
            // Save term
            smeTermRepository.save(term);
        }
    }

    private void returnMoneyToBranch(CIFCurrentAccount cifAccount, BigDecimal amount, SMELoan loan) {
        try {
            // Extract branch code from CIF code
            String cifCode = loan.getCif().getCifCode();
            String branchCode = extractBranchCode(cifCode);
            
            // Create transaction to branch
            Transaction transaction = new Transaction();
            transaction.setFromAccountId(cifAccount.getId());
            transaction.setFromAccountType(Transaction.AccountType.CIF);
            transaction.setToAccountType(Transaction.AccountType.BRANCH);
            transaction.setAmount(amount);
            
            // Find branch account ID using branch code
            // Note: You'll need to implement this method in your repository
            int branchAccountId = getBranchAccountIdByCode(branchCode);
            transaction.setToAccountId(branchAccountId);
            
            // Set payment method (assuming 1 is the default payment method ID)
            transaction.setPaymentMethod(paymentMethodRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Payment Method not found")));
            
//            // Process the transaction
//            transactionService.processTransaction(transaction);
        } catch (Exception e) {
            // Log the error but don't throw it to avoid disrupting the payment process
            System.err.println("Failed to return money to branch: " + e.getMessage());
        }
    }

    private String extractBranchCode(String cifCode) {
        // CIF Code format: branchCode(3) + userSerial(5) + cifSerial(5)
        // Extract first 3 digits which represent branch code
        return cifCode.substring(0, 3);
    }

    private int getBranchAccountIdByCode(String branchCode) {
        // You'll need to implement this method to get the branch account ID
        // This should query your branch_current_account table using the branch code
        return branchCurrentAccountRepository.findByBranchCode(branchCode)
            .orElseThrow(() -> new RuntimeException("Branch account not found for code: " + branchCode))
            .getId();
    }

    private void processLastTermPrincipal(SMETerm lastTerm, CIFCurrentAccount cifAccount, SMELoan loan) {
        BigDecimal available = getTotalAvailableAmount(cifAccount);
        if (available.compareTo(BigDecimal.ZERO) > 0) {
            // Try to pay principal
            processTermPrincipal(lastTerm, cifAccount, loan);
        }
        
        // If there's still principal remaining and any available balance
        if (lastTerm.getPrincipal().compareTo(BigDecimal.ZERO) > 0) {
            // Move any remaining balance to hold amount
            available = getTotalAvailableAmount(cifAccount);
            if (available.compareTo(BigDecimal.ZERO) > 0) {
                cifAccount.setHoldAmount(cifAccount.getHoldAmount().add(available));
                cifAccount.setBalance(BigDecimal.ZERO);
                cifCurrentAccountRepository.save(cifAccount);
            }
        }
    }
    private SMETerm findLastTerm(List<SMETerm> terms) {
        return terms.stream()
                .max((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate()))
                .orElse(null);
    }
    // Helper methods
    private List<SMETerm> getTermsToProcess(List<SMETerm> allTerms) {
        return allTerms.stream()
                .filter(term -> term.getStatus() == ConstraintEnum.PAST_DUE.getCode()
                        || term.getStatus() == ConstraintEnum.GRACE_PERIOD.getCode())
                .sorted((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate()))
                .collect(Collectors.toList());
    }
} 