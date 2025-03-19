package com.prj.LoneHPManagement.Service;

import com.prj.LoneHPManagement.model.entity.BranchCurrentAccount;

public interface BranchCurrentAccountService {
    BranchCurrentAccount getAccountByBranchId(int branchId);
}
