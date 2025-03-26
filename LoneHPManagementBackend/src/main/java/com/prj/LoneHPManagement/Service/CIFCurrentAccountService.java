package com.prj.LoneHPManagement.Service;

import com.prj.LoneHPManagement.model.entity.CIFCurrentAccount;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
public interface CIFCurrentAccountService {
    List<CIFCurrentAccount> getByBranchCode(String branchCode,int userid);
    CIFCurrentAccount getAccountByCifId(int id);
    CIFCurrentAccount getAccountById(int id);
    CIFCurrentAccount changeFreezeStatus(int accountId, String status);

    CIFCurrentAccount saveCIFCurrentAccount(Integer cifId, CIFCurrentAccount account);

    List<CIFCurrentAccount> getAllCIFCurrentAccounts();

    List<CIFCurrentAccount> getCIFCurrentAccountsByCifId(int cifId);

    void deleteCIFCurrentAccount(int id);
    CIFCurrentAccount getCIFCurrentAccountById(int id);

    Page<CIFCurrentAccount> findByBranch(String branchCode, Pageable pageable);

    //Page<CIFCurrentAccount> findByCifId(int cifId, Pageable pageable);
}
