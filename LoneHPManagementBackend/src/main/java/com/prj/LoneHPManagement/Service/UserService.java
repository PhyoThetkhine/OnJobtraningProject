package com.prj.LoneHPManagement.Service;

import com.prj.LoneHPManagement.model.dto.PagedResponse;
import com.prj.LoneHPManagement.model.dto.UserUpdateDTO;
import com.prj.LoneHPManagement.model.entity.Permission;
import com.prj.LoneHPManagement.model.entity.User;
import com.prj.LoneHPManagement.model.entity.UserPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    User getUserById(int id);
    boolean changePassword(int userId, String currentPassword, String newPassword);
    User save(User user);
    User changeUserStatus(int userId, int statusCode);
    Page<User> getAllUser(Pageable pageable);
    User getUserByUserCode(String userCode);
    User changeUserRole(int userId, int roleId);
    List<UserPermission> getPermissionByUserId(int userId);
    PagedResponse<User> getRoleUsers(Integer roleId, int page, int size);
    User updateUser(int id, UserUpdateDTO userUpdates);
}
