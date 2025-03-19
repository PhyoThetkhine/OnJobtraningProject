package com.prj.LoneHPManagement.Controller;

import com.prj.LoneHPManagement.Service.RolePermissionService;
import com.prj.LoneHPManagement.Service.RoleService;
import com.prj.LoneHPManagement.Service.UserService;
import com.prj.LoneHPManagement.model.dto.*;
import com.prj.LoneHPManagement.model.entity.Permission;
import com.prj.LoneHPManagement.model.entity.Role;
import com.prj.LoneHPManagement.model.entity.RolePermission;
import com.prj.LoneHPManagement.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private UserService userService;
    @Autowired
    private RolePermissionService rolePermissionService;
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }
    @PutMapping("/update/{roleId}")
    public ResponseEntity<ApiResponse<Role>> updateRole(
            @PathVariable Integer roleId,
            @RequestBody UpdateRoleRequest updateRoleRequest) {

        Role updatedRole = roleService.updateRole(roleId, updateRoleRequest);
        return ResponseEntity.ok(ApiResponse.success(200, "Role updated successfully", updatedRole));
    }
    @GetMapping("/{roleId}/permissions")
    public ResponseEntity<ApiResponse<List<RolePermission>>> getRolePermissions(
            @PathVariable Integer roleId) {

        List<RolePermission> rolePermissions = roleService.getRolePermissions(roleId);
        return ResponseEntity.ok(ApiResponse.success(200, "Role permissions retrieved", rolePermissions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> getRoleById(@PathVariable Integer id) {
        Role role = roleService.getRoleById(id);
        return ResponseEntity.ok(ApiResponse.success(200, "Role retrieved successfully", role));
    }
    @GetMapping("/{roleId}/users")
    public ResponseEntity<ApiResponse<PagedResponse<User>>> getRoleUsers(
            @PathVariable Integer roleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PagedResponse<User> users = userService.getRoleUsers(roleId, page, size);
        return ResponseEntity.ok(ApiResponse.success(200, "Users retrieved successfully", users));
    }

    @GetMapping("/allRoles")
    public ResponseEntity<List<Role>>  getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRole());
    }

    @GetMapping("/regularBranchLevelRole")
    public ResponseEntity<List<Role>> getRegularBranchLevelRole() {
        return ResponseEntity.ok(roleService.getRoleByAuthority(Role.AUTHORITY.RegularBranchLevel));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Role>> createRole(@RequestBody CreateRoleRequest request) {
        Role createdRole = roleService.createRole(request);
        return ResponseEntity.ok(ApiResponse.success(201, "Role created successfully", createdRole));
    }

//    @PutMapping("/update/{id}")
//    public ResponseEntity<Role> updateRole(@PathVariable Integer id , @RequestBody Role role) {
//        Role updatedRole = roleService.updateRoleById(id , role);
//
//        if(updatedRole != null) {
//            return ResponseEntity.ok(updatedRole);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//
//    }
@PostMapping("/{roleId}/permissions/{permissionId}")
public ResponseEntity<ApiResponse<Void>> addRolePermission(
        @PathVariable Integer roleId,
        @PathVariable Integer permissionId) {
    rolePermissionService.addRolePermission(roleId, permissionId);
    return ResponseEntity.ok(ApiResponse.success(200, "Permission added successfully", null));
}

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<ApiResponse<Void>> removeRolePermission(
            @PathVariable Integer roleId,
            @PathVariable Integer permissionId) {
        rolePermissionService.removeRolePermission(roleId, permissionId);
        return ResponseEntity.ok(ApiResponse.success(200, "Permission removed successfully", null));
    }
    @GetMapping("/mainBranchLevelRole")
    public ResponseEntity<List<Role>> getMainBranchLevelRole() {
        return ResponseEntity.ok(roleService.getRoleByAuthority(Role.AUTHORITY.MainBranchLevel));
    }







}
