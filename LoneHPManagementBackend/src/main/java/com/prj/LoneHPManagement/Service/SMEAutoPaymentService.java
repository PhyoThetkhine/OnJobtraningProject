package com.prj.LoneHPManagement.Service;

import com.prj.LoneHPManagement.model.entity.CIFCurrentAccount;
import com.prj.LoneHPManagement.model.entity.SMELoan;
import com.prj.LoneHPManagement.model.entity.SMETerm;

import java.util.List;

public interface SMEAutoPaymentService {
    void processSMEAccountPaymentsForTransaction(CIFCurrentAccount cifAccount);
    void processAutoPayments();
} 