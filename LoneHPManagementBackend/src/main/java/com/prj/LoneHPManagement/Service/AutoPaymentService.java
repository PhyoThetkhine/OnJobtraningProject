package com.prj.LoneHPManagement.Service;

import com.prj.LoneHPManagement.model.entity.*;

import java.util.List;

public interface AutoPaymentService {
    void processTransaction(Transaction transaction);
     boolean SmeProcessTerm(SMETerm term, CIFCurrentAccount cifAccount);
     boolean HpProcessTerm(HpTerm term, CIFCurrentAccount cifAccount);
    void processLateLoan(SMELoan loan, List<SMETerm> allTerms, CIFCurrentAccount cifAccount);
}
