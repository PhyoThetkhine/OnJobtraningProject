package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.Service.CodeGenerateService;
import com.prj.LoneHPManagement.model.entity.Branch;
import com.prj.LoneHPManagement.model.entity.CIF;
import com.prj.LoneHPManagement.model.entity.User;
import com.prj.LoneHPManagement.model.entity.UserCurrentAccount;
import com.prj.LoneHPManagement.model.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CodeGenerateServiceImpl implements CodeGenerateService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserCurrentAccountRepository userCurrentAccountRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private CIFRepository cifRepository;
    @Autowired
    private SMELoanRepository smeLoanRepository;
    @Autowired
    private HpLoanRepository hpLoanRepository;

    @Override
    public String generateUserCode(Branch branch) {
        String branchCode = branch.getBranchCode();
        String maxUserCode = userRepository.findMaxUserCodeByBranch(branch);
        int sequence = 1;

        if (maxUserCode != null && maxUserCode.startsWith(branchCode)) {
            String sequencePart = maxUserCode.substring(branchCode.length());
            try {
                sequence = Integer.parseInt(sequencePart) + 1;
            } catch (NumberFormatException e) {
                // Log error and default to sequence = 1
            }
        }
        return branchCode + String.format("%05d", sequence);
    }
    @Override
    public String generateBranchCode() {
        String maxBranchCode = branchRepository.findMaxBranchCode(); // Find the latest branch code
        int sequence = 1; // Default sequence number

        // If there are existing branches, extract the sequence number and increment it
        if (maxBranchCode != null) {
            // Extract the numeric part of the existing branch code
            String sequencePart = maxBranchCode; // No prefix, so we take the whole code
            try {
                sequence = Integer.parseInt(sequencePart) + 1; // Increment the sequence
            } catch (NumberFormatException e) {
                // Log error and default to sequence = 1
            }
        }

        // Format the sequence to 3 digits
        return String.format("%03d", sequence); // Return the branch code without a prefix
    }



    @Override
    public String generateUserAccountCode(User user) {
        String prefix = "UAC"; // Fixed prefix for account codes
        String userCode = user.getUserCode(); // Get the user code (e.g., "00100001")
        // Concatenate the prefix and user code
        return prefix + userCode;
    }
    @Override
    public String generateBranchAccountCode(Branch Branch) {
        String prefix = "BAC"; // Fixed prefix for account codes
        String BranchCode = Branch.getBranchCode(); // Get the user code (e.g., "00100001")
        // Concatenate the prefix and user code
        return prefix + BranchCode;
    }

    @Override
    public String generateCIFAccountCode(CIF cif){
        String prefix = "CAC";
        String cifCode = cif.getCifCode();
        return prefix + cifCode;
    }

    @Override
    public String generateCIFCode(User createdUser ) {
        String userCode = createdUser .getUserCode(); // Get the user code of the creator
        String maxCIFCode = cifRepository.findMaxCIFCodeByUser(createdUser); // Get the max CIF code for the user
        int sequence = 1;

        // Check if there is an existing CIF code for the user
        if (maxCIFCode != null && maxCIFCode.startsWith(userCode)) {
            String sequencePart = maxCIFCode.substring(userCode.length());
            try {
                sequence = Integer.parseInt(sequencePart) + 1; // Increment the sequence
            } catch (NumberFormatException e) {
                // Log error and default to sequence = 1
                System.err.println("Error parsing sequence part: " + e.getMessage());
            }
        }
        return userCode + String.format("%05d", sequence); // Return the CIF code
    }
    @Override
    public String generateLoanCode(CIF cif) {
        String cifCode = cif.getCifCode(); // Get the CIF code
        String maxLoanCode = smeLoanRepository.findMaxSMELoanCodeByCif(cif);
        int sequence = 1;
        // Check if there is an existing CIF code for the user
        if (maxLoanCode != null && maxLoanCode.startsWith(cifCode)) {
            String sequencePart = maxLoanCode.substring(cifCode.length());
            try {
                sequence = Integer.parseInt(sequencePart) + 1; // Increment the sequence
            } catch (NumberFormatException e) {
                // Log error and default to sequence = 1
                System.err.println("Error parsing sequence part: " + e.getMessage());
            }
        }
        return cifCode + String.format("%05d", sequence); // Return the CIF code
    }

    @Override
    public String generateHpLoanCode(CIF cif) {
        String cifCode = cif.getCifCode(); // Get the CIF code
        String maxLoanCode = hpLoanRepository.findMaxHpLoanCodeByCif(cif);
        int sequence = 1;
        // Check if there is an existing CIF code for the user
        if (maxLoanCode != null && maxLoanCode.startsWith(cifCode)) {
            String sequencePart = maxLoanCode.substring(cifCode.length());
            try {
                sequence = Integer.parseInt(sequencePart) + 1; // Increment the sequence
            } catch (NumberFormatException e) {
                // Log error and default to sequence = 1
                System.err.println("Error parsing sequence part: " + e.getMessage());
            }
        }
        return cifCode + String.format("%05d", sequence); // Return the CIF code
    }
}

