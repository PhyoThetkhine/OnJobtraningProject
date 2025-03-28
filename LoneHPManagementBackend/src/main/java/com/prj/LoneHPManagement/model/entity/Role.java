package com.prj.LoneHPManagement.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "authority", nullable = false)
    private AUTHORITY authority;

    public enum AUTHORITY {
        MainBranchLevel, RegularBranchLevel
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