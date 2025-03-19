package com.prj.LoneHPManagement.model.dto;

import lombok.Data;

@Data
public class BranchUpdateDTO {
    private String branchName;
    private AddressDTO address;
}
