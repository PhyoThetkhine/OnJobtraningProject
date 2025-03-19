package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, UserPermission.UserPermissionPK> {
    @Query("SELECT up FROM UserPermission up WHERE up.user.id = :userId AND up.isAllowed = :isAllowedCode")
    List<UserPermission> findByUserIdAndIsAllowed(@Param("userId") Integer userId, @Param("isAllowedCode") int isAllowedCode);
    @Query("SELECT up FROM UserPermission up WHERE up.user.id = :userId") // Changed to select UserPermission
    List<UserPermission> findByUserId(@Param("userId") Integer userId);

  void deleteByUserId(Integer userId);
}