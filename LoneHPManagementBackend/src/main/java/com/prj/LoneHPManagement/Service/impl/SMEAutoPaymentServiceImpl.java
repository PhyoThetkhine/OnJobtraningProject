package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.Service.SMEAutoPaymentService;
import com.prj.LoneHPManagement.Service.TransactionService;
import com.prj.LoneHPManagement.model.entity.*;
import com.prj.LoneHPManagement.model.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiFunction;
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
    @Autowired
    private SMELongOverPaidHistoryRepository smeLongOverPaidHistoryRepository;


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

        // Long-term overdue example
        BigDecimal lateFee = totalOutstanding
                .multiply(BigDecimal.valueOf(loan.getLongTermOverdueRate())) // e.g., 0.24 (24% annual rate)
                .multiply(BigDecimal.valueOf(maxLateDays)) // e.g., 30 days
                .divide(BigDecimal.valueOf(365), 10, RoundingMode.HALF_UP);
//
//        BigDecimal lateFee = calculateLateFee(totalOutstanding, BigDecimal.valueOf(loan.getLongTermOverdueRate()))
//                .multiply(BigDecimal.valueOf(maxLateDays));

        // Process late fee payment
        if (processLateFeePayment(lateFee, cifAccount, allTerms,maxLateDays )) {
                // Reset late days for all terms after successful payment
                resetLateDaysForAllTerms(allTerms);
        }

        if (cifAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            processRemainingPayments(allTerms, cifAccount, loan);

        }
    }

    private void processDefaultedLoan(SMELoan loan, List<SMETerm> allTerms, CIFCurrentAccount cifAccount) {
        // Calculate total outstanding (Interest + IOD for each term + Principal once)
        BigDecimal totalOutstanding = calculateTotalOutstanding(allTerms);
        
        // Get the maximum late days for calculation
        long maxLateDays = getMaxLateDays(allTerms);
        // Long-term overdue example
        BigDecimal lateFee = totalOutstanding
                .multiply(BigDecimal.valueOf(loan.getDefaultedRate())) // e.g., 0.24 (24% annual rate)
                .multiply(BigDecimal.valueOf(maxLateDays)) // e.g., 30 days
                .divide(BigDecimal.valueOf(365), 10, RoundingMode.HALF_UP);


        // Process late fee payment
        if (processLateFeePayment(lateFee, cifAccount, allTerms,maxLateDays )) {
            // Reset late days for all terms after successful payment
            resetLateDaysForAllTerms(allTerms);
        }
        if (cifAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            processRemainingPayments(allTerms, cifAccount, loan);

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
//        // Filter and sort terms
//        List<SMETerm> termsToProcess = terms.stream()
//                .filter(term -> term.getStatus() == ConstraintEnum.PAST_DUE.getCode()
//                        || term.getStatus() == ConstraintEnum.GRACE_PERIOD.getCode())
//                .sorted(Comparator.comparing(SMETerm::getDueDate))
//                .collect(Collectors.toList());

        // Map to track payment history per term
        Map<SMETerm, SMELoanHistory> paymentHistoryMap = new LinkedHashMap<>();

        // Initialize history entries for all terms
        termsToProcess.forEach(term -> {
            SMELoanHistory history = new SMELoanHistory();
            history.setSmeTerm(term);
            history.setPaidDate(LocalDateTime.now());
            history.setIodPaid(BigDecimal.ZERO);
            history.setInterestPaid(BigDecimal.ZERO);
            history.setPrincipalPaid(BigDecimal.ZERO);
            paymentHistoryMap.put(term, history);
        });
        processPaymentComponent(termsToProcess, cifAccount, paymentHistoryMap,
                (term, history) -> {
                    BigDecimal paid = processTermLateFees(term, cifAccount);
                    history.setInterestLateFeePaid(paid);
                    return paid;
                });

        // First pass: Process IOD for all terms
        processPaymentComponent(termsToProcess, cifAccount, paymentHistoryMap,
                (term, history) -> {
                    BigDecimal paid = processTermIOD(term, cifAccount);
                    history.setIodPaid(paid);
                    return paid;
                });

        // Second pass: Process Interest for all terms
        processPaymentComponent(termsToProcess, cifAccount, paymentHistoryMap,
                (term, history) -> {
                    BigDecimal paid = processTermInterest(term, cifAccount);
                    history.setInterestPaid(paid);
                    return paid;
                });

        // Third pass: Process Principal for eligible terms
        if (!termsToProcess.isEmpty()) {
            SMETerm lastTerm = termsToProcess.get(termsToProcess.size() - 1);
            if (loan.getPaidPrincipalStatus() == ConstraintEnum.ALLOWED.getCode() || isLastTerm(lastTerm, loan)) {
                processPaymentComponent(Collections.singletonList(lastTerm), cifAccount, paymentHistoryMap,
                        (term, history) -> {
                            BigDecimal paid = processTermPrincipal(term, cifAccount, loan);
                            history.setPrincipalPaid(paid);
                            return paid;
                        });
            }
        }

        // Calculate totals and save histories
        paymentHistoryMap.values().forEach(history -> {
            history.setTotalPaid(history.getIodPaid()
                    .add(history.getInterestPaid())
                    .add(history.getPrincipalPaid()));
            smeLoanHistoryRepository.save(history);
        });

        // Update term statuses
        termsToProcess.forEach(term -> updateTermStatus(term, loan));

    }

    private BigDecimal processTermLateFees(SMETerm term, CIFCurrentAccount cifAccount) {
        BigDecimal totalPaid =BigDecimal.ZERO;

        if (term.getInterestLateDays() <= 0) {
            return null;
        }
        BigDecimal lateFee = term.getInterestOfOverdue() // e.g., $1,000 (IOD amount)
                .multiply(BigDecimal.valueOf(term.getSmeLoan().getLateFeeRate())) // e.g., 0.12 (12% annual rate)
                .multiply(BigDecimal.valueOf(term.getInterestLateDays())) // e.g., 10 days
                .divide(BigDecimal.valueOf(365), 10, RoundingMode.HALF_UP); // Divide by days in year

//        BigDecimal lateFee = calculateLateFee(term.getInterestOfOverdue(), BigDecimal.valueOf(term.getSmeLoan().getInterestRate()))
//                .multiply(BigDecimal.valueOf(term.getInterestLateDays()));
//
        BigDecimal availableBalance = getTotalAvailableAmount(cifAccount);
        if (availableBalance.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        if (availableBalance.compareTo(lateFee) >= 0) {
            updateAccountBalances(cifAccount, lateFee);
            term.setInterestLateDays(0);
            smeTermRepository.save(term);
            return lateFee;
        } else {
            // Insufficient funds: Move available balance to hold
            cifAccount.setHoldAmount(cifAccount.getHoldAmount().add(availableBalance));
            cifAccount.setBalance(BigDecimal.ZERO);
            cifCurrentAccountRepository.save(cifAccount);
            return null;
        }


    }
    private BigDecimal processTermIOD(SMETerm term, CIFCurrentAccount cifAccount) {
        BigDecimal totalAvailable = getTotalAvailableAmount(cifAccount);
        BigDecimal iodDue = term.getInterestOfOverdue();

        if (iodDue.compareTo(BigDecimal.ZERO) <= 0 || totalAvailable.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Pay as much as possible
        BigDecimal iodPaid = iodDue.min(totalAvailable);
        term.setInterestOfOverdue(iodDue.subtract(iodPaid)); // Track remaining IOD
        updateAccountBalances(cifAccount, iodPaid);
        smeTermRepository.save(term);

        return iodPaid;
    }
    private BigDecimal processTermInterest(SMETerm term, CIFCurrentAccount cifAccount) {
        BigDecimal totalAvailable = getTotalAvailableAmount(cifAccount);
        BigDecimal interestDue = term.getInterest();

        if (interestDue.compareTo(BigDecimal.ZERO) <= 0 || totalAvailable.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Pay as much as possible
        BigDecimal insterestPaid = interestDue.min(totalAvailable);
        term.setInterest(interestDue.subtract(insterestPaid)); // Track remaining IOD
        updateAccountBalances(cifAccount, insterestPaid);
        smeTermRepository.save(term);

        return insterestPaid;
    }

    private BigDecimal processTermPrincipal(SMETerm term, CIFCurrentAccount cifAccount, SMELoan loan) {
        BigDecimal totalAvailable = getTotalAvailableAmount(cifAccount);
        BigDecimal totalPaid = BigDecimal.ZERO;

        // Process principal payment if there's an outstanding principal and available funds
        if (term.getPrincipal().compareTo(BigDecimal.ZERO) > 0 && totalAvailable.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal principalPaid = processPayment(term.getPrincipal(), totalAvailable);
            totalAvailable = totalAvailable.subtract(principalPaid);
            totalPaid = totalPaid.add(principalPaid);

            // Get all future terms (those with a due date after the current term)
            List<SMETerm> futureTerms = smeTermRepository.findBySmeLoan_IdAndDueDateAfter(
                            loan.getId(),
                            term.getDueDate()
                    ).stream()
                    .sorted(Comparator.comparing(SMETerm::getDueDate))
                    .collect(Collectors.toList());

            // Calculate the remaining principal after payment
            BigDecimal remainingPrincipal = term.getPrincipal().subtract(principalPaid);

            // For the current term, clear the principal and update outstanding amount accordingly
            term.setPrincipal(BigDecimal.ZERO);
            term.setOutstandingAmount(term.getInterest().add(term.getInterestOfOverdue()));
            smeTermRepository.save(term);

            // For each future term, set the remaining principal and recalculate the interest.
            // The new interest is calculated using:
            //   newInterest = remainingPrincipal * (loan.getInterestRate()) * (days) / (365 * 100)
            for (SMETerm futureTerm : futureTerms) {
                futureTerm.setPrincipal(remainingPrincipal);
                int days = futureTerm.getDays(); // reuse the original 'days' for the term
                BigDecimal newInterest = remainingPrincipal
                        .multiply(BigDecimal.valueOf(loan.getInterestRate()))
                        .multiply(BigDecimal.valueOf(days))
                        .divide(BigDecimal.valueOf(365 * 100), 10, RoundingMode.HALF_UP);
                futureTerm.setInterest(newInterest);
                futureTerm.setOutstandingAmount(newInterest);
                smeTermRepository.save(futureTerm);
            }
        }
        return totalPaid;
    }

    private void updateAccountBalances(CIFCurrentAccount cifAccount, BigDecimal amountPaid) {
        BigDecimal remainingAmount = amountPaid;
        BigDecimal holdAmount = cifAccount.getHoldAmount();

        // Deduct from holdAmount first
        if (holdAmount.compareTo(BigDecimal.ZERO) > 0) {
            if (holdAmount.compareTo(remainingAmount) >= 0) {
                cifAccount.setHoldAmount(holdAmount.subtract(remainingAmount));
                remainingAmount = BigDecimal.ZERO;
            } else {
                remainingAmount = remainingAmount.subtract(holdAmount);
                cifAccount.setHoldAmount(BigDecimal.ZERO);
            }
        }

        // Deduct remaining amount from balance
        if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
            cifAccount.setBalance(cifAccount.getBalance().subtract(remainingAmount));
        }

        // Save the updated account
        cifCurrentAccountRepository.save(cifAccount);
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

    private boolean processLateFeePayment(BigDecimal lateFee, CIFCurrentAccount cifAccount,List<SMETerm> allTerms, long maxLateDays) {
        BigDecimal totalAvailable = getTotalAvailableAmount(cifAccount);
        BigDecimal totalOutstanding = calculateTotalOutstanding(allTerms);
        SMELoan loan = allTerms.get(0).getSmeLoan();

        BigDecimal paidAmount = BigDecimal.ZERO;
        if (totalAvailable.compareTo(lateFee) >= 0) {
            paidAmount = lateFee;
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
            SMELongOverPaidHistory history = new SMELongOverPaidHistory();
            history.setLateFeeAmount(lateFee);
            history.setOutstandingAmount(totalOutstanding);
            history.setPaidAmount(paidAmount);
            history.setLateDays((int) maxLateDays);
            history.setLoan(loan);
            history.setPaidDate(LocalDateTime.now());
            smeLongOverPaidHistoryRepository.save(history);
            
            cifCurrentAccountRepository.save(cifAccount);
            return true; // Late fee fully paid
        } else {
            cifAccount.setHoldAmount(totalAvailable); // Move totalAvailable into hold
            cifAccount.setBalance(BigDecimal.ZERO);   // Clear balance
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

        
        smeLoanHistoryRepository.save(history);
    }

//    private void processRemainingPayments(List<SMETerm> terms, CIFCurrentAccount cifAccount, SMELoan loan) {
//
//        // Filter and sort terms that need processing (PAST_DUE or GRACE_PERIOD)
//        List<SMETerm> termsToProcess = terms.stream()
//                .filter(term -> term.getStatus() == ConstraintEnum.PAST_DUE.getCode()
//                        || term.getStatus() == ConstraintEnum.GRACE_PERIOD.getCode())
//                .sorted((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate()))
//                .collect(Collectors.toList());
//
//        termsToProcess.sort((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate()));
//
//        // first pass: Process all IOD
//        for (SMETerm term : termsToProcess) {
//            SMELoanHistory history = new SMELoanHistory();  // Create NEW history per term
//            history.setSmeTerm(term);
//            if (getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) <= 0) {
//                return;
//            }
//            BigDecimal iodPaid = processTermIOD(term, cifAccount);
//            history.setIodPaid(iodPaid);
//        }
//
//        // second pass: Process all interest
//        for (SMETerm term : termsToProcess) {
//            if (getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) <= 0) {
//                return;
//            }
//            BigDecimal interestPaid = processTermInterest(term, cifAccount);
//            history.setInterestPaid(interestPaid);
//        }
//        SMETerm lastTerm = termsToProcess.get(termsToProcess.size() - 1);
//        // third pass: Process principal if allowed
//        if (loan.getPaidPrincipalStatus() == ConstraintEnum.ALLOWED.getCode() && !termsToProcess.isEmpty()) {
//            BigDecimal principalPaid =   processTermPrincipal(lastTerm, cifAccount, loan);
//            history.setPrincipalPaid(principalPaid);
//        }else {
//            if (isLastTerm(lastTerm, loan)) {
//                BigDecimal principalPaid =  processTermPrincipal(lastTerm, cifAccount, loan);
//                history.setPrincipalPaid(principalPaid);
//            }
//        }
//        smeLoanHistoryRepository.save(history);
//
//        // Update term statuses
//        for (SMETerm term : termsToProcess) {
//            updateTermStatus(term, loan);
//        }
//
//
//    }
private void processRemainingPayments(List<SMETerm> terms, CIFCurrentAccount cifAccount, SMELoan loan) {
    // Filter and sort terms
    List<SMETerm> termsToProcess = terms.stream()
            .filter(term -> term.getStatus() == ConstraintEnum.PAST_DUE.getCode()
                    || term.getStatus() == ConstraintEnum.GRACE_PERIOD.getCode())
            .sorted(Comparator.comparing(SMETerm::getDueDate))
            .collect(Collectors.toList());

    // Map to track payment history per term
    Map<SMETerm, SMELoanHistory> paymentHistoryMap = new LinkedHashMap<>();

    // Initialize history entries for all terms
    termsToProcess.forEach(term -> {
        SMELoanHistory history = new SMELoanHistory();
        history.setSmeTerm(term);
        history.setPaidDate(LocalDateTime.now());
        history.setIodPaid(BigDecimal.ZERO);
        history.setInterestPaid(BigDecimal.ZERO);
        history.setPrincipalPaid(BigDecimal.ZERO);
        paymentHistoryMap.put(term, history);
    });

    // First pass: Process IOD for all terms
    processPaymentComponent(termsToProcess, cifAccount, paymentHistoryMap,
            (term, history) -> {
                BigDecimal paid = processTermIOD(term, cifAccount);
                history.setIodPaid(paid);
                return paid;
            });

    // Second pass: Process Interest for all terms
    processPaymentComponent(termsToProcess, cifAccount, paymentHistoryMap,
            (term, history) -> {
                BigDecimal paid = processTermInterest(term, cifAccount);
                history.setInterestPaid(paid);
                return paid;
            });

    // Third pass: Process Principal for eligible terms
    if (!termsToProcess.isEmpty()) {
        SMETerm lastTerm = termsToProcess.get(termsToProcess.size() - 1);
        if (loan.getPaidPrincipalStatus() == ConstraintEnum.ALLOWED.getCode() || isLastTerm(lastTerm, loan)) {
            processPaymentComponent(Collections.singletonList(lastTerm), cifAccount, paymentHistoryMap,
                    (term, history) -> {
                        BigDecimal paid = processTermPrincipal(term, cifAccount, loan);
                        history.setPrincipalPaid(paid);
                        return paid;
                    });
        }
    }

    // Calculate totals and save histories
    paymentHistoryMap.values().forEach(history -> {
        history.setTotalPaid(history.getIodPaid()
                .add(history.getInterestPaid())
                .add(history.getPrincipalPaid()));
        smeLoanHistoryRepository.save(history);
    });

    // Update term statuses
    termsToProcess.forEach(term -> updateTermStatus(term, loan));
}
    private void processPaymentComponent(List<SMETerm> terms,
                                         CIFCurrentAccount cifAccount,
                                         Map<SMETerm, SMELoanHistory> historyMap,
                                         BiFunction<SMETerm, SMELoanHistory, BigDecimal> paymentProcessor) {
        for (SMETerm term : terms) {
            // Check available balance first
            if (getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            SMELoanHistory history = historyMap.get(term);
            if (history != null) {
                paymentProcessor.apply(term, history);
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