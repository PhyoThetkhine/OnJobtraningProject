package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.Service.PaymentMethodService;
import com.prj.LoneHPManagement.model.entity.ConstraintEnum;
import com.prj.LoneHPManagement.model.entity.PaymentMethod;
import com.prj.LoneHPManagement.model.entity.User;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import com.prj.LoneHPManagement.model.repo.PaymentMethodRepository;
import com.prj.LoneHPManagement.model.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public PaymentMethod createPaymentMethod(PaymentMethod paymentMethod, int userId) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new RuntimeException("User not found");
        }

        paymentMethod.setCreatedUser(user.get());
        paymentMethod.setCreatedDate(LocalDateTime.now());
        paymentMethod.setUpdatedDate(LocalDateTime.now());
        paymentMethod.setStatus(ConstraintEnum.ACTIVE.getCode());
        return paymentMethodRepository.save(paymentMethod);
    }

    @Override
    public Page<PaymentMethod> getAllPaymentMethods(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return paymentMethodRepository.findAll(pageable);
    }

    @Override
    public PaymentMethod getPaymentMethodById(int id) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PaymentMethod not found with ID: " + id));
        if (paymentMethod == null) {
            throw new ServiceException("Payment method not found");
        }
        return paymentMethod;
    }


    @Override
    public PaymentMethod updatePaymentMethod(int id, PaymentMethod paymentMethodDetails) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PaymentMethod not found with ID: " + id));

        paymentMethod.setPaymentType(paymentMethodDetails.getPaymentType());
        // Update other fields if necessary
        return paymentMethodRepository.save(paymentMethod);
    }

    @Override
    public void deletePaymentMethod(int id) {
        if (!paymentMethodRepository.existsById(id)) {
            throw new RuntimeException("PaymentMethod not found with ID: " + id);
        }
        paymentMethodRepository.deleteById(id);
    }

    @Override
    public List<PaymentMethod> getPaymentMethodsByUserId(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<PaymentMethod> paymentMethods = paymentMethodRepository.findByCreatedUser(user);

        if (paymentMethods.isEmpty()) {
            throw new RuntimeException("No payment methods found for the user");
        }

        return paymentMethods;
    }
}
