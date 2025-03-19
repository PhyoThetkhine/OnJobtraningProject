package com.prj.LoneHPManagement.Service;

import com.prj.LoneHPManagement.model.dto.CreateRoleRequest;
import com.prj.LoneHPManagement.model.dto.PermissionDTO;
import com.prj.LoneHPManagement.model.dto.UpdateRoleRequest;
import com.prj.LoneHPManagement.model.entity.Permission;
import com.prj.LoneHPManagement.model.entity.Role;
import com.prj.LoneHPManagement.model.entity.RolePermission;

import java.util.List;

public interface RoleService {
    List<Role> getRoleByAuthority(Role.AUTHORITY authority);
    List<RolePermission> getRolePermissions(Integer roleId);
    Role createRole(CreateRoleRequest request);
    Role save(Role role);
    Role updateRole(Integer roleId, UpdateRoleRequest updateRoleRequest);
    Role getRoleById(Integer id);

    Role updateRoleById(Integer id , Role role);

    List<Role> getAllRole();

    //Role deleteRole(Integer id);


}
