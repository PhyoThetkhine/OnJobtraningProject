package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {

}
