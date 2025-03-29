package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.Service.PaymentTrigger;
import com.prj.LoneHPManagement.Service.TransactionService;
import com.prj.LoneHPManagement.model.entity.*;
import com.prj.LoneHPManagement.model.repo.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
public class HPAutoPaymentServiceImpl implements PaymentTrigger {
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
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private HpLongOverPaidHistoryRepository hpLongOverPaidHistoryRepository;
    @Autowired
    private BranchCurrentAccountRepository branchCurrentAccountRepository;
@Override
    public void processHpAccountPaymentsForTransaction(CIFCurrentAccount cifAccount) {
        List<HpLoan> activeLoans = hpLoanRepository.findByCifIdAndStatus(
                cifAccount.getCif().getId(),
                ConstraintEnum.UNDER_SCHEDULE.getCode()
        );

        for (HpLoan loan : activeLoans) {
            if (getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            processHpLoanPayment(loan, cifAccount);
        }
    }

    @Scheduled(cron = "0 0 0 * * *") // Run at midnight daily
    @Transactional
@Override
    public void processHpAutoPayments() {
        // Get all CIF accounts with balance
        List<CIFCurrentAccount> accountsWithBalance = cifCurrentAccountRepository.findAll().stream()
                .filter(acc -> acc.getBalance().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());

        for (CIFCurrentAccount cifAccount : accountsWithBalance) {
            processHpAccountPayments(cifAccount);
        }
    }

    private void processHpAccountPayments(CIFCurrentAccount cifAccount) {
        // Get all under schedule HP loans for this CIF
        List<HpLoan> activeLoans = hpLoanRepository.findByCifIdAndStatus(
                cifAccount.getCif().getId(),
                ConstraintEnum.UNDER_SCHEDULE.getCode()
        );

        for (HpLoan loan : activeLoans) {
            if (getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) <= 0) {
                break; // Stop if no funds
            }
            processHpLoanPayment(loan, cifAccount);
        }
    }

