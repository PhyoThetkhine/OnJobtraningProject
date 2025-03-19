package com.prj.LoneHPManagement.Service;

import com.prj.LoneHPManagement.model.entity.SMELoan;
import com.prj.LoneHPManagement.model.entity.SMETerm;

import java.math.BigDecimal;

public interface SMETermCalculationService {
    void calculateLateFee(SMETerm term);
     void updateOutstandingAmount(SMETerm term);
}
