package com.prj.LoneHPManagement.Service;

import com.prj.LoneHPManagement.model.dto.CollateralRequest;
import com.prj.LoneHPManagement.model.dto.CollateralUpdateRequest;
import com.prj.LoneHPManagement.model.entity.Collateral;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CollateralService {
    Collateral updateCollateral(int id, CollateralUpdateRequest request);
    //     Collateral saveCollateral(Collateral collateral);
 Collateral create(CollateralRequest dto);
   Page<Collateral> getCollateralsByLoanId(int loanId, int page, int size, String sortBy);
    List<Collateral> getAllCollaterals();
    Page<Collateral> getCollateralsByCifId(int cifId, int page, int size, String sortBy);
    Collateral getCollateralById(int id);
    List<Collateral> getCollateralByCIFId(int cifid);
    List<Collateral> getCollateralByLoanId(int loanId);

    void deleteCollateral(int id);
}