    private void processHpLoanPayment(HpLoan loan, CIFCurrentAccount cifAccount) {
        List<HpTerm> allTerms = hpTermRepository.findByHpLoan_Id(loan.getId());

        List<HpTerm> termsToProcess = allTerms.stream()
                .filter(term -> term.getStatus() == ConstraintEnum.PAST_DUE.getCode()
                        || term.getStatus() == ConstraintEnum.GRACE_PERIOD.getCode())
                .collect(Collectors.toList());

        if (termsToProcess.isEmpty()) return;

        long maxLateDays = getHpMaxLateDays(termsToProcess);

        if (maxLateDays > 180) {
            processHpLongTermOverdue(loan, allTerms, cifAccount);
        } else if (maxLateDays > 90) {
            processHpDefaultedLoan(loan, allTerms, cifAccount);
        } else {
            processHpNormalOverdue(loan, termsToProcess, cifAccount);
        }
    }
    private void processHpNormalOverdue(HpLoan loan, List<HpTerm> termsToProcess, CIFCurrentAccount cifAccount) {
        // Sort terms by due date
        termsToProcess.sort(Comparator.comparing(HpTerm::getDueDate));

        // Map to track payment history per term
        Map<HpTerm, HpLoanHistory> paymentHistoryMap = new LinkedHashMap<>();

        // Initialize history entries
        termsToProcess.forEach(term -> {
            HpLoanHistory history = new HpLoanHistory();
            history.setHpTerm(term);
            history.setPodPaid(BigDecimal.ZERO);
            history.setPrincipalLateFeePaid(BigDecimal.ZERO);
            history.setPrincipalLateDays(0);
            history.setPaidAmount(BigDecimal.ZERO);
            history.setOutstanding(BigDecimal.ZERO);
            history.setPaidDate(LocalDateTime.now());
            history.setInterestLateFeePaid(BigDecimal.ZERO);
            history.setTotalPaid(BigDecimal.ZERO);// Initialize late fee tracking
            history.setIodPaid(BigDecimal.ZERO);
            history.setInterestLateDays(0);
            history.setInterestPaid(BigDecimal.ZERO);
            history.setPrincipalPaid(BigDecimal.ZERO);
            paymentHistoryMap.put(term, history);
        });

        // 1. Process Interest Late Fees
        processPaymentComponent(termsToProcess, cifAccount, paymentHistoryMap,
                (term, history) -> {
                    int interestLateDays = term.getInterestLateDays(); // Capture current days
                    BigDecimal paid = processHpInterestLateFee(term, cifAccount);
                    if (paid != null) {
                        // Only set if payment was actually made
                        history.setInterestLateDays(interestLateDays);
                    }
                    history.setInterestLateFeePaid(paid);
                    return paid;
                });

        // 2. Process Principal Late Fees
        processPaymentComponent(termsToProcess, cifAccount, paymentHistoryMap,
                (term, history) -> {
                    int principalLateDays = term.getLatePrincipalDays(); // Capture current days
                    BigDecimal paid = processHpPrincipalLateFee(term, cifAccount);
                    if (paid != null) {
                        // Only set if payment was actually made
                        history.setPrincipalLateDays(principalLateDays);
                    }
                    history.setPrincipalLateFeePaid(paid);
                    return paid;
                });

        // 3. Process IOD
        processPaymentComponent(termsToProcess, cifAccount, paymentHistoryMap,
                (term, history) -> {
                    BigDecimal paid = processHpIOD(term, cifAccount);
                    history.setIodPaid(paid);
                    return paid;
                });

        // 4. Process POD
        processPaymentComponent(termsToProcess, cifAccount, paymentHistoryMap,
                (term, history) -> {
                    BigDecimal paid = processHpPOD(term, cifAccount);
                    history.setPodPaid(paid);
                    return paid;
                });

        // 5. Process Interest
        processPaymentComponent(termsToProcess, cifAccount, paymentHistoryMap,
                (term, history) -> {
                    BigDecimal paid = processHpTermInterest(term, cifAccount);
                    history.setInterestPaid(paid);
                    return paid;
                });

        // 6. Process Principal
        processPaymentComponent(termsToProcess, cifAccount, paymentHistoryMap,
                (term, history) -> {
                    BigDecimal paid = processHpPrincipal(term, cifAccount);
                    history.setPrincipalPaid(paid);
                    return paid;
                });



        // Update term repayment tracking
        paymentHistoryMap.forEach((term, history) -> {
            history.setHpTerm(term);

            BigDecimal totalPaid = history.getPrincipalLateFeePaid()  // Principal Late Fee
                    .add(history.getInterestLateFeePaid())                // Interest Late Fee
                    .add(history.getIodPaid())                            // Interest Overdue (IOD)
                    .add(history.getPodPaid())                            // Principal Overdue (POD)
                    .add(history.getInterestPaid())                       // Regular Interest
                    .add(history.getPrincipalPaid());

            history.setTotalPaid(totalPaid);
            hpLoanHistoryRepository.save(history);

            // Update term's repayment tracking fields
            if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
                // 1. Update total repayment amount
                term.setTotalRepaymentAmount(
                        term.getTotalRepaymentAmount().add(totalPaid)
                );

                // 2. Update last repayment date
                term.setLastRepayDate(new Date(System.currentTimeMillis()));
            }


        });

