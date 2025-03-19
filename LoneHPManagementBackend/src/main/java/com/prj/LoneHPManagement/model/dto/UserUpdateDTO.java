package com.prj.LoneHPManagement.model.dto;

import com.prj.LoneHPManagement.model.entity.UserCIFBaseEntity;
import lombok.Data;

import java.sql.Date;

@Data
public class UserUpdateDTO {
    private String name;
    private String email;
    private String phoneNumber;
    private String nrc;
    private String nrcFrontPhoto;
    private String nrcBackPhoto;
    private Date dateOfBirth;
    private UserCIFBaseEntity.Gender gender; // Alternatively, you can use an enum if needed
    private String photo;
    private AddressDTO address;
}
