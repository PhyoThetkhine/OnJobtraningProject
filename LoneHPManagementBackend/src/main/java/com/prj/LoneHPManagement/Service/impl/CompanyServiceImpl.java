package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.Service.CompanyService;
import com.prj.LoneHPManagement.model.dto.CompanyDTO;
import com.prj.LoneHPManagement.model.entity.*;
import com.prj.LoneHPManagement.model.repo.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private  CompanyRepository companyRepository;

    @Autowired
    private AddressRepository addressRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CIFRepository cifRepository;
    @Autowired
    private BusinessPhotoRepository businessPhotoRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }
    @Override
    @Transactional
    public Company save(CompanyDTO companyDTO) {
        System.out.println("Starting company registration...");

        User createdUser = userRepository.findById(companyDTO.getCreatedUserId())
                .orElseThrow(() -> new RuntimeException("User with ID " + companyDTO.getCreatedUserId() + " not found in database"));

        System.out.println("User found: " + createdUser.getName());

        CIF cif = cifRepository.findById(companyDTO.getCifId())
                .orElseThrow(() -> new RuntimeException("CIF with ID " + companyDTO.getCifId() + " not found in database"));

        Address address = new Address();
        address.setCity(companyDTO.getCity());
        address.setState(companyDTO.getState());
        address.setTownship(companyDTO.getTownship());
        address.setAdditionalAddress(companyDTO.getAddress());

        Address savedAddress = addressRepository.save(address);

        Company company = new Company();
        company.setId(companyDTO.getId());
        company.setName(companyDTO.getName());
        company.setCompanyType(companyDTO.getCompanyType());
        company.setCategory(companyDTO.getCategory());
        company.setBusinessType(companyDTO.getBusinessType());
        company.setRegistrationDate(companyDTO.getRegistrationDate());
        company.setLicenseNumber(companyDTO.getLicenseNumber());
        company.setLicenseIssueDate(companyDTO.getLicenseIssueDate());
        company.setLicenseExpiryDate(companyDTO.getLicenseExpiryDate());
        company.setPhoneNumber(companyDTO.getPhoneNumber());

        company.setAddress(savedAddress);
        company.setCreatedUser(createdUser);
        company.setCif(cif);
        company.setCreatedDate(companyDTO.getCreatedDate());
        company.setUpdatedDate(companyDTO.getUpdatedDate());

        System.out.println("Saving company to database...");
        Company savedCompany = companyRepository.save(company);
        System.out.println("Company saved successfully with ID: " + savedCompany.getId());

        return savedCompany;
    }

    @Override
    public Page<Company> getCompaniesByCifId(int cifId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return companyRepository.findByCifId(cifId, pageable);
    }
    @Override
    public List<BusinessPhoto> getPhotosByCompanyId(int companyId) {
        return businessPhotoRepository.findByCompanyIdOrderByIdAsc(companyId);
    }
    @Override
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    // ✅ Implemented: Get Company by ID
    @Override
    public Company getCompanyById(int id) {
        Optional<Company> company = companyRepository.findById(id);
        return company.orElseThrow(() -> new RuntimeException("Company not found with ID: " + id));
    }

    @Override
    public void deleteCompany(int id) {
        companyRepository.deleteById(id);
    }

    @Override
    public Company updateCompany(int id, CompanyDTO companyDTO) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        company.setName(companyDTO.getName());
        company.setCompanyType(companyDTO.getCompanyType());
        company.setCategory(companyDTO.getCategory());
        company.setBusinessType(companyDTO.getBusinessType());
        company.setRegistrationDate(companyDTO.getRegistrationDate());
        company.setLicenseNumber(companyDTO.getLicenseNumber());
        company.setLicenseIssueDate(companyDTO.getLicenseIssueDate());
        company.setLicenseExpiryDate(companyDTO.getLicenseExpiryDate());
        company.setPhoneNumber(companyDTO.getPhoneNumber());
        company.setUpdatedDate(companyDTO.getUpdatedDate());

        return companyRepository.save(company);
    }
}



