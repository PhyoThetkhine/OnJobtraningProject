package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.model.entity.ConstraintEnum;
import com.prj.LoneHPManagement.model.entity.UserPermission;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import com.prj.LoneHPManagement.model.repo.UserPermissionRepository;
import com.prj.LoneHPManagement.Service.UserPermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserPermissionServiceImpl implements UserPermissionService {

    private final UserPermissionRepository userPermissionRepository;

    public UserPermissionServiceImpl(UserPermissionRepository userPermissionRepository) {
        this.userPermissionRepository = userPermissionRepository;
    }

    @Override
    @Transactional
    public UserPermission updateUserPermission(int userId, int permissionId, String status) {
        UserPermission userPermission = userPermissionRepository.findById(new UserPermission.UserPermissionPK(userId, permissionId))
                .orElseThrow(() -> new ServiceException("User Permission not found for userId: " + userId + " and permissionId: " + permissionId));

        // Update the isAllowed status based on the provided status
        if ("allow".equalsIgnoreCase(status)) {
            userPermission.setIsAllowed(ConstraintEnum.ALLOWED.getCode());
        } else if ("limited".equalsIgnoreCase(status)) {
            userPermission.setIsAllowed(ConstraintEnum.LIMITED.getCode());
        } else {
            throw new ServiceException("Invalid status: " + status);
        }

        return userPermissionRepository.save(userPermission);
    }
}