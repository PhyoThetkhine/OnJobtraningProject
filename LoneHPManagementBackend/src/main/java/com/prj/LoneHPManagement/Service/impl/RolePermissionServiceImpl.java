package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.Service.RolePermissionService;
import com.prj.LoneHPManagement.model.entity.*;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import com.prj.LoneHPManagement.model.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RolePermissionServiceImpl implements RolePermissionService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserPermissionRepository userPermissionRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public RolePermissionServiceImpl(RoleRepository roleRepository,
                                     PermissionRepository permissionRepository,
                                     RolePermissionRepository rolePermissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Override
    public void addRolePermission(Integer roleId, Integer permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ServiceException("Role not found with id: " + roleId));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ServiceException("Permission not found with id: " + permissionId));

        RolePermission rolePermission = new RolePermission();
        RolePermission.RolePermissionPK id = new RolePermission.RolePermissionPK();
        id.setRoleId(roleId);
        id.setPermissionId(permissionId);
        rolePermission.setId(id);
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);

        rolePermissionRepository.save(rolePermission);
        // Retrieve the currently logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUserDetails = (UserDetails) authentication.getPrincipal();
        User allowedUser  = userRepository.findByUserCode(currentUserDetails.getUsername());

        // Update UserPermissions
        List<User> usersWithRole = userRepository.findByRole_Id(roleId); // Assuming you have this method
        for (User user : usersWithRole) {
            UserPermission userPermission = new UserPermission();
            UserPermission.UserPermissionPK userPk = new UserPermission.UserPermissionPK();
            userPk.setUserId(user.getId());
            userPk.setPermissionId(permissionId);
            userPermission.setId(userPk);
            userPermission.setUser (user);
            userPermission.setPermission(permission);
            userPermission.setIsAllowed(ConstraintEnum.ALLOWED.getCode());
            userPermission.setAllowedDate(LocalDateTime.now());
            userPermission.setAllowedUser (allowedUser); // Assuming allowedUser  is the same user
            userPermission.setLimitedDate(LocalDateTime.now().plusYears(1)); // Example

            userPermissionRepository.save(userPermission);
        }
    }

    @Override
    public void removeRolePermission(Integer roleId, Integer permissionId) {
        RolePermission.RolePermissionPK id = new RolePermission.RolePermissionPK();
        id.setRoleId(roleId);
        id.setPermissionId(permissionId);
        rolePermissionRepository.deleteById(id); // This should work if the repository is set up correctly

        // Remove UserPermissions
        List<User> usersWithRole = userRepository.findByRole_Id(roleId); // Assuming you have this method
        for (User  user : usersWithRole) {
            UserPermission.UserPermissionPK userPk = new UserPermission.UserPermissionPK();
            userPk.setUserId(user.getId());
            userPk.setPermissionId(permissionId);
            userPermissionRepository.deleteById(userPk); // Delete the UserPermission
        }
    }
}