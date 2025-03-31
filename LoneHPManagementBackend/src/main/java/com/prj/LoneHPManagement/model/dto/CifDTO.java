package com.prj.LoneHPManagement.model.dto;

import com.prj.LoneHPManagement.model.entity.CIF;
import com.prj.LoneHPManagement.model.entity.UserCIFBaseEntity;
import lombok.Data;

import java.sql.Date;

@Data
public class CifDTO {

    private int id;
    private String cifCode;
    private String gender;
    private String name;
    private String email;
    private String phoneNumber;
    private Date dateOfBirth;
    private String NRC;
    private String NRCFrontPhoto;
    private String NRCBackPhoto;
    private Integer createdUserId;
    private String userCode;
    private Integer addressId;
    private String cifType;
    private int status;

    public CifDTO(int id, String cifCode, String name, String email, UserCIFBaseEntity.Gender gender, Date dateOfBirth, String phoneNumber, String nrc, CIF.CIFType cifType) {
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public CifDTO(int id, String cifCode, String name, String email, String phoneNumber, Date dateOfBirth,
                  String nrc, String nrcFrontPhoto, String nrcBackPhoto, String userCode, String branchName,
                  String state, String city, String township, String additionalAddress, Integer createdUserId,
                  Integer addressId) {
        this.id = id;
        this.cifCode = cifCode;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.NRC = nrc;
        this.NRCFrontPhoto = nrcFrontPhoto;
        this.NRCBackPhoto = nrcBackPhoto;
        this.userCode = userCode;
        this.branchName = branchName;
        this.state = state;
        this.city = city;
        this.township = township;
        this.additionalAddress = additionalAddress;
        this.createdUserId = createdUserId;
        this.addressId = addressId;
    }

    public Integer getCreatedUserId() {
        return createdUserId;
    }

    public void setCreatedUserId(Integer createdUserId) {
        this.createdUserId = createdUserId;
    }

    public String getNRCFrontPhoto() {
        return NRCFrontPhoto;
    }

    public void setNRCFrontPhoto(String NRCFrontPhoto) {
        this.NRCFrontPhoto = NRCFrontPhoto;
    }

    public String getNRCBackPhoto() {
        return NRCBackPhoto;
    }

    public void setNRCBackPhoto(String NRCBackPhoto) {
        this.NRCBackPhoto = NRCBackPhoto;
    }

    private String branchName;
    private String state;
    private String city;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCifType() {
        return cifType;
    }

    public void setCifType(String cifType) {
        this.cifType = cifType;
    }

    private String township;
    private String additionalAddress;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTownship() {
        return township;
    }

    public void setTownship(String township) {
        this.township = township;
    }

    public String getAdditionalAddress() {
        return additionalAddress;
    }

    public void setAdditionalAddress(String additionalAddress) {
        this.additionalAddress = additionalAddress;
    }

    public CifDTO(int id, String cifCode, String name, String email, String phoneNumber,
                  Date dateOfBirth, String NRC, String NRCFrontPhoto, String NRCBackPhoto, String userCode, String branchName,
                  String state, String city, String township, String additionalAddress, String gender, Integer createdUserId
                    , Integer addressId) {
        this.id = id;
        this.cifCode = cifCode;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.NRC = NRC;
        this.NRCFrontPhoto = NRCFrontPhoto;
        this.NRCBackPhoto = NRCBackPhoto;
        this.userCode = userCode;
        this.branchName = branchName;
        this.state = state;
        this.city = city;
        this.township = township;
        this.additionalAddress = additionalAddress;
        this.createdUserId = createdUserId;
        this.addressId = addressId;
//        this.gender = gender;
    }

    public CifDTO() {

    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCifCode() {
        return cifCode;
    }

    public void setCifCode(String cifCode) {
        this.cifCode = cifCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNRC() {
        return NRC;
    }

    public void setNRC(String NRC) {
        this.NRC = NRC;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
}
