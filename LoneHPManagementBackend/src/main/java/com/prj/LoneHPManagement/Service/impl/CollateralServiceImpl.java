package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.Service.CollateralService;
import com.prj.LoneHPManagement.model.entity.Collateral;
import com.prj.LoneHPManagement.model.entity.SMELoan;
import com.prj.LoneHPManagement.model.entity.User;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import com.prj.LoneHPManagement.model.repo.CollateralRepository;
import com.prj.LoneHPManagement.model.repo.SMELoanRepository;
import com.prj.LoneHPManagement.model.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollateralServiceImpl implements CollateralService {

    @Autowired
    private CollateralRepository collateralRepository;
    @Autowired
    private SMELoanRepository smeLoanRepository;

    @Autowired
    private UserRepository userRepository;
    @Override
    public Page<Collateral> getCollateralsByCifId(int cifId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return collateralRepository.findByCIF_Id(cifId, pageable);
    }

    public Collateral getRoleById(Integer id) {
        return collateralRepository.findById(id).orElseThrow(() -> new ServiceException("Role not found"));
    }

//    // Create or update a Collateral
//    public Collateral saveCollateral(Collateral collateral) {
//        SMELoan loan = smeLoanRepository.findById(collateral.getLoan().getId()).orElseThrow(() -> new ServiceException("Loan not found"));
//        User createdUser = userRepository.findById(collateral.getCreatedUser().getId()).orElseThrow(() -> new ServiceException("User not found"));
//       collateral.setLoan(loan);
//       collateral.setCreatedUser(createdUser);
//        return collateralRepository.save(collateral);
//    }

    // Get all Collaterals
    public List<Collateral> getAllCollaterals() {
        return collateralRepository.findAll();
    }

    // Get a Collateral by ID
    public Collateral getCollateralById(int id) {
        Collateral collateral = collateralRepository.findById(id)
                .orElseThrow(() -> new ServiceException("Collateral not found with id: " + id));
//
//        // Fetch the related Loan and User, just in case they are not already loaded
//        SMELoan loan = smeLoanRepository.findById(collateral.getLoan().getId()).orElseThrow(() -> new ServiceException("Loan not found with id: " + collateral.getLoan().getId()));
//        User createdUser = userRepository.findById(collateral.getCreatedUser().getId()).orElseThrow(() -> new ServiceException("User not found with id: " + collateral.getCreatedUser().getId()));
//
//        collateral.setLoan(loan);
//        collateral.setCreatedUser(createdUser);

        return collateral;
    }

    @Override
    public List<Collateral> getCollateralByCIFId(int cifid) {
        return collateralRepository.findByCIF_Id(cifid);
    }

    @Override
    public List<Collateral> getCollateralByLoanId(int loanId) {
        return List.of();
    }

    // Delete a Collateral by ID
    public void deleteCollateral(int id) {
        collateralRepository.deleteById(id);
    }

    @Override
    public Page<Collateral> getCollateralsByLoanId(int loanId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return collateralRepository.findByLoanId(loanId, pageable);
    }

}