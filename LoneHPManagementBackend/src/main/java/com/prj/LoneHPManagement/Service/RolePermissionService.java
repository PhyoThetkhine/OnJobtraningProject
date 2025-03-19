package com.prj.LoneHPManagement.Service;

public interface RolePermissionService {
    void addRolePermission(Integer roleId, Integer permissionId);
    void removeRolePermission(Integer roleId, Integer permissionId);
}
