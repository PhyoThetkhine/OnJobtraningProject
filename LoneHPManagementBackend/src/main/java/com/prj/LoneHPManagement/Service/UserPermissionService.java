package com.prj.LoneHPManagement.Service;

import com.prj.LoneHPManagement.model.entity.UserPermission;

public interface UserPermissionService {
    UserPermission updateUserPermission(int userId, int permissionId, String status);
}
