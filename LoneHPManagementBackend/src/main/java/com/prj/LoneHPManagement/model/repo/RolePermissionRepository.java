package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.Permission;
import com.prj.LoneHPManagement.model.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermission.RolePermissionPK> {
    @Query("SELECT rp.permission FROM RolePermission rp WHERE rp.role.id = :roleId")
    List<Permission> findPermissionByRoleId(@Param("roleId") Integer roleId);
    List<RolePermission> findByRoleId(Integer roleId);
}
