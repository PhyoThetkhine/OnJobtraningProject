package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.Service.AutoPaymentService;
import com.prj.LoneHPManagement.Service.AutoRunService;
import com.prj.LoneHPManagement.Service.HpTermCalculationService;
import com.prj.LoneHPManagement.Service.SMETermCalculationService;
import com.prj.LoneHPManagement.model.entity.*;
import com.prj.LoneHPManagement.model.repo.CIFCurrentAccountRepository;
import com.prj.LoneHPManagement.model.repo.HpTermRepository;
import com.prj.LoneHPManagement.model.repo.SMELoanRepository;
import com.prj.LoneHPManagement.model.repo.SMETermRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AutoRunServiceImpl implements AutoRunService {
    @Autowired
    private SMETermRepository smeTermRepository;

    @Autowired
    private SMETermCalculationServiceImpl smeTermCalculationService;

    @Autowired
    private CIFCurrentAccountRepository cifCurrentAccountRepository;

    @Autowired
    private HpTermRepository hpTermRepository; // Added missing repository

    @Autowired
    private AutoPaymentService autoPaymentService;

    @Autowired
    private HpTermCalculationService hpTermCalculationService;

    @Autowired
    private SMELoanRepository smeLoanRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
    @Override
    public void dailyRun() {

        //dailyLateFeeCalculation

//        List<SMETerm> SmeOverdueTerms = smeTermRepository.findByStatus(ConstraintEnum.PAST_DUE.getCode());
//        SmeOverdueTerms.forEach(term -> {
//            smeTermCalculationService.calculateLateFee(term);
//            smeTermCalculationService.updateOutstandingAmount(term);
//            smeTermRepository.save(term);
//        });

//        List<HpTerm> HpOverdueTerms = hpTermRepository.findByStatus(ConstraintEnum.PAST_DUE.getCode());
//        HpOverdueTerms.forEach(term -> {
//            hpTermCalculationService.calculateLateFee(term);
//            hpTermCalculationService.updateOutstandingAmount(term);
//            hpTermRepository.save(term);
//        });
//        //dailyLateFeeCalculation
//        List<SMETerm> SMEGracePeriodTerms = smeTermRepository.findByStatus(ConstraintEnum.GRACE_PERIOD.getCode());
//        SMEGracePeriodTerms.forEach(term -> {
//            SMELoan smeLoan = term.getSmeLoan();
//            LocalDate dueDate = term.getDueDate().toLocalDate();
//            LocalDate currentDate = LocalDate.now();
//
//            if (currentDate.isAfter(dueDate.plusDays(smeLoan.getGracePeriod()))) {
//                term.setStatus(ConstraintEnum.PAST_DUE.getCode());
//                term.setInterestOfOverdue(term.getInterest());
//                term.setInterest(BigDecimal.ZERO);
//                smeTermCalculationService.calculateLateFee(term);
//                smeTermRepository.save(term);
//            }
//        });

//        List<HpTerm> HPGracePeriodTerms = hpTermRepository.findByStatus(ConstraintEnum.GRACE_PERIOD.getCode());
//        HPGracePeriodTerms.forEach(term -> {
//            HpLoan hpLoan = term.getHpLoan();
//            LocalDate dueDate = term.getDueDate().toLocalDate();
//            LocalDate currentDate = LocalDate.now();
//
//            if (currentDate.isAfter(dueDate.plusDays(hpLoan.getGracePeriod()))) {
//                term.setStatus(ConstraintEnum.PAST_DUE.getCode());
//                term.setInterestOfOverdue(term.getInterest());
//                term.setInterest(BigDecimal.ZERO);
//                hpTermCalculationService.calculateLateFee(term);
//                hpTermRepository.save(term);
//            }
//        });
        // Find all terms that are currently scheduled (UNDER_SCHEDULE)
        List<SMETerm> terms = smeTermRepository.findByStatus(ConstraintEnum.UNDER_SCHEDULE.getCode());
        LocalDate today = LocalDate.now();

        for (SMETerm term : terms) {
            // Convert the due date (java.sql.Date) to LocalDate
            LocalDate dueDate = term.getDueDate().toLocalDate();
            // Calculate how many days the term is past its due date.
            long daysPastDue = ChronoUnit.DAYS.between(dueDate, today);
            // If due date is in the future, no update is needed.
            if (daysPastDue < 0) {
                continue;
            }
            // Retrieve the grace period (in days) from the associated loan.
            int gracePeriod = term.getSmeLoan().getGracePeriod();

            if (daysPastDue <= gracePeriod) {
                // Within the grace period, update status to GRACE_PERIOD.
                term.setStatus(ConstraintEnum.GRACE_PERIOD.getCode());
            } else {
                // Past the grace period:
                //  - Update status to PAST_DUE.
                //  - Update interestOfOverdue using the current interest amount.
                //  - Set interest to 0.
                term.setStatus(ConstraintEnum.PAST_DUE.getCode());
                term.setInterestOfOverdue(term.getInterest());
                term.setInterest(BigDecimal.ZERO);
            }
            // Save the updated term.
            smeTermRepository.save(term);
        }
        // Process payments for all accounts
        List<CIFCurrentAccount> allAccounts = cifCurrentAccountRepository.findAll();
        allAccounts.forEach(this::processAccountPayments);
    }

    public void processAccountPayments(CIFCurrentAccount cifAccount) {
        boolean hasBalance = true;

        while (hasBalance && cifAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            hasBalance = processSmePayments(cifAccount) || processHpPayments(cifAccount);
        }

        cifCurrentAccountRepository.save(cifAccount);
    }

    public boolean processSmePayments(CIFCurrentAccount cifAccount) {
        List<SMELoan> smeLoans = smeLoanRepository.findByCifId(cifAccount.getCif().getId());
        for (SMELoan loan : smeLoans) {
            if (cifAccount.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            List<SMETerm> allTerms = smeTermRepository.findBySmeLoan_Id(loan.getId());

            long maxOverdueDays = allTerms.stream()
                    .filter(term -> term.getStatus() == ConstraintEnum.PAST_DUE.getCode())
                    .mapToLong (term -> {
                        LocalDateTime dueDateTime = term.getDueDate().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime();
                        return Duration.between(dueDateTime, LocalDateTime.now()).toDays();
                    })
                    .max()
                    .orElse(0);

            if (maxOverdueDays > 90) {
                SmeProcessLateLoan(loan, allTerms, cifAccount);
                continue;
            }

            List<SMETerm> overdueTerms = allTerms.stream()
                    .filter(term -> term.getStatus() == ConstraintEnum.PAST_DUE.getCode())
                    .collect(Collectors.toList());
            for (SMETerm term : overdueTerms) {
                if (smeProcessTerm(term, cifAccount)) {
                    break; // Break if a payment was made
                }
            }

            List<SMETerm> graceTerms = allTerms.stream()
                    .filter(term -> term.getStatus() == ConstraintEnum.GRACE_PERIOD.getCode())
                    .collect(Collectors.toList());
            for (SMETerm term : graceTerms) {
                if (smeProcessTerm(term, cifAccount)) {
                    break; // Break if a payment was made
                }
            }
        }
        return cifAccount.getBalance().compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean processHpPayments(CIFCurrentAccount cifAccount) {
        CIF cif = cifAccount.getCif();
        List<HpTerm> termsToProcess = new ArrayList<>();
        termsToProcess.addAll(hpTermRepository.findByHpLoan_CifAndStatus(
                cif, ConstraintEnum.PAST_DUE.getCode()));
        termsToProcess.addAll(hpTermRepository.findByHpLoan_CifAndStatus(
                cif, ConstraintEnum.GRACE_PERIOD.getCode()));

        for (HpTerm term : termsToProcess) {
            if (cifAccount.getBalance().compareTo(BigDecimal.ZERO) <= 0) break;
            boolean paymentResult = hpProcessTerm(term, cifAccount); // Fixed method name (lowercase)
            if (!paymentResult) break;
        }

        return cifAccount.getBalance().compareTo(BigDecimal.ZERO) > 0;
    }

    // Example implementation (ensure these methods exist)
    private boolean smeProcessTerm(SMETerm term, CIFCurrentAccount cifAccount) {
        // Logic to process SME term payment
        return autoPaymentService.SmeProcessTerm(term,cifAccount);
    }

    private boolean hpProcessTerm(HpTerm term, CIFCurrentAccount cifAccount) {
        // Logic to process HP term payment
        return autoPaymentService.HpProcessTerm(term,cifAccount);
    }
    private void  SmeProcessLateLoan(SMELoan loan,List<SMETerm> allTerms, CIFCurrentAccount cifCurrentAccount) {
        autoPaymentService.processLateLoan(loan,allTerms,cifCurrentAccount);
    }
}