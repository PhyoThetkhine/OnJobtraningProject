
package com.prj.LoneHPManagement.security;


import com.prj.LoneHPManagement.Service.PermissionService;
import com.prj.LoneHPManagement.Service.UserService;
import com.prj.LoneHPManagement.model.entity.Permission;
import com.prj.LoneHPManagement.model.entity.UserPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;
    private final PermissionService permissionService;

    @Autowired
    public CustomUserDetailsService(UserService userService, PermissionService permissionService) {
        this.userService = userService;
        this.permissionService = permissionService;
    }

    @Override
    public UserDetails loadUserByUsername(String userCode) throws UsernameNotFoundException {
        com.prj.LoneHPManagement.model.entity.User user = userService.getUserByUserCode(userCode);
        if (user == null) {
            log.error("User not found: {}", userCode);
            System.out.println("user not found");
            System.out.flush();
            throw new UsernameNotFoundException("User not found with code: " + userCode);
        }

        // Check if the user has a role assigned
//        if (user.getRole() == null) {
//            log.warn("User has no role assigned: {}", userCode);
//            System.out.println("role not assigned");
//            System.out.flush();
//            throw new UsernameNotFoundException("User has no role assigned: " + userCode);
//        } else {
//          //  System.out.println("role have assigned");
//           // System.out.flush();
//            //log.debug("User role found: {}", user.getRole().getRoleName()); // <-- Log debug info
//        }

        try {
            List<Permission> rolePermissions = permissionService.getPermissionsForRole(user.getRole().getId());
            log.debug("Role permissions for role ID {}: {}", user.getRole().getId(), rolePermissions);

            List<UserPermission> userPermissions = permissionService.getUserPermissionsIsAllowed(user.getId());
            log.debug("User permissions for user ID {}: {}", user.getId(), userPermissions);
            // Handle null cases
            rolePermissions = rolePermissions != null ? rolePermissions : new ArrayList<>();
            userPermissions = userPermissions != null ? userPermissions : new ArrayList<>();

            List<String> effectivePermissions = permissionService.mergePermissions(rolePermissions, userPermissions);

            // Log effective permissions for debugging
            log.debug("Effective permissions: {}", effectivePermissions);

            List<SimpleGrantedAuthority> authorities = userPermissions.stream()
                    .map(UserPermission::getPermission)
                    .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                    .collect(Collectors.toList());
            return new User(user.getUserCode(), user.getPassword(), authorities);

        } catch (Exception e) {
            log.error("Error loading permissions for user: {}", userCode, e);
            throw new UsernameNotFoundException("Error loading user permissions", e);
        }
    }
}
