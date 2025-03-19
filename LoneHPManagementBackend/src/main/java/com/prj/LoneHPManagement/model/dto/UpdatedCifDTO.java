package com.prj.LoneHPManagement.model.dto;

import com.prj.LoneHPManagement.model.entity.CIF;
import com.prj.LoneHPManagement.model.entity.UserCIFBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatedCifDTO {
    private String name;
    private String email;
    private String phoneNumber;
    private String nrc;
    private String nrcFrontPhoto;
    private String nrcBackPhoto;
    private Date dateOfBirth;
    private UserCIFBaseEntity.Gender gender;
    private CIF.CIFType cifType;
    private AddressDTO address;
    private String photo;
}