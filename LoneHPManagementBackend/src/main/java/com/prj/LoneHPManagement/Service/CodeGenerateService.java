package com.prj.LoneHPManagement.Service;

import com.prj.LoneHPManagement.model.entity.Branch;
import com.prj.LoneHPManagement.model.entity.CIF;
import com.prj.LoneHPManagement.model.entity.User;

public interface CodeGenerateService {
    String generateBranchAccountCode(Branch Branch);
    String generateUserCode(Branch branch);
    String generateUserAccountCode(User user);
    String generateCIFCode(User createdUser );
    String generateCIFAccountCode(CIF cif);
    String generateBranchCode();
    String generateLoanCode(CIF cif);
    String generateHpLoanCode(CIF cif);
}