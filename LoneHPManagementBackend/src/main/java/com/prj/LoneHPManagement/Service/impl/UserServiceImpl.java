package com.prj.LoneHPManagement.Service.impl;


import com.prj.LoneHPManagement.Service.CodeGenerateService;
import com.prj.LoneHPManagement.Service.UserService;
import com.prj.LoneHPManagement.model.dto.PagedResponse;
import com.prj.LoneHPManagement.model.dto.UserUpdateDTO;
import com.prj.LoneHPManagement.model.entity.*;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import com.prj.LoneHPManagement.model.exception.UserNotFoundException;
import com.prj.LoneHPManagement.model.repo.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private CodeGenerateService codeGenerateService;
    @Autowired
    private UserCurrentAccountRepository userCurrentAccountRepository;
    @Autowired
    private RolePermissionRepository rolePermissionRepository;
    @Autowired
    private UserPermissionRepository userPermissionRepository;

    @Override
    public User getUserById(int id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new UserNotFoundException("User not found for ID: " + id);
        }
    }
    @Override
    public User updateUser(int id, UserUpdateDTO userUpdates) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ServiceException("User not found with id " + id));

        // Update only the necessary fields
        existingUser.setName(userUpdates.getName());
        existingUser.setEmail(userUpdates.getEmail());
        existingUser.setPhoneNumber(userUpdates.getPhoneNumber());
        existingUser.setNRC(userUpdates.getNrc());
        existingUser.setNRCFrontPhoto(userUpdates.getNrcFrontPhoto());
        existingUser.setNRCBackPhoto(userUpdates.getNrcBackPhoto());
        existingUser.setDateOfBirth(userUpdates.getDateOfBirth());
        existingUser.setGender(userUpdates.getGender());
        existingUser.setPhoto(userUpdates.getPhoto());

        if (userUpdates.getAddress() != null) {
            Address address = existingUser.getAddress();
            if (address == null) {
                address = new Address();
            }
            address.setState(userUpdates.getAddress().getState());
            address.setTownship(userUpdates.getAddress().getTownship());
            address.setCity(userUpdates.getAddress().getCity());
            address.setAdditionalAddress(userUpdates.getAddress().getAdditionalAddress());
            existingUser.setAddress(address);
        }

        // The @PreUpdate in your entity will update the updatedDate field automatically.
        return userRepository.save(existingUser);
    }
    @Override
    @Transactional
    public User changeUserRole(int userId, int roleId) {
        // Retrieve the user by its ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User  not found with id: " + userId));

        // Retrieve the new role by its ID
        Role newRole = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        // Update the user's role
        user.setRole(newRole);
        User updatedUser  = userRepository.save(user);

        // Get the permissions associated with the new role
        List<RolePermission> newRolePermissions = rolePermissionRepository.findByRoleId(roleId);

        // Update UserPermissions
        // First, remove existing UserPermissions for the user
        // Remove existing UserPermissions for the user
        List<UserPermission> existingPermissions = userPermissionRepository.findByUserId(userId);
        if (!existingPermissions.isEmpty()) {
            userPermissionRepository.deleteByUserId(userId);
            System.out.println("Deleted UserPermissions for userId: " + userId);
        } else {
            System.out.println("No UserPermissions found for userId: " + userId);
        }
        // Retrieve the currently logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUserDetails = (UserDetails) authentication.getPrincipal();
        User allowedUser  = userRepository.findByUserCode(currentUserDetails.getUsername());
        // Then, create new UserPermissions based on the new role's permissions
        for (RolePermission rolePermission : newRolePermissions) {
            Permission permission = rolePermission.getPermission();

            UserPermission userPermission = new UserPermission();
            UserPermission.UserPermissionPK userPk = new UserPermission.UserPermissionPK();
            userPk.setUserId(updatedUser.getId());
            userPk.setPermissionId(permission.getId());
            userPermission.setId(userPk);
            userPermission.setUser (updatedUser );
            userPermission.setPermission(permission);
            userPermission.setIsAllowed(ConstraintEnum.ALLOWED.getCode());
            userPermission.setAllowedDate(LocalDateTime.now());
            userPermission.setAllowedUser (allowedUser); // Assuming allowedUser  is the same user
            userPermission.setLimitedDate(LocalDateTime.now().plusYears(1)); // Example

            userPermissionRepository.save(userPermission);
        }
        // Return the updated user
        return updatedUser ;
    }

    @Override
    public List<UserPermission> getPermissionByUserId(int userId) {
        return userPermissionRepository.findByUserId(userId);
    }

    @Override
    public PagedResponse<User> getRoleUsers(Integer roleId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findByRoleId(roleId, pageable);

        return new PagedResponse<>(
                userPage.getContent(),
                userPage.getTotalPages(),
                userPage.getTotalElements(),
                userPage.getSize(),
                userPage.getNumber(),
                userPage.getNumberOfElements(),
                userPage.isFirst(),
                userPage.isLast(),
                userPage.isEmpty()
        );
    }

    @Override
    public User getUserByUserCode(String userCode) {
        try {
            return userRepository.findByUserCode(userCode); // or getUserByUserCode()
        } catch (Exception e) {

            throw e; // Re-throw to propagate the error
        }
    }

    @Override
    public User save(User user) {
        User creator = userRepository.findById(user.getCreatedUser().getId())
                .orElseThrow(() -> new ServiceException("User not found with id: " ));
        Role userrole = roleRepository.findById(user.getRole().getId())
                .orElseThrow(() -> new ServiceException("Role not found with id: " ));
        Branch branch = branchRepository.findById(user.getBranch().getId())
                .orElseThrow(() -> new ServiceException("Branch not found with id: " ));
        Address address = new Address();
        address.setCity(user.getAddress().getCity());
        address.setState(user.getAddress().getState());
        address.setTownship(user.getAddress().getTownship());
        address.setAdditionalAddress(user.getAddress().getAdditionalAddress());
        Address savedAddress = addressRepository.save(address);
        user.setUserCode(codeGenerateService.generateUserCode(branch));
        user.setRole(userrole);
        user.setBranch(branch);
        user.setCreatedUser(creator);
        user.setPassword("$2a$10$tVX1cC0Yq5MC5eF5YNzVle/vMqTuhvUqtPkD/Pv04vw2c3cNGm0d6");
        user.setCreatedDate(LocalDateTime.now());
        user.setUpdatedDate(LocalDateTime.now());
        user.setAddress(savedAddress);
        User savedUser = userRepository.save(user);
        Role role = savedUser.getRole();
        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleId(role.getId());
        // For each role permission, create a user permission
        for (RolePermission rolePermission : rolePermissions) {
            Permission permission = rolePermission.getPermission();

            UserPermission userPermission = new UserPermission();
            UserPermission.UserPermissionPK pk = new UserPermission.UserPermissionPK();
            pk.setUserId(savedUser.getId());
            pk.setPermissionId(permission.getId());
            userPermission.setId(pk);
            userPermission.setUser(savedUser);
            userPermission.setPermission(permission);
            userPermission.setIsAllowed(ConstraintEnum.ALLOWED.getCode());
            userPermission.setAllowedDate(LocalDateTime.now());
            userPermission.setAllowedUser(savedUser.getCreatedUser()); // Assuming allowedUser is the same user
            userPermission.setLimitedDate(LocalDateTime.now().plusYears(1)); // Example

            userPermissionRepository.save(userPermission);
        }
        // Create UserCurrentAccount
        UserCurrentAccount userCurrentAccount = new UserCurrentAccount();
        userCurrentAccount.setUser(savedUser);
        userCurrentAccount.setAccCode(codeGenerateService.generateUserAccountCode(savedUser));
        userCurrentAccount.setBalance(BigDecimal.ZERO);
        userCurrentAccount.setIsFreeze(ConstraintEnum.NOT_FREEZE.getCode());
        userCurrentAccount.setCreatedDate(LocalDateTime.now());
        userCurrentAccount.setUpdatedDate(LocalDateTime.now());
        userCurrentAccountRepository.save(userCurrentAccount);
        return savedUser;
    }

    @Override
    public Page<User> getAllUser(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users;
    }
    @Override
    public User changeUserStatus(int userId, int statusCode) {
        // Retrieve user from repository
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Set the new status code
        user.setStatus(statusCode);
        user.setUpdatedDate(LocalDateTime.now());
        // Save changes
        userRepository.save(user);
        // Convert the updated entity to a DTO (using your conversion logic)
        return user;
    }
    public Page<User> getUsersByBranchId(int branchId, Pageable pageable) {
        return userRepository.findByBranch_Id(branchId, pageable);
    }


}
