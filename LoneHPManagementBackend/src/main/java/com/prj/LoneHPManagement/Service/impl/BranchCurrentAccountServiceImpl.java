package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.Service.BranchCurrentAccountService;
import com.prj.LoneHPManagement.model.entity.BranchCurrentAccount;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import com.prj.LoneHPManagement.model.repo.BranchCurrentAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BranchCurrentAccountServiceImpl implements BranchCurrentAccountService {
    @Autowired
    private  BranchCurrentAccountRepository branchCurrentAccountRepository;
    @Override
    public BranchCurrentAccount getAccountByBranchId(int branchId) {
        return branchCurrentAccountRepository.findByBranch_Id(branchId)
                .orElseThrow(() -> new ServiceException(
                        "Account not found for branch ID: " + branchId
                ));
    }

    @Override
    public BranchCurrentAccount getAccountById(int accountId) {
        return branchCurrentAccountRepository.findById(accountId)
                .orElseThrow(() -> new ServiceException(
                        "Account not found for branch ID: " + accountId
                ));
    }
}
