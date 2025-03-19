package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.User;
import com.prj.LoneHPManagement.model.entity.UserCurrentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserCurrentAccountRepository extends JpaRepository<UserCurrentAccount, Integer> {

    List<UserCurrentAccount> findByIsFreeze(int isFreeze);

    List<UserCurrentAccount> findByAccCode(String accCode);

    @Query("SELECT MAX(u.accCode) FROM UserCurrentAccount u WHERE u.user = :user")
    String findMaxAccountCodeByUser(@Param("user") User user);

    List<UserCurrentAccount> findByUserId(Integer userId);

    @Query("SELECT uca FROM UserCurrentAccount uca WHERE uca.user.id = :userId")
    Optional<UserCurrentAccount> findByUserId(@Param("userId") int userId);





}
