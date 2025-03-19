package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.Service.HpTermCalculationService;
import com.prj.LoneHPManagement.model.entity.ConstraintEnum;
import com.prj.LoneHPManagement.model.entity.HpLoan;
import com.prj.LoneHPManagement.model.entity.HpTerm;
import com.prj.LoneHPManagement.model.repo.HpTermRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

@Service
public class HpTermCalculationServiceImpl implements HpTermCalculationService {
    @Autowired
    private HpTermRepository hpTermRepository;
    @Override
    public void calculateLateFee(HpTerm term) {
        // Only calculate if the term is in PAST_DUE status (status 7)
        if (term.getStatus() != ConstraintEnum.PAST_DUE.getCode()) {
            return;
        }

        HpLoan hpLoan = term.getHpLoan();
        LocalDate dueDate = term.getDueDate().toLocalDate();
        int gracePeriod = hpLoan.getGracePeriod();
        BigDecimal lateFeeRate = BigDecimal.valueOf(hpLoan.getLateFeeRate());

        // Get overdue amounts
        BigDecimal principalOverdue = term.getPrincipalOfOverdue();
        BigDecimal interestOverdue = term.getInterestOfOverdue();

        Date lastRepayDate = term.getLastRepayDate() != null
                ? Date.valueOf(term.getLastRepayDate().toLocalDate())
                : null;

        // Output parameters
        Integer principalLateDays = 0;
        BigDecimal principalLateFee = BigDecimal.ZERO;
        Integer interestLateDays = 0;
        BigDecimal interestLateFee = BigDecimal.ZERO;

        // Call the HP-specific stored procedure
        hpTermRepository.calculateHPLateFee(
                Date.valueOf(dueDate),   // Due date
                gracePeriod,           // Grace period
                lateFeeRate,            // Late fee rate
                principalOverdue,      // Principal overdue amount
                interestOverdue,        // Interest overdue amount
                lastRepayDate,         // Last repayment date
                principalLateDays,      // Output: Principal late days
                principalLateFee,       // Output: Principal late fee
                interestLateDays,       // Output: Interest late days
                interestLateFee         // Output: Interest late fee
        );

        // Update all calculated fields
        term.setLatePrincipalDays(principalLateDays);
        term.setPrincipalLateFee(principalLateFee);
        term.setInterestLateDays(interestLateDays);
        term.setInterestLateFee(interestLateFee);
    }

    @Override
    public void updateOutstandingAmount(HpTerm term) {
        BigDecimal outstandingAmount = term.getInterest().add(term.getPrincipal()).
                add(term.getPrincipalOfOverdue()).add(term.getPrincipalLateFee())
                .add(term.getInterestLateFee()).add(term.getInterestOfOverdue());
        term.setOutstandingAmount(outstandingAmount);

    }
}
