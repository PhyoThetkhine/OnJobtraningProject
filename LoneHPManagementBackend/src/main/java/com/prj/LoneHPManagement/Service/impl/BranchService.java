package com.prj.LoneHPManagement.Service.impl;


import com.prj.LoneHPManagement.Service.CodeGenerateService;
import com.prj.LoneHPManagement.model.dto.BranchDTO;
import com.prj.LoneHPManagement.model.dto.BranchUpdateDTO;
import com.prj.LoneHPManagement.model.entity.Address;
import com.prj.LoneHPManagement.model.entity.Branch;
import com.prj.LoneHPManagement.model.entity.BranchCurrentAccount;
import com.prj.LoneHPManagement.model.entity.User;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import com.prj.LoneHPManagement.model.repo.AddressRepository;
import com.prj.LoneHPManagement.model.repo.BranchCurrentAccountRepository;
import com.prj.LoneHPManagement.model.repo.BranchRepository;
import com.prj.LoneHPManagement.model.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
public class BranchService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private BranchCurrentAccountRepository branchCurrentAccountRepository;
    @Autowired
    private  CodeGenerateService codeGenerateService;

//    public Branch createBranch(Branch branch, int addressID, int createdUserId){
//        Address address = addressRepository.findById(addressID)
//                .orElseThrow(() -> new ServiceException("Address Not Found"));
//
//        User user = userRepository.findById(createdUserId)
//                .orElseThrow(() -> new ServiceException("User Not Found"));
//
//        if(branchRepository.findByBranchCode(branch.getBranchCode()) != null){
//            throw new ServiceException("Branch Code is already have");
//        }
//        branch.setAddress(address);
//        branch.setCreatedUser(user);
//
//        return  branchRepository.save(branch);
//
//    }

    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }
    public Page<Branch> getAllbranches(Pageable pageable) {
        return branchRepository.findAll(pageable);
    }
    public Branch createBranch(Branch branch) {
        Address address = new Address();
        address.setTownship(branch.getAddress().getTownship());
        address.setState(branch.getAddress().getState());
        address.setCity(branch.getAddress().getCity());
        address.setAdditionalAddress(branch.getAddress().getAdditionalAddress());
        Address savedAddress = addressRepository.save(address);
        branch.setBranchCode(codeGenerateService.generateBranchCode());
        branch.setBranchName(branch.getBranchName());
        branch.setAddress(savedAddress);
        branch.setCreatedUser(branch.getCreatedUser());
//
//        User user = new User();
//        user.setId(branch.getId());
//        User savedUser = userRepository.save(user);

        // Save the branch
        Branch savedBranch = branchRepository.save(branch);

        // Auto-create BranchCurrentAccount
        BranchCurrentAccount account = new BranchCurrentAccount();
        account.setAccCode(codeGenerateService.generateBranchAccountCode(savedBranch)); // Use savedBranch
        account.setBranch(savedBranch); // Link to the saved branch
        account.setBalance(BigDecimal.ZERO); // Initialize balance

        // Save the account
        branchCurrentAccountRepository.save(account);

        return savedBranch;

    }

    public Branch updateBranch(int id, BranchUpdateDTO dto) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + id));

        // Update branch name
        branch.setBranchName(dto.getBranchName());

        // Update associated address
        Address address = branch.getAddress();
        address.setState(dto.getAddress().getState());
        address.setTownship(dto.getAddress().getTownship());
        address.setCity(dto.getAddress().getCity());
        address.setAdditionalAddress(dto.getAddress().getAdditionalAddress());
        branch.setAddress(address);

        return  branchRepository.save(branch);
    }

    public Branch getBranchById(int id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new ServiceException("Branch Id Not Found"));
    }


    public Branch getBranchByCode(String branchCode) {
        Branch branch = branchRepository.findByBranchCode(branchCode);

        if (branch == null) {
            throw new ServiceException("Branch Code Not Found");
        }

        return branch;
    }
}
