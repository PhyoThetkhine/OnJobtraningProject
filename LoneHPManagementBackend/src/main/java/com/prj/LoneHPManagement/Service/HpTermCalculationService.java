package com.prj.LoneHPManagement.Service;

import com.prj.LoneHPManagement.model.entity.HpTerm;
import com.prj.LoneHPManagement.model.entity.SMETerm;

public interface HpTermCalculationService {
    void calculateLateFee(HpTerm term);
    void updateOutstandingAmount(HpTerm term);
}
