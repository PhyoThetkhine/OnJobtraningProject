package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.Service.CompanyService;
import com.prj.LoneHPManagement.model.dto.CompanyCreateRequest;
import com.prj.LoneHPManagement.model.dto.CompanyDTO;
import com.prj.LoneHPManagement.model.dto.CompanyDTOUpdate;
import com.prj.LoneHPManagement.model.entity.*;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import com.prj.LoneHPManagement.model.repo.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private  CompanyRepository companyRepository;
    @Autowired
    private FinancialRepository financialRepository;
    @Autowired
    private AddressRepository addressRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CIFRepository cifRepository;
    @Autowired
    private BusinessPhotoRepository businessPhotoRepository;
    @Override
    public Company createCompany(CompanyDTO request) {
        // Fetch User and CIF
        User user = userRepository.findById(request.getCreatedUserId())
                .orElseThrow(() -> new ServiceException("User not found"));
        CIF cif = cifRepository.findById(request.getCifId())
                .orElseThrow(() -> new ServiceException("CIF not found"));

        // Create Address
        Address address = new Address();
        address.setState(request.getState());
        address.setCity(request.getCity());
        address.setTownship(request.getTownship());
        address.setAdditionalAddress(request.getAddress());
        address = addressRepository.save(address);

        // Create Company
        Company company = new Company();
        company.setName(request.getName());
        company.setCompanyType(request.getCompanyType());
        company.setBusinessType(request.getBusinessType());
        company.setCategory(request.getCategory());
        company.setRegistrationDate(request.getRegistrationDate());
        company.setLicenseIssueDate(Date.valueOf(request.getLicenseIssueDate()));
        company.setLicenseExpiryDate(Date.valueOf(request.getLicenseExpiryDate()));
        company.setLicenseNumber(request.getLicenseNumber());

        company.setPhoneNumber(request.getPhoneNumber());
        company.setAddress(address);
        company.setCreatedUser(user);
        company.setCif(cif);
        company.setCreatedDate(LocalDateTime.now());
        company.setUpdatedDate(LocalDateTime.now());

        company = companyRepository.save(company);

        // Create Financial Information
        Financial financial = new Financial();
        financial.setAverageIncome(String.valueOf(request.getFinancial().getAverageIncome()));
        financial.setAverageEmployees(String.valueOf(request.getFinancial().getAverageEmployees()));
        financial.setAverageSalaryPaid(String.valueOf(request.getFinancial().getAverageSalaryPaid()));
        financial.setRevenueProof(String.valueOf(request.getFinancial().getRevenueProof()));
        financial.setAverageExpenses(String.valueOf(request.getFinancial().getAverageExpenses()));
        financial.setExpectedIncome(String.valueOf(request.getFinancial().getExpectedIncome()));
        financial.setAverageInvestment(String.valueOf(request.getFinancial().getAverageInvestment()));
        financial.setCompany(company);
        financial.setCreatedUser(user);
        financial.setCreatedDate(LocalDateTime.now());
        financial.setUpdatedDate(LocalDateTime.now());

        financialRepository.save(financial);

        // Create Business Photos
        if (request.getBusinessPhotoUrls() != null) {
            for (String photoUrl : request.getBusinessPhotoUrls()) {
                BusinessPhoto businessPhoto = new BusinessPhoto();
                businessPhoto.setPhoto(photoUrl);
                businessPhoto.setCompany(company);
                businessPhotoRepository.save(businessPhoto);
            }
        }

        return company;
    }
    @Override
    public Company updateCompany(int id, CompanyDTOUpdate request) {
        // Fetch existing company
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new ServiceException("Company not found"));
//
//        CIF cif = cifRepository.findById(request.getCifId())
//                .orElseThrow(() -> new ServiceException("CIF not found"));

        // Update Address
        Address address = existingCompany.getAddress();
        address.setState(request.getState());
        address.setCity(request.getCity());
        address.setTownship(request.getTownship());
        address.setAdditionalAddress(request.getAddress());
        address = addressRepository.save(address);

        // Update Company
        existingCompany.setName(request.getName());
        existingCompany.setCompanyType(request.getCompanyType());
        existingCompany.setBusinessType(request.getBusinessType());
        existingCompany.setCategory(request.getCategory());
        existingCompany.setRegistrationDate(request.getRegistrationDate());
        existingCompany.setLicenseIssueDate(Date.valueOf(request.getLicenseIssueDate()));
        existingCompany.setLicenseExpiryDate(Date.valueOf(request.getLicenseExpiryDate()));
        existingCompany.setLicenseNumber(request.getLicenseNumber());
        existingCompany.setPhoneNumber(request.getPhoneNumber());
        existingCompany.setAddress(address);
        existingCompany.setCreatedUser(existingCompany.getCreatedUser());
        existingCompany.setCif(existingCompany.getCif());
        existingCompany.setUpdatedDate(LocalDateTime.now());

        existingCompany = companyRepository.save(existingCompany);

               return existingCompany;
    }
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


//    public Company updateCompany(int id, CompanyDTO companyDTO) {
//        Company company = companyRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Company not found"));
//
//        // Update basic fields
//        company.setName(companyDTO.getName());
//        company.setCompanyType(companyDTO.getCompanyType());
//        company.setCategory(companyDTO.getCategory());
//        company.setBusinessType(companyDTO.getBusinessType());
//        company.setRegistrationDate(companyDTO.getRegistrationDate());
//        company.setLicenseNumber(companyDTO.getLicenseNumber());
//        company.setLicenseIssueDate(companyDTO.getLicenseIssueDate());
//        company.setLicenseExpiryDate(companyDTO.getLicenseExpiryDate());
//        company.setPhoneNumber(companyDTO.getPhoneNumber());
//        company.setUpdatedDate(LocalDateTime.now());
//
//        // Update address
//        if (companyDTO.getAddress() != null) {
//            company.getAddress().setState(companyDTO.getAddress().getState());
//            company.getAddress().setTownship(companyDTO.getAddress().getTownship());
//            company.getAddress().setCity(companyDTO.getAddress().getCity());
//            company.getAddress().setAdditionalAddress(companyDTO.getAddress().getAdditionalAddress());
//        }
//
//        return companyRepository.save(company);
//    }
}



