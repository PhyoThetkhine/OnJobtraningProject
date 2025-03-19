package com.prj.LoneHPManagement.model.repo;

import com.prj.LoneHPManagement.model.entity.PaymentMethod;
import com.prj.LoneHPManagement.model.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {

        List<PaymentMethod> findByCreatedUser(User createdUser);

}


