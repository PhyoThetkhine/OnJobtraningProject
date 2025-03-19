package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.model.dto.CreateRoleRequest;
import com.prj.LoneHPManagement.model.dto.PermissionDTO;
import com.prj.LoneHPManagement.model.dto.UpdateRoleRequest;
import com.prj.LoneHPManagement.model.entity.Permission;
import com.prj.LoneHPManagement.model.entity.Role;
import com.prj.LoneHPManagement.Service.RoleService;
import com.prj.LoneHPManagement.model.entity.RolePermission;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import com.prj.LoneHPManagement.model.repo.PermissionRepository;
import com.prj.LoneHPManagement.model.repo.RolePermissionRepository;
import com.prj.LoneHPManagement.model.repo.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    @Override
    @Transactional
    public Role updateRole(Integer roleId, UpdateRoleRequest updateRoleRequest) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ServiceException("Role not found with id: " + roleId));

        // Update role fields
        role.setRoleName(updateRoleRequest.getRoleName());
        role.setAuthority(updateRoleRequest.getAuthority());
        role.setUpdatedDate(LocalDateTime.now());

        // Save the updated role
        return roleRepository.save(role);
    }
    @Override
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Role getRoleById(Integer id) {
        return roleRepository.findById(id).orElseThrow(() -> new ServiceException("Role not found"));
    }

    @Override
    public Role updateRoleById(Integer id, Role role) {
        if(roleRepository.existsById(id)) {
            role.setId(id);
            return roleRepository.save(role);
        }
        return null;
    }
    @Override
    public List<Role> getRoleByAuthority(Role.AUTHORITY authority) {
        return roleRepository.findByAuthority(authority);
    }

    @Override
    public List<RolePermission> getRolePermissions(Integer roleId) {
        // Verify role exists
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ServiceException("Role not found with id: " + roleId));

        // Get all role permissions
        return rolePermissionRepository.findByRoleId(roleId);
    }

    private PermissionDTO convertToPermissionDTO(Permission permission) {
        return new PermissionDTO(permission.getId(), permission.getName());
    }

    @Override
    @Transactional
    public Role createRole(CreateRoleRequest request) {
        // Validate role name uniqueness
        if (roleRepository.existsByRoleName(request.getRoleName())) {
            throw new ServiceException("Role name already exists");
        }

        // Create and save new role
        Role newRole = new Role();
        newRole.setRoleName(request.getRoleName());
        newRole.setAuthority(request.getAuthority());
        Role savedRole = roleRepository.save(newRole);

        // Handle permissions
        List<Permission> permissions = permissionRepository.findAllById(request.getPermissions());
        if (permissions.size() != request.getPermissions().size()) {
            throw new IllegalArgumentException("One or more permissions are invalid");
        }

        // Create and save role-permission relationships
        List<RolePermission> rolePermissions = permissions.stream()
                .map(permission -> {
                    RolePermission rolePermission = new RolePermission();

                    // Create composite ID
                    RolePermission.RolePermissionPK pk = new RolePermission.RolePermissionPK();
                    pk.setRoleId(savedRole.getId());
                    pk.setPermissionId(permission.getId());

                    rolePermission.setId(pk);
                    rolePermission.setRole(savedRole);
                    rolePermission.setPermission(permission);

                    return rolePermission;
                })
                .collect(Collectors.toList());

        rolePermissionRepository.saveAll(rolePermissions);

        return savedRole;
    }

    @Override
    public List<Role> getAllRole() {
        return roleRepository.findAll();
    }

//    @Override
//    public Role deleteRole(Integer id) {
//        Role role = getRoleById(id);
//
//        return null;
//    }


}
