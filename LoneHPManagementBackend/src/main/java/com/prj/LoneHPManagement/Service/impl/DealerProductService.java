package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.model.exception.ServiceException;
import com.prj.LoneHPManagement.model.repo.DealerProductRepository;
import com.prj.LoneHPManagement.model.entity.DealerProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DealerProductService {
    @Autowired
    private DealerProductRepository dealerProductRepository;

    public List<DealerProduct> getAllDealerProducts() {
        return dealerProductRepository.findAll();
    }

    public Optional<DealerProduct> getDealerProductById(int id) {
        return dealerProductRepository.findById(id);
    }

    public DealerProduct createDealerProduct(DealerProduct dealerProduct) {
        return dealerProductRepository.save(dealerProduct);
    }

    public DealerProduct updateDealerProduct(int id, DealerProduct dealerProductDetails) {
        DealerProduct dealerProduct = dealerProductRepository.findById(id)
                .orElseThrow(() -> new ServiceException("DealerProduct not found with id: " + id));

        dealerProduct.setName(dealerProductDetails.getName());
        dealerProduct.setPrice(dealerProductDetails.getPrice());
        dealerProduct.setDescription(dealerProductDetails.getDescription());
        dealerProduct.setSubCategory(dealerProductDetails.getSubCategory());
        dealerProduct.setCompany(dealerProductDetails.getCompany());
        dealerProduct.setCreatedUser(dealerProductDetails.getCreatedUser());

        return dealerProductRepository.save(dealerProduct);
    }
    public List<DealerProduct> getProductsForSelect(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return dealerProductRepository.findAll();
        }
        return dealerProductRepository.findByNameContainingIgnoreCase(searchTerm);
    }

    public void deleteDealerProduct(int id) {
        DealerProduct dealerProduct = dealerProductRepository.findById(id)
                .orElseThrow(() -> new ServiceException("DealerProduct not found with id: " + id));
        dealerProductRepository.delete(dealerProduct);
    }
}
