package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.Service.DealerProductService;
import com.prj.LoneHPManagement.model.entity.DealerProduct;
import com.prj.LoneHPManagement.model.entity.Company;
import com.prj.LoneHPManagement.model.entity.SubCategory;
import com.prj.LoneHPManagement.model.entity.User;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import com.prj.LoneHPManagement.model.repo.DealerProductRepository;
import com.prj.LoneHPManagement.model.repo.CompanyRepository;
import com.prj.LoneHPManagement.model.repo.SubCategoryRepository;
import com.prj.LoneHPManagement.model.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DealerProductServiceImpl implements DealerProductService {

    @Autowired
    private DealerProductRepository dealerProductRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<DealerProduct> getAllDealerProducts() {
        return dealerProductRepository.findAll();
    }

    @Override
    public Optional<DealerProduct> getDealerProductById(int id) {
        return dealerProductRepository.findById(id);
    }

    @Override
    public List<DealerProduct> getDealerProductsByCompanyId(int companyId) {
        return dealerProductRepository.findByCompanyId(companyId);
    }
    // DealerProductService.java
    @Override
    public Page<DealerProduct> getAllDealerProducts(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return dealerProductRepository.findAll(pageable);
    }
    @Override
    public List<DealerProduct> getProductsForSelect(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return dealerProductRepository.findAll();
        }
        return dealerProductRepository.findByNameContainingIgnoreCase(searchTerm);
    }

    @Override
    public DealerProduct createDealerProduct(int companyId, DealerProduct dealerProduct) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ServiceException("Company not found with ID: " + companyId));

        // Assuming you may want to fetch and associate SubCategory and User entities as well.
        SubCategory subCategory = subCategoryRepository.findById(dealerProduct.getSubCategory().getId())
                .orElseThrow(() -> new ServiceException("SubCategory not found"));

        User user = userRepository.findById(dealerProduct.getCreatedUser().getId())
                .orElseThrow(() -> new ServiceException("User not found"));

        // Set the associated entities on the DealerProduct
        dealerProduct.setCompany(company);
        dealerProduct.setSubCategory(subCategory);
        dealerProduct.setCreatedUser(user);

        return dealerProductRepository.save(dealerProduct);
    }

    @Override
    public DealerProduct updateDealerProduct(int id, DealerProduct dealerProductDetails) {
        DealerProduct dealerProduct = dealerProductRepository.findById(id)
                .orElseThrow(() -> new ServiceException("DealerProduct not found with ID: " + id));

        // Update the fields of the DealerProduct
        dealerProduct.setName(dealerProductDetails.getName());
        dealerProduct.setPrice(dealerProductDetails.getPrice());
        dealerProduct.setDescription(dealerProductDetails.getDescription());

        // Assuming update of SubCategory and User as well if needed
        SubCategory subCategory = subCategoryRepository.findById(dealerProductDetails.getSubCategory().getId())
                .orElseThrow(() -> new ServiceException("SubCategory not found"));
        User user = userRepository.findById(dealerProductDetails.getCreatedUser().getId())
                .orElseThrow(() -> new ServiceException("User not found"));

        dealerProduct.setSubCategory(subCategory);
        dealerProduct.setCreatedUser(user);

        return dealerProductRepository.save(dealerProduct);
    }

    @Override
    public void deleteDealerProduct(int id) {
        DealerProduct dealerProduct = dealerProductRepository.findById(id)
                .orElseThrow(() -> new ServiceException("DealerProduct not found with ID: " + id));
        dealerProductRepository.delete(dealerProduct);
    }
}
