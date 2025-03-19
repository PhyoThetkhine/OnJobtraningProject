package com.prj.LoneHPManagement.model.dto;

import com.prj.LoneHPManagement.model.entity.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateRoleRequest {
    private String roleName;
    private Role.AUTHORITY authority;
    private List<Integer> permissions;
}