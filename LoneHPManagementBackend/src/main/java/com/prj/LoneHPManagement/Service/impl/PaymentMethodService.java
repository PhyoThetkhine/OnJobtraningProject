package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.model.repo.PaymentMethodRepository;
import com.prj.LoneHPManagement.model.entity.PaymentMethod;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentMethodService {
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Transactional
    public PaymentMethod savePaymentMethod(PaymentMethod paymentMethod) {
        return paymentMethodRepository.save(paymentMethod);
    }

    public List<PaymentMethod> getAllPaymentMethods() {
        return paymentMethodRepository.findAll();
    }

    public Optional<PaymentMethod> getPaymentMethodById(int id) {
        return paymentMethodRepository.findById(id);
    }

    public void deletePaymentMethod(int id) {
        paymentMethodRepository.deleteById(id);
    }
}
