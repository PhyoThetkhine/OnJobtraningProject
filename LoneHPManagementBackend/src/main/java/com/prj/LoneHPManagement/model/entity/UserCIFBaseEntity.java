package com.prj.LoneHPManagement.model.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass

public abstract class UserCIFBaseEntity {
    @Column(name = "name", nullable = false,  length = 100)
    private String name;
    @Column(name = "email", nullable = false,  length = 200)
    private String email;
    @Column(name = "phone_number", nullable = false,  length = 20)
    private String phoneNumber;
    @Column(name = "nrc", nullable = false,  length = 20)
    private String NRC;
    @Column(name = "nrc_front_photo", nullable = false,  length = 220)
    @JsonProperty("nrcFrontPhoto")
    private String NRCFrontPhoto;
    @Column(name = "nrc_back_photo", nullable = false,  length = 200)
    @JsonProperty("nrcBackPhoto")
    private String NRCBackPhoto;
    @Column(name = "date_of_birth", nullable = false)
    private Date dateOfBirth;
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false , length = 6)
    private Gender gender;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;
    @ManyToOne
    @JoinColumn(name = "created_user_id", nullable = false)

    private User createdUser;
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;
    @Column(name = "photo_url", nullable = false,  length = 220)
    private String photo;

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

    public String getNRC() {
        return NRC;
    }

    public void setNRC(String NRC) {
        this.NRC = NRC;
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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public User getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(User createdUser) {
        this.createdUser = createdUser;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public enum Gender{
        MALE,FEMALE
    }
    @PrePersist
    protected void prePersist() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void preUpdate() {
        updatedDate = LocalDateTime.now();
    }
}

