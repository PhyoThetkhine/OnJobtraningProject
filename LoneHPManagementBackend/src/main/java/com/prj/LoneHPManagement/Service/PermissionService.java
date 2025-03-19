package com.prj.LoneHPManagement.Service;

import com.prj.LoneHPManagement.model.entity.Permission;
import com.prj.LoneHPManagement.model.entity.UserPermission;

import java.util.List;

public interface PermissionService {
    List<Permission> getAllPermissions();
    List<UserPermission> getUserPermissions(Integer userId);
    List<UserPermission> getUserPermissionsIsAllowed(Integer userId);
    List<Permission> getPermissionsForRole(Integer roleId);
    List<String> mergePermissions(List<Permission> rolePermissions, List<UserPermission> userPermissions);
}
