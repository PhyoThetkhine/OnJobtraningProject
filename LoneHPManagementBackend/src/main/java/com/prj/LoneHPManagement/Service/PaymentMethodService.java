package com.prj.LoneHPManagement.Service;

import com.prj.LoneHPManagement.model.entity.PaymentMethod;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface PaymentMethodService {

    PaymentMethod createPaymentMethod(PaymentMethod paymentMethod, int userId);

    Page<PaymentMethod> getAllPaymentMethods(int page, int size, String sortBy);

    PaymentMethod getPaymentMethodById(int id);

    PaymentMethod updatePaymentMethod(int id, PaymentMethod paymentMethodDetails);
    void deletePaymentMethod(int id);

    List<PaymentMethod> getPaymentMethodsByUserId(int userId);
}
