package com.prj.LoneHPManagement.Service.impl;
import com.prj.LoneHPManagement.model.dto.AccountLimitUpdateDTO;
import com.prj.LoneHPManagement.model.entity.*;

import com.prj.LoneHPManagement.Service.CIFCurrentAccountService;

import com.prj.LoneHPManagement.model.exception.AccountNotFoundException;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import com.prj.LoneHPManagement.model.exception.UserNotFoundException;
import com.prj.LoneHPManagement.model.repo.CIFCurrentAccountRepository;
import com.prj.LoneHPManagement.model.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CIFCurrentAccountServiceImpl implements CIFCurrentAccountService {

    @Autowired
    private CIFCurrentAccountRepository cifCurrentAccountRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public CIFCurrentAccount updateAccountLimits(int accountId, AccountLimitUpdateDTO updateDTO) {
        // Validate user exists
        User updatedBy = userRepository.findById(updateDTO.getUpdatedUserId())
                .orElseThrow(() -> new ServiceException("User not found with id: " + updateDTO.getUpdatedUserId()));

        CIFCurrentAccount account = cifCurrentAccountRepository.findById(accountId)
                .orElseThrow(() -> new ServiceException("Account not found with id: " + accountId));

        // Update account limits
        account.setMinAmount(updateDTO.getMinAmount());
        account.setMaxAmount(updateDTO.getMaxAmount());
        account.setUpdatedDate(LocalDateTime.now());

        return cifCurrentAccountRepository.save(account);
    }
    @Override
    public CIFCurrentAccount changeFreezeStatus(int accountId, String status) {
        CIFCurrentAccount account = cifCurrentAccountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        account.setIsFreeze(status.equals(ConstraintEnum.IS_FREEZE.getDescription()) ? ConstraintEnum.IS_FREEZE.getCode() : ConstraintEnum.NOT_FREEZE.getCode());
        return cifCurrentAccountRepository.save(account);
    }
    @Override
    public CIFCurrentAccount getAccountByCifId(int id) {
         return cifCurrentAccountRepository.findByCifId(id) .orElseThrow(() -> new RuntimeException("CIFCurrentAccount not found with ID: " + id));
    }

    @Override
    public CIFCurrentAccount getAccountById(int id) {
        return cifCurrentAccountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    @Override
    public CIFCurrentAccount saveCIFCurrentAccount(Integer cifId, CIFCurrentAccount account) {
        return null;
    }

    @Override
    public List<CIFCurrentAccount> getAllCIFCurrentAccounts() {
        return cifCurrentAccountRepository.findAll();
    }

    @Override
    public List<CIFCurrentAccount> getCIFCurrentAccountsByCifId(int cifId) {
        return List.of();
    }

//    @Override
//    public List<CIFCurrentAccount> getCIFCurrentAccountsByCifId(Integer cifId) {
//        List<CIFCurrentAccount> accounts = cifCurrentAccountRepository.findByCifId(cifId);
//        if (accounts.isEmpty()) {
//            throw new ServiceException("No CIF Current Accounts found with CIF ID: " + cifId);
//        }
//        return accounts;
//    }

    @Override
    public void deleteCIFCurrentAccount(int id) {
        cifCurrentAccountRepository.deleteById(id);
    }

    @Override
    public CIFCurrentAccount getCIFCurrentAccountById(int id) {
        CIFCurrentAccount cifCurrentAccount = cifCurrentAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CIFCurrentAccount not found with ID: " + id));
        if (cifCurrentAccount == null) {
            throw new ServiceException("CIF Current Account not found with id: " + id);
        }
        return cifCurrentAccount;
        }

    @Override
    public Page<CIFCurrentAccount> findByBranch(String branchCode, Pageable pageable) {
        return cifCurrentAccountRepository.findByBranchCode(branchCode, pageable);
    }
    @Override
    public List<CIFCurrentAccount> getByBranchCode(String branchCode, int userid) {
        User user = userRepository.findById(userid)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Role.AUTHORITY authority = user.getRole().getAuthority();
        if (authority == Role.AUTHORITY.MainBranchLevel) {
            return cifCurrentAccountRepository.findAll();
        } else {

            return cifCurrentAccountRepository.findByBranchCode(branchCode);
        }
    }

//    @Override
//    public Page<CIFCurrentAccount> findByCifId(int cifId, Pageable pageable) {
//        return cifCurrentAccountRepository.findByCifId(cifId, pageable);
//    }
}
