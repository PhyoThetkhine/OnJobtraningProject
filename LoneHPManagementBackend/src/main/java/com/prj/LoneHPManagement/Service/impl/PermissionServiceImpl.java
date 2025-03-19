package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.Service.PermissionService;
import com.prj.LoneHPManagement.model.entity.ConstraintEnum;
import com.prj.LoneHPManagement.model.entity.Permission;
import com.prj.LoneHPManagement.model.entity.UserPermission;
import com.prj.LoneHPManagement.model.repo.PermissionRepository;
import com.prj.LoneHPManagement.model.repo.RolePermissionRepository;
import com.prj.LoneHPManagement.model.repo.UserPermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private UserPermissionRepository userPermissionRepository;
    @Autowired
    private PermissionRepository permissionRepository;

    /**
     * Retrieve default permissions for a given role.
     * @param roleId The ID of the role.
     * @return List of permissions assigned to the role.
     */
    @Override
    public List<Permission> getPermissionsForRole(Integer roleId) {
        return rolePermissionRepository.findPermissionByRoleId(roleId);
    }

    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    public List<UserPermission> getUserPermissions(Integer userId) {
        return userPermissionRepository.findByUserId(userId);
    }

    @Override
    /**
     * Retrieve user-specific permissions (overrides).
     * @param userId The ID of the user.
     * @return List of user-specific permissions.
     */
    public List<UserPermission> getUserPermissionsIsAllowed(Integer userId) {
        return userPermissionRepository.findByUserIdAndIsAllowed(userId,ConstraintEnum.ALLOWED.getCode());
    }

    @Override
    /**
     * Merge role-based permissions with user-specific overrides.
     * @param rolePermissions Default role permissions.
     * @param userPermissions User-specific permission overrides.
     * @return List of effective permission names.
     */
    public List<String> mergePermissions(List<Permission> rolePermissions, List<UserPermission> userPermissions) {
        // Start with role permissions
        List<String> effectivePerms = rolePermissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toList());

        // Apply user-specific overrides
        userPermissions.forEach(userPerm -> {
            String permName = userPerm.getPermission().getName();
            if (userPerm.getIsAllowed() == ConstraintEnum.ALLOWED.getCode()) {
                if (!effectivePerms.contains(permName)) {
                    effectivePerms.add(permName); // Add if allowed
                }
            } else {
                effectivePerms.remove(permName); // Remove if denied
            }
        });

        return effectivePerms;
    }
}
