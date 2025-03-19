package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.model.entity.Collateral;
import com.prj.LoneHPManagement.model.repo.CollateralRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CollateralService {

    @Autowired
    private CollateralRepository collateralRepository;

    // Create or update a Collateral
    public Collateral saveCollateral(Collateral collateral) {
        return collateralRepository.save(collateral);
    }

    // Get all Collaterals
    public List<Collateral> getAllCollaterals() {
        return collateralRepository.findAll();
    }

    // Get a Collateral by ID
    public Optional<Collateral> getCollateralById(int id) {
        return collateralRepository.findById(id);
    }

    // Delete a Collateral by ID
    public void deleteCollateral(int id) {
        collateralRepository.deleteById(id);
    }
}