        // Update term statuses
        termsToProcess.forEach(term -> updateHpTermStatus(term, loan));
    }

    private void processHpLongTermOverdue(HpLoan loan, List<HpTerm> allTerms, CIFCurrentAccount cifAccount) {
        BigDecimal totalOutstanding = calculateHpTotalOutstanding(allTerms);
        long maxLateDays = getHpMaxLateDays(allTerms);

        BigDecimal lateFee = totalOutstanding
                .multiply(BigDecimal.valueOf(loan.getLongTermOverdueRate()))
                .multiply(BigDecimal.valueOf(maxLateDays))
                .divide(BigDecimal.valueOf(365), 10, RoundingMode.HALF_UP);

        if (processHpLateFeePayment(lateFee, cifAccount, allTerms, maxLateDays)) {
            resetHpLateDays(allTerms);
        }

        if (cifAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            processHpRemainingPayments(allTerms, cifAccount, loan);
        }
    }

    private void processHpDefaultedLoan(HpLoan loan, List<HpTerm> allTerms, CIFCurrentAccount cifAccount) {
        BigDecimal totalOutstanding = calculateHpTotalOutstanding(allTerms);
        long maxLateDays = getHpMaxLateDays(allTerms);

        BigDecimal lateFee = totalOutstanding
                .multiply(BigDecimal.valueOf(loan.getDefaultedRate()))
                .multiply(BigDecimal.valueOf(maxLateDays))
                .divide(BigDecimal.valueOf(365), 10, RoundingMode.HALF_UP);

        if (processHpLateFeePayment(lateFee, cifAccount, allTerms, maxLateDays)) {
            resetHpLateDays(allTerms);
        }

        if (cifAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            processHpRemainingPayments(allTerms, cifAccount, loan);
        }
    }


    private boolean processHpLateFeePayment(BigDecimal lateFee, CIFCurrentAccount cifAccount,
                                            List<HpTerm> terms, long maxLateDays) {
        BigDecimal totalAvailable = getTotalAvailableAmount(cifAccount);
        BigDecimal totalOutstanding = calculateHpTotalOutstanding(terms);
        BigDecimal paidAmount = BigDecimal.ZERO;
        HpLoan loan = terms.get(0).getHpLoan();
        if (totalAvailable.compareTo(lateFee) >= 0) {
            paidAmount = lateFee;
            // Deduct from hold amount first
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
            // Save payment history
            HpLongOverPaidHistory history = new HpLongOverPaidHistory();
            history.setLoan(loan);
            history.setLateFeeAmount(lateFee);
            history.setOutstandingAmount(totalOutstanding);
            history.setPaidAmount(lateFee);
            history.setLateDays((int)maxLateDays);
            hpLongOverPaidHistoryRepository.save(history);
            return true;
        } else {
            cifAccount.setHoldAmount(totalAvailable);
            cifAccount.setBalance(BigDecimal.ZERO);
            return false;
        }
    }

    private void processHpRemainingPayments(List<HpTerm> terms, CIFCurrentAccount cifAccount, HpLoan loan) {
        List<HpTerm> sortedTerms = terms.stream()
                .sorted(Comparator.comparing(HpTerm::getDueDate))
                .collect(Collectors.toList());

        Map<HpTerm, HpLoanHistory> historyMap = new LinkedHashMap<>();
        sortedTerms.forEach(term -> {
            HpLoanHistory history = new HpLoanHistory();
            history.setHpTerm(term);
            history.setPodPaid(BigDecimal.ZERO);
            history.setPrincipalLateFeePaid(BigDecimal.ZERO);
            history.setPrincipalLateDays(0);
            history.setPaidAmount(BigDecimal.ZERO);
            history.setOutstanding(BigDecimal.ZERO);
            history.setPaidDate(LocalDateTime.now());
            history.setInterestLateFeePaid(BigDecimal.ZERO);
            history.setTotalPaid(BigDecimal.ZERO);// Initialize late fee tracking
            history.setIodPaid(BigDecimal.ZERO);
            history.setInterestLateDays(0);
            history.setInterestPaid(BigDecimal.ZERO);
            history.setPrincipalPaid(BigDecimal.ZERO);
            historyMap.put(term, history);
        });
//        // 1. Process Interest Late Fees
//        processPaymentComponent(sortedTerms, cifAccount, historyMap,
//                (term, history) -> processHpInterestLateFee(term, cifAccount, history));
//
//        // 2. Process Principal Late Fees
//        processPaymentComponent(sortedTerms, cifAccount, historyMap,
//                (term, history) -> processHpPrincipalLateFee(term, cifAccount, history));

        // 3. Process IOD
        processPaymentComponent(sortedTerms, cifAccount, historyMap,

        (term, history) -> {
            BigDecimal paid = processHpIOD(term, cifAccount);
            history.setIodPaid(paid);
            return paid;
        });
        // 4. Process POD (Principal Overdue)
        processPaymentComponent(sortedTerms, cifAccount, historyMap,
               // (term, history) -> processHpPOD(term, cifAccount, history));
        (term, history) -> {
            BigDecimal paid = processHpPOD(term, cifAccount);
            history.setPodPaid(paid);
            return paid;
        });

        // 5. Process Interest
        processPaymentComponent(sortedTerms, cifAccount, historyMap,
              //  (term, history) -> processHpTermInterest(term, cifAccount));
        (term, history) -> {
            BigDecimal paid = processHpTermInterest(term, cifAccount);
            history.setInterestPaid(paid);
            return paid;
        });
        // 6. Process Principal
        processPaymentComponent(sortedTerms, cifAccount, historyMap,
                //(term, history) -> processHpPrincipal(term, cifAccount, loan));
        (term, history) -> {
            BigDecimal paid = processHpPrincipal(term, cifAccount);
            history.setPrincipalPaid(paid);
            return paid;
        });
        // Save histories and update terms
        // Update term repayment tracking
        historyMap.forEach((term, history) -> {
            history.setHpTerm(term);

            // Calculate total paid for this transaction
            BigDecimal totalPaid = history.getPrincipalLateFeePaid()
                    .add(history.getIodPaid())
                    .add(history.getInterestLateFeePaid())
                    .add(history.getPodPaid())  // Added POD component
                    .add(history.getInterestPaid())
                    .add(history.getPrincipalPaid());

            history.setTotalPaid(totalPaid);
            hpLoanHistoryRepository.save(history);

            if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
                term.setTotalRepaymentAmount(
                        term.getTotalRepaymentAmount().add(totalPaid)
                );
                term.setLastRepayDate(new Date(System.currentTimeMillis()));
            }

        });
        sortedTerms.forEach(term -> updateHpTermStatus(term, loan));
    }
    private void processPaymentComponent(List<HpTerm> terms,
                                         CIFCurrentAccount cifAccount,
                                         Map<HpTerm, HpLoanHistory> historyMap,
                                         BiFunction<HpTerm, HpLoanHistory, BigDecimal> paymentProcessor) {
        for (HpTerm term : terms) {
            // Check available balance first
            if (getTotalAvailableAmount(cifAccount).compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            HpLoanHistory history = historyMap.get(term);
            if (history != null) {
                paymentProcessor.apply(term, history);
            }
        }
    }
    private BigDecimal processHpTermInterest(HpTerm term, CIFCurrentAccount cifAccount) {
        BigDecimal totalAvailable = getTotalAvailableAmount(cifAccount);
        BigDecimal interestDue = term.getInterest();

        if (interestDue.compareTo(BigDecimal.ZERO) <= 0 || totalAvailable.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Pay as much as possible
        BigDecimal interestPaid = interestDue.min(totalAvailable);
        term.setInterest(interestDue.subtract(interestPaid));

        // Update account balances (same logic as SME but might need HP-specific history tracking)
        updateAccountBalances(cifAccount, interestPaid);

        // Update HP-specific fields
        term.setOutstandingAmount(term.getOutstandingAmount().subtract(interestPaid));
        term.setTotalRepaymentAmount(term.getTotalRepaymentAmount().add(interestPaid));
        term.setLastRepayment(interestPaid);
        term.setLastRepayDate(new Date(System.currentTimeMillis()));

        hpTermRepository.save(term);

        return interestPaid;
    }

    // HP-specific payment components
    private BigDecimal processHpInterestLateFee(HpTerm term, CIFCurrentAccount cifAccount) {
        if (term.getInterestLateDays() <= 0) {
            return null;
        }

        // Calculate interest late fee
        BigDecimal lateFee = term.getInterestOfOverdue()
                .multiply(BigDecimal.valueOf(term.getHpLoan().getLateFeeRate()))
                .multiply(BigDecimal.valueOf(term.getInterestLateDays()))
                .divide(BigDecimal.valueOf(365), 10, RoundingMode.HALF_UP);

        BigDecimal availableBalance = getTotalAvailableAmount(cifAccount);
        if (availableBalance.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        if (availableBalance.compareTo(lateFee) >= 0) {
            updateAccountBalances(cifAccount, lateFee);
            term.setInterestLateDays(0);
            term.setInterestLateFee(BigDecimal.ZERO); // Clear accumulated fee
            hpTermRepository.save(term);
            return lateFee;
        } else {
            cifAccount.setHoldAmount(cifAccount.getHoldAmount().add(availableBalance));
            cifAccount.setBalance(BigDecimal.ZERO);
            cifCurrentAccountRepository.save(cifAccount);
            return null;
        }
    }
    private BigDecimal processHpPrincipalLateFee(HpTerm term, CIFCurrentAccount cifAccount) {
        if (term.getLatePrincipalDays() <= 0) {
            return null;
        }

        // Calculate principal late fee
        BigDecimal lateFee = term.getPrincipalOfOverdue()
                .multiply(BigDecimal.valueOf(term.getHpLoan().getLateFeeRate()))
                .multiply(BigDecimal.valueOf(term.getLatePrincipalDays()))
                .divide(BigDecimal.valueOf(365), 10, RoundingMode.HALF_UP);

        BigDecimal availableBalance = getTotalAvailableAmount(cifAccount);
        if (availableBalance.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        if (availableBalance.compareTo(lateFee) >= 0) {
            updateAccountBalances(cifAccount, lateFee);
            term.setLatePrincipalDays(0);
            term.setPrincipalLateFee(BigDecimal.ZERO); // Clear accumulated fee
            hpTermRepository.save(term);
            return lateFee;
        } else {
            cifAccount.setHoldAmount(cifAccount.getHoldAmount().add(availableBalance));
            cifAccount.setBalance(BigDecimal.ZERO);
            cifCurrentAccountRepository.save(cifAccount);
            return null;
        }
    }

    private BigDecimal processHpPOD(HpTerm term, CIFCurrentAccount account) {
        BigDecimal totalAvailable = getTotalAvailableAmount(account);
        BigDecimal podDue = term.getPrincipalOfOverdue();
//        BigDecimal paid = payFromAccount(term.getPrincipalOfOverdue(), account);
        if (podDue.compareTo(BigDecimal.ZERO) <= 0 || totalAvailable.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal podPaid = podDue.min(totalAvailable);
        term.setPrincipalOfOverdue(podDue.subtract(podPaid));
        updateAccountBalances(account, podPaid);
        return podPaid;
    }
    private BigDecimal processHpIOD(HpTerm term, CIFCurrentAccount account) {
        BigDecimal totalAvailable = getTotalAvailableAmount(account);
        BigDecimal iodDue = term.getInterestOfOverdue();
//        BigDecimal paid = payFromAccount(term.getPrincipalOfOverdue(), account);
        if (iodDue.compareTo(BigDecimal.ZERO) <= 0 || totalAvailable.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal iodPaid = iodDue.min(totalAvailable);
        term.setInterestOfOverdue(iodDue.subtract(iodPaid));
        updateAccountBalances(account, iodPaid);
        hpTermRepository.save(term);
        return iodPaid;
    }


    private BigDecimal processHpPrincipal(HpTerm term, CIFCurrentAccount account) {
        BigDecimal totalAvailable = getTotalAvailableAmount(account);
        BigDecimal prinDue = term.getPrincipal();

        if (prinDue.compareTo(BigDecimal.ZERO) <= 0 || totalAvailable.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Pay as much as possible
        BigDecimal prinPaid = prinDue.min(totalAvailable);
        term.setPrincipal(prinDue.subtract(prinPaid)); // Track remaining IOD
        updateAccountBalances(account, prinPaid);
        hpTermRepository.save(term);

        return prinPaid;
    }

    // Helper methods
    private BigDecimal calculateHpTotalOutstanding(List<HpTerm> terms) {
        return terms.stream()
                .map(t -> t.getInterest()
                        .add(t.getInterestOfOverdue())
                        .add(t.getPrincipalOfOverdue())
                        .add(t.getPrincipal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private long getHpMaxLateDays(List<HpTerm> terms) {
        return terms.stream()
                .filter(term -> term.getStatus() == ConstraintEnum.PAST_DUE.getCode())
                .mapToLong(term -> Math.max(term.getLatePrincipalDays(), term.getInterestLateDays()))
                .max()
                .orElse(0);
    }


    private void resetHpLateDays(List<HpTerm> terms) {
        terms.forEach(t -> {
            t.setInterestLateDays(0);
            t.setLatePrincipalDays(0);
        });
        hpTermRepository.saveAll(terms);
    }

    private void updateHpTermStatus(HpTerm term, HpLoan loan) {
        if (term.getPrincipal().add(term.getInterest())
                .add(term.getInterestOfOverdue()).add(term.getPrincipalOfOverdue())
                .compareTo(BigDecimal.ZERO) == 0) {
            term.setStatus(ConstraintEnum.PAID_OFF.getCode());
            if (isLastHpTerm(term, loan)) {
                loan.setStatus(ConstraintEnum.PAID_OFF.getCode());
                hpLoanRepository.save(loan);
            }
        }
        hpTermRepository.save(term);
    }
    private boolean isLastHpTerm(HpTerm currentTerm, HpLoan loan) {
        // Get all terms for this loan
        List<HpTerm> allTerms = hpTermRepository.findByHpLoan_Id(loan.getId());

        // Find the term with the latest due date
        HpTerm lastTerm = allTerms.stream()
                .max((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate()))
                .orElse(null);

        // Check if current term is the last term
        return lastTerm != null && lastTerm.getId() == currentTerm.getId();
    }
    private BigDecimal getTotalAvailableAmount(CIFCurrentAccount cifAccount) {
        return cifAccount.getBalance().add(cifAccount.getHoldAmount());
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

}
