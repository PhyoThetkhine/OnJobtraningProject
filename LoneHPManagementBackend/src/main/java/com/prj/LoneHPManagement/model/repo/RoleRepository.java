package com.prj.LoneHPManagement.model.repo;


import com.prj.LoneHPManagement.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role,Integer> {
    List<Role> findByAuthority(Role.AUTHORITY authority);
    @Query("SELECT COUNT(r) > 0 FROM Role r WHERE LOWER(r.roleName) = LOWER(:roleName)")
    boolean existsByRoleName(@Param("roleName") String roleName);
}
