package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.model.entity.ConstraintEnum;
import com.prj.LoneHPManagement.model.entity.UserCurrentAccount;
import com.prj.LoneHPManagement.model.exception.AccountNotFoundException;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import com.prj.LoneHPManagement.model.repo.UserCurrentAccountRepository;
import com.prj.LoneHPManagement.Service.UserCurrentAccountService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class UserCurrentAccountServiceImpl implements UserCurrentAccountService {

    private final UserCurrentAccountRepository userCurrentAccountRepository;

    public UserCurrentAccountServiceImpl(UserCurrentAccountRepository userCurrentAccountRepository) {
         this.userCurrentAccountRepository = userCurrentAccountRepository;
    }

    @Override
    public UserCurrentAccount saveCurrentAccount(UserCurrentAccount userCurrentAccount) {
        return userCurrentAccountRepository.save(userCurrentAccount);
    }
    @Override
    public UserCurrentAccount getAccountByUserId(int userId) {
        return userCurrentAccountRepository.findByUserId(userId).orElse(null);
    }

    @Override
    public UserCurrentAccount getAccountById(Integer id) {
        return userCurrentAccountRepository.findById(id).orElse(null);
    }

    @Override
    public UserCurrentAccount updateAccountById(Integer id, UserCurrentAccount userCurrentAccount) {
        if(userCurrentAccountRepository.existsById(id)) {
            userCurrentAccount.setId(id);
            return userCurrentAccountRepository.save(userCurrentAccount);
        }
        return null;
    }
    @Override
    public UserCurrentAccount changeFreezeStatus(int accountId, String status) {
        UserCurrentAccount account = userCurrentAccountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        account.setIsFreeze(status.equals(ConstraintEnum.IS_FREEZE.getDescription()) ? ConstraintEnum.IS_FREEZE.getCode() : ConstraintEnum.NOT_FREEZE.getCode());
        return userCurrentAccountRepository.save(account);
    }

    @Override
    public List<UserCurrentAccount> getAllAccount() {
        return userCurrentAccountRepository.findAll();
    }

//    @Override
//    public List<UserCurrentAccount> getAvailableAccounts() {
//        return userCurrentAccountRepository.findAvailableAccounts();
//    }

    @Override
    public List<UserCurrentAccount> getFrozenAccounts(int isFreeze) {
        return userCurrentAccountRepository.findByIsFreeze(ConstraintEnum.IS_FREEZE.getCode());
    }

    @Override
    public UserCurrentAccount getAccountFreezeStatus(Integer id) {
        // Fetch the account, throw exception if not found
        UserCurrentAccount userCurrentAccount = userCurrentAccountRepository.findById(id)
                .orElseThrow(() -> new ServiceException("Account not found with id: " + id));

        // Update freeze status and timestamp
        userCurrentAccount.setIsFreeze(ConstraintEnum.IS_FREEZE.getCode());
        userCurrentAccount.setUpdatedDate(LocalDateTime.now());

        // Save and return updated account
        return userCurrentAccountRepository.save(userCurrentAccount);
    }


    @Override
    public UserCurrentAccount getAccountUnFreezeStatus(Integer id) {
        UserCurrentAccount userCurrentAccount = userCurrentAccountRepository.findById(id).orElse(null);
        assert userCurrentAccount != null;
        userCurrentAccount.setIsFreeze((ConstraintEnum.NOT_FREEZE.getCode()));
        userCurrentAccount.setUpdatedDate(LocalDateTime.now());

        return userCurrentAccountRepository.save(userCurrentAccount);
    }

    @Override
    public List<UserCurrentAccount> getAllAccountByAccCode(String accCode) {
        return userCurrentAccountRepository.findByAccCode(accCode);
    }

    @Override
    public List<UserCurrentAccount> getAllAccountByUserId(Integer userId) {
        return userCurrentAccountRepository.findByUserId(userId);
    }
//
//    @Override
//    public List<UserCurrentAccount> findByBalanceGreaterThan(BigDecimal balance) {
//        return userCurrentAccountRepository.findByBalanceGreaterThan(balance);
//    }



}
