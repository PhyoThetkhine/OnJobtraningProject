package com.prj.LoneHPManagement.Service;

import com.prj.LoneHPManagement.model.entity.User;
import com.prj.LoneHPManagement.model.entity.UserCurrentAccount;

import java.math.BigDecimal;
import java.util.List;

public interface UserCurrentAccountService {
    UserCurrentAccount changeFreezeStatus(int accountId, String status);

    UserCurrentAccount saveCurrentAccount(UserCurrentAccount userCurrentAccount);

    UserCurrentAccount getAccountById(Integer id);

    UserCurrentAccount updateAccountById(Integer id , UserCurrentAccount userCurrentAccount);

    List<UserCurrentAccount> getAllAccount();
    UserCurrentAccount getAccountByUserId(int userId);

   // List<UserCurrentAccount> getAvailableAccounts();

    List<UserCurrentAccount> getFrozenAccounts(int isFreeze);

    UserCurrentAccount getAccountFreezeStatus(Integer id);

    UserCurrentAccount getAccountUnFreezeStatus(Integer id);

    List<UserCurrentAccount> getAllAccountByAccCode(String accCode);

    List<UserCurrentAccount> getAllAccountByUserId(Integer userId);

   // List<UserCurrentAccount> findByBalanceGreaterThan(BigDecimal balance);

}
