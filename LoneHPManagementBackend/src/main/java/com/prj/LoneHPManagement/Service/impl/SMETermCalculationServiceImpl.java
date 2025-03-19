package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.Service.SMETermCalculationService;
import com.prj.LoneHPManagement.model.entity.ConstraintEnum;
import com.prj.LoneHPManagement.model.entity.SMELoan;
import com.prj.LoneHPManagement.model.entity.SMETerm;
import com.prj.LoneHPManagement.model.repo.SMETermRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Service
public class SMETermCalculationServiceImpl implements SMETermCalculationService {
    @Autowired
    private SMETermRepository smeTermRepository;
    @Override
    public void calculateLateFee(SMETerm term) {
        // Convert the term's due date to LocalDateTime.
        LocalDateTime dueDateTime = term.getDueDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        // Calculate the total number of days past the due date.
        long totalLateDays = ChronoUnit.DAYS.between(dueDateTime, LocalDateTime.now());

        // Retrieve the grace period (in days) from the associated loan.
        int gracePeriodDays = term.getSmeLoan().getGracePeriod();

        // If the term is not past due or within the grace period, set fee to zero.
        if (totalLateDays <= gracePeriodDays) {
            term.setInterestLateFee(BigDecimal.ZERO);
            term.setInterestLateDays((int) totalLateDays);
        } else {

            BigDecimal interestOfOverdue = term.getInterestOfOverdue();

            // Retrieve the late fee rate from the associated loan.
            BigDecimal lateFeeRate = BigDecimal.valueOf(term.getSmeLoan().getLateFeeRate());

            // Calculate the late fee using the formula:
            // fee = InterestOfOverdue * (lateFeeRate / 100) * effectiveLateDays, rounded to 2 decimals.
            BigDecimal fee = interestOfOverdue
                    .multiply(lateFeeRate)
                    .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(totalLateDays))
                    .setScale(2, RoundingMode.HALF_UP);

            term.setInterestLateFee(fee);
            // Optionally, store the total days past due, or only the effective late days.
            term.setInterestLateDays((int) totalLateDays);
        }

        // Persist the changes.
        smeTermRepository.save(term);
    }


//    @Override
//    public void calculateLateFee(SMETerm term) {
////        // Only calculate if the term is in PAST_DUE status (status 7)
////        if (term.getStatus() != ConstraintEnum.PAST_DUE.getCode()) {
////            return;
////        }
////
////        SMELoan smeLoan = term.getSmeLoan();
////        LocalDate dueDate = term.getDueDate().toLocalDate();
////        int gracePeriod = smeLoan.getGracePeriod();
////        BigDecimal lateFeeRate = BigDecimal.valueOf(smeLoan.getLateFeeRate());
////        BigDecimal principal = term.getPrincipal();
////        Date lastRepayDate = term.getLastRepayDate() != null
////                ? Date.valueOf(term.getLastRepayDate().toLocalDate())
////                : null;
////
////        // Output parameters
////        Integer lateDays = 0;
////        BigDecimal lateFee = BigDecimal.ZERO;
////
////        // Call the stored procedure
////        smeTermRepository.calculateLateFee(
////                Date.valueOf(dueDate), // Due date
////                gracePeriod,           // Grace period
////                lateFeeRate,           // Late fee rate
////                principal,             // Principal
////                lastRepayDate,         // Last repayment date
////                lateDays,              // Output: Late days
////                lateFee                // Output: Late fee
////        );
////
////        // Update the term object with the results
////        term.setInterestLateDays(lateDays);
////        term.setInterestLateFee(lateFee);
////        term.setInterest(term.getInterestOfOverdue());
//
//    }

    @Override
    public void updateOutstandingAmount(SMETerm term) {
        BigDecimal outstandingAmount = term.getInterest()
                .add(term.getInterestLateFee()).add(term.getInterestOfOverdue());
        term.setOutstandingAmount(outstandingAmount);
    }

}