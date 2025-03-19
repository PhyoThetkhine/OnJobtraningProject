package com.prj.LoneHPManagement.model.dto;


import com.prj.LoneHPManagement.model.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRoleRequest {
    private String roleName;
    private Role.AUTHORITY authority;
}