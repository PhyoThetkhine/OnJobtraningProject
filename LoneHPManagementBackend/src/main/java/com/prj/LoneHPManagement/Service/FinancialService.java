package com.prj.LoneHPManagement.Service;

import com.prj.LoneHPManagement.model.entity.Collateral;
import com.prj.LoneHPManagement.model.entity.Financial;

import java.util.List;

public interface FinancialService {
    Financial createFinancial(int companyId, Financial financial);

    List<Financial> getAllFinancials();

    Financial getFinancialById(int id);

    Financial updateFinancial(int id, Financial financialDetails);

    void deleteFinancial(int id);

    Financial getFinancialByCompanyId(int companyId);
}
