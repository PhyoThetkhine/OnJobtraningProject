package com.prj.LoneHPManagement.model.repo;


import com.prj.LoneHPManagement.model.entity.Branch;
import com.prj.LoneHPManagement.model.entity.Role;
import com.prj.LoneHPManagement.model.entity.Branch;
import com.prj.LoneHPManagement.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Page<User> findByRoleId(Integer roleId, Pageable pageable);
    User findByUserCode(String userCode);
    Page<User> findByBranch_Id(int branchId, Pageable pageable);
    @Query("SELECT DISTINCT u.phoneNumber FROM User u")
    List<String> findAllUniquePhoneNumbers();
    List<User> findByRole_Id(int roleId);
    @Query("SELECT DISTINCT u.email FROM User u")
    List<String> findAllUniqueEmails();
    // Add this custom query
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.branch WHERE u.userCode = ?1")
    User findByUserCodeWithBranch(String userCode);
    public User findByPhoneNumber(String phoneNumber);

    public User findByEmail(String email);

    List<User> findByStatus(int status);
    @Query("SELECT MAX(u.userCode) FROM User u WHERE u.branch = :branch")
    String findMaxUserCodeByBranch(@Param("branch") Branch branch);


    @Query("SELECT u FROM User u WHERE u.userCode LIKE :branchCode%")
    Page<User> findByBranchCode(@Param("branchCode") String branchCode, Pageable pageable);
}
