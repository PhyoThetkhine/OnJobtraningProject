package com.prj.LoneHPManagement.Service;

import com.prj.LoneHPManagement.model.entity.CIFCurrentAccount;

public interface PaymentTrigger {
    void processHpAccountPaymentsForTransaction(CIFCurrentAccount cifAccount);
    void processHpAutoPayments();
}
