// ClientRegistrationDTO.java
package com.prj.LoneHPManagement.model.dto;

import com.prj.LoneHPManagement.model.entity.CIF.CIFType;
import com.prj.LoneHPManagement.model.entity.UserCIFBaseEntity.Gender;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class ClientRegistrationDTO {
    private String name;
    private String email;
    private String phoneNumber;
    private String NRC;
    private String photo;
    private String nrcFrontPhoto;
    private String nrcBackPhoto;
    private Date dateOfBirth;
    private Gender gender;
    private CIFType cifType;
    private AddressDTO address;
    private String status;
    private AccountDTO account;
    private BusinessRegistrationDTO business;
    private Integer createdUserId;
}

// AddressDTO.java


// AccountDTO.java



