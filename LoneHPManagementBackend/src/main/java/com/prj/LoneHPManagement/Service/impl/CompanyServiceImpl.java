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

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
//    @Override
//    @Transactional
//    private Company createCompany(BusinessRegistrationDTO businessDTO, CIF cif, User createdUser) {
//        // Create and save business address
//        Address businessAddress = createAddress(businessDTO.getAddress());
//        Address savedBusinessAddress = addressRepository.save(businessAddress);
//
//        // Create company
//        Company company = new Company();
//        company.setName(businessDTO.getName());
//        company.setCompanyType(businessDTO.getCompanyType());
//        company.setBusinessType(businessDTO.getBusinessType());
//        company.setCategory(businessDTO.getCategory());
//        company.setRegistrationDate(
//                businessDTO.getRegistrationDate().toInstant()
//                        .atZone(ZoneId.systemDefault())
//                        .toLocalDateTime()
//        );
//        company.setLicenseNumber(businessDTO.getLicenseNumber());
//        company.setLicenseIssueDate(
//                businessDTO.getLicenseIssueDate().toInstant()
//                        .atZone(ZoneId.systemDefault())
//                        .toLocalDateTime()
//        );
//        company.setLicenseExpiryDate(
//                businessDTO.getLicenseExpiryDate().toInstant()
//                        .atZone(ZoneId.systemDefault())
//                        .toLocalDateTime()
//        );
//        company.setPhoneNumber(businessDTO.getPhoneNumber());
//        company.setAddress(savedBusinessAddress);
//        company.setCreatedUser(createdUser);
//        company.setCif(cif);
//        company.setBusinessPhotos(businessDTO.getBusinessPhotos());
//
//        Company savedCompany = companyRepository.save(company);
//
//        // Create financial record
//        Financial financial = new Financial();
//        financial.setAverageIncome(BigDecimal.valueOf(businessDTO.getFinancial().getAverageIncome()));
//        financial.setExpectedIncome(BigDecimal.valueOf(businessDTO.getFinancial().getExpectedIncome()));
//        financial.setAverageExpenses(BigDecimal.valueOf(businessDTO.getFinancial().getAverageExpenses()));
//        financial.setAverageInvestment(BigDecimal.valueOf(businessDTO.getFinancial().getAverageInvestment()));
//        financial.setAverageEmployees(businessDTO.getFinancial().getAverageEmployees());
//        financial.setAverageSalaryPaid(BigDecimal.valueOf(financial.getAverageSalaryPaid()));
//        financial.setRevenueProof(financial.getRevenueProof());
//        financial.setCompany(savedCompany);
//        financial.setCreatedUser(createdUser);
//        financial.setCreatedDate(LocalDateTime.now());
//
//        financialRepository.save(financial);
//
//        return savedCompany;
//    }

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

        // Update basic fields
        company.setName(companyDTO.getName());
        company.setCompanyType(companyDTO.getCompanyType());
        company.setCategory(companyDTO.getCategory());
        company.setBusinessType(companyDTO.getBusinessType());
        company.setRegistrationDate(companyDTO.getRegistrationDate());
        company.setLicenseNumber(companyDTO.getLicenseNumber());
        company.setLicenseIssueDate(companyDTO.getLicenseIssueDate());
        company.setLicenseExpiryDate(companyDTO.getLicenseExpiryDate());
        company.setPhoneNumber(companyDTO.getPhoneNumber());
        company.setUpdatedDate(LocalDateTime.now());

        // Update address
        if (companyDTO.getAddress() != null) {
            company.getAddress().setState(companyDTO.getAddress().getState());
            company.getAddress().setTownship(companyDTO.getAddress().getTownship());
            company.getAddress().setCity(companyDTO.getAddress().getCity());
            company.getAddress().setAdditionalAddress(companyDTO.getAddress().getAdditionalAddress());
        }

        return companyRepository.save(company);
    }
}



