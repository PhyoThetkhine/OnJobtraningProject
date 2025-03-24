package com.prj.LoneHPManagement.Service.impl;


import com.prj.LoneHPManagement.Service.CodeGenerateService;
import com.prj.LoneHPManagement.model.dto.AddressDTO;
import com.prj.LoneHPManagement.model.dto.ClientRegistrationDTO;
import com.prj.LoneHPManagement.model.dto.UpdatedCifDTO;
import com.prj.LoneHPManagement.model.entity.*;
import com.prj.LoneHPManagement.model.exception.*;
import com.prj.LoneHPManagement.model.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CIFService {


    @Autowired
    private CIFRepository cifRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private FinancialRepository financialRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CodeGenerateService codeGenerateService;
    @Autowired
    private CIFCurrentAccountRepository cifCurrentAccountRepository;
    @Autowired
    private BusinessPhotoRepository businessPhotoRepository;



    public CIF registerClient(ClientRegistrationDTO dto) {
        // Get the user by ID
        User createdUser = userRepository.findById(dto.getCreatedUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Create and save address
        Address clientAddress = createAddress(dto.getAddress());
        Address savedClientAddress = addressRepository.save(clientAddress);

        // 2. Create and save CIF
        CIF cif = new CIF();
       cif.setCifCode(codeGenerateService.generateCIFCode(createdUser));
      // cif.setCifCode("000100030090");
        cif.setCifType(dto.getCifType());
        cif.setStatus(13); // Active status
        // Set base entity fields
        cif.setName(dto.getName());
        cif.setEmail(dto.getEmail());
        cif.setPhoneNumber(dto.getPhoneNumber());
        cif.setNRC(dto.getNRC());
        cif.setNRCFrontPhoto(dto.getNrcFrontPhoto());
        cif.setNRCBackPhoto(dto.getNrcBackPhoto());
        cif.setDateOfBirth(new java.sql.Date(dto.getDateOfBirth().getTime()));
        cif.setGender(dto.getGender());
        cif.setAddress(savedClientAddress);
        cif.setCreatedUser(createdUser);  // Use the provided user
        cif.setPhoto(dto.getPhoto());
        cif.setCreatedDate(LocalDateTime.now());
        cif.setUpdatedDate(LocalDateTime.now());
        System.out.println("CIFCODe"+cif.getCifCode());

        CIF savedCIF = cifRepository.save(cif);

        // 3. Create and save account
        CIFCurrentAccount account = new CIFCurrentAccount();
        account.setAccCode(codeGenerateService.generateCIFAccountCode(savedCIF));
        account.setMinAmount(BigDecimal.valueOf(dto.getAccount().getMinAmount()));
        account.setMaxAmount(BigDecimal.valueOf(dto.getAccount().getMaxAmount()));
        account.setBalance(BigDecimal.ZERO);
        account.setIsFreeze(ConstraintEnum.NOT_FREEZE.getCode()); // Not frozen
        account.setHoldAmount(BigDecimal.ZERO);
        account.setCif(savedCIF);
        cifCurrentAccountRepository.save(account);

        // 4. If business type, create company and business photos
        if (dto.getBusiness() != null &&
                (dto.getCifType() == CIF.CIFType.DEVELOPED_COMPANY ||
                        dto.getCifType() == CIF.CIFType.SETUP_COMPANY)) {

            // Create and save business address
            Address businessAddress = createAddress(dto.getBusiness().getAddress());
            Address savedBusinessAddress = addressRepository.save(businessAddress);

            // Create and save company
            Company company = new Company();
            company.setName(dto.getBusiness().getName());
            company.setCompanyType(dto.getBusiness().getCompanyType());
            company.setCategory(dto.getBusiness().getCategory());
            company.setBusinessType(dto.getBusiness().getBusinessType());
            company.setRegistrationDate(
                    LocalDateTime.ofInstant(
                            dto.getBusiness().getRegistrationDate().toInstant(),
                            ZoneId.systemDefault()
                    )
            );
            company.setLicenseNumber(dto.getBusiness().getLicenseNumber());
            company.setLicenseIssueDate(
                    new java.sql.Date(dto.getBusiness().getLicenseIssueDate().getTime())
            );
            company.setLicenseExpiryDate(
                    new java.sql.Date(dto.getBusiness().getLicenseExpiryDate().getTime())
            );
            company.setPhoneNumber(dto.getBusiness().getPhoneNumber());
            company.setAddress(savedBusinessAddress);
            company.setCreatedUser(createdUser);  // Use the provided user
            company.setCif(savedCIF);

            Company savedCompany = companyRepository.save(company);

            // Save business photos
            if (dto.getBusiness().getBusinessPhotos() != null) {
                dto.getBusiness().getBusinessPhotos().forEach(photoUrl -> {
                    BusinessPhoto photo = new BusinessPhoto();
                    photo.setPhoto(photoUrl);
                    photo.setCompany(savedCompany);
                    businessPhotoRepository.save(photo);
                });
            }
            Financial financial = new Financial();
            financial.setAverageIncome(String.valueOf(dto.getBusiness().getFinancial().getAverageIncome()));
            financial.setAverageEmployees(String.valueOf(dto.getBusiness().getFinancial().getAverageEmployees()));
            financial.setAverageSalaryPaid(String.valueOf(dto.getBusiness().getFinancial().getAverageSalaryPaid()));
            financial.setRevenueProof(String.valueOf(dto.getBusiness().getFinancial().getRevenueProof()));
            financial.setAverageExpenses(String.valueOf(dto.getBusiness().getFinancial().getAverageExpenses()));
            financial.setExpectedIncome(String.valueOf(dto.getBusiness().getFinancial().getExpectedIncome()));
            financial.setAverageInvestment(String.valueOf(dto.getBusiness().getFinancial().getAverageInvestment()));
            financial.setCompany(savedCompany);
            financial.setCreatedUser(createdUser);
            financialRepository.save(financial);
        }

        return savedCIF;
    }
    public CIF changeUserStatus(int userId, int statusCode) {
        // Retrieve user from repository
        CIF cif = cifRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("CIF not found"));
        // Set the new status code
        cif.setStatus(statusCode);
        cif.setUpdatedDate(LocalDateTime.now());
        // Save changes
        cifRepository.save(cif);
        // Convert the updated entity to a DTO (using your conversion logic)
        return cif;
    }
    public CIF updateCIF(int id, UpdatedCifDTO updatedCifDTO) {
        // Find the existing CIF record or throw an exception if not found
        CIF existingCif = cifRepository.findById(id)
                .orElseThrow(() -> new ServiceException("CIF not found with id " + id));

        // Update individual fields from the DTO
        existingCif.setName(updatedCifDTO.getName());
        existingCif.setEmail(updatedCifDTO.getEmail());
        existingCif.setPhoneNumber(updatedCifDTO.getPhoneNumber());
        existingCif.setNRC(updatedCifDTO.getNrc());
        existingCif.setNRCFrontPhoto(updatedCifDTO.getNrcFrontPhoto());
        existingCif.setNRCBackPhoto(updatedCifDTO.getNrcBackPhoto());
        existingCif.setDateOfBirth(updatedCifDTO.getDateOfBirth());
        existingCif.setGender(updatedCifDTO.getGender());
        existingCif.setPhoto(updatedCifDTO.getPhoto());
        existingCif.setCifType(updatedCifDTO.getCifType());

        // Update or create the Address using cascading.
        if (updatedCifDTO.getAddress() != null) {
            Address address = existingCif.getAddress();
            if (address == null) {
                address = new Address();
            }
            address.setState(updatedCifDTO.getAddress().getState());
            address.setTownship(updatedCifDTO.getAddress().getTownship());
            address.setCity(updatedCifDTO.getAddress().getCity());
            address.setAdditionalAddress(updatedCifDTO.getAddress().getAdditionalAddress());
            existingCif.setAddress(address);
        }

        // Save and return the updated CIF entity.
        // Because of the cascade setting, the Address will be automatically saved/merged.
        return cifRepository.save(existingCif);
    }

    private Address createAddress(AddressDTO dto) {
        Address address = new Address();
        address.setState(dto.getState());
        address.setTownship(dto.getTownship());
        address.setCity(dto.getCity());
        address.setAdditionalAddress(dto.getAdditionalAddress());
        return address;
    }
    public Page<CIF> getAllCIFs(Pageable pageable){
        return cifRepository.findAll(pageable);
    }
    public Page<CIF> getAllBystatus(String status,Pageable pageable){
        int statusCode = 0;
        switch(status.toLowerCase()) {
            case "active":
                statusCode = ConstraintEnum.ACTIVE.getCode();
                break;
            case "terminated":
                statusCode = ConstraintEnum.TERMINATED.getCode();
                break;

            default:

        }
        return cifRepository.findByStatus(statusCode,pageable);
    }
    public Page<CIF> getAllByBranchAndstatus(Integer branchId,String status,Pageable pageable){
        int statusCode = 0;
        switch(status.toLowerCase()) {
            case "active":
                statusCode = ConstraintEnum.ACTIVE.getCode();
                break;
            case "terminated":
                statusCode = ConstraintEnum.TERMINATED.getCode();
                break;

            default:

        }
        Branch branch = branchRepository.findById(branchId) .orElseThrow(() -> new ServiceException("branch not found"));
        return cifRepository.findByBranchCodeAndStatus(branch.getBranchCode(),statusCode,pageable);
    }

    public CIF registerCIF(CIF cif, int addressId, int createdUserId) {

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException("Address not found"));
        System.out.println(" Address ID: " + addressId);

        User createdUser = userRepository.findById(createdUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        System.out.println(" Created User ID: " + createdUserId);

        String cifCode = codeGenerateService.generateCIFCode(createdUser);
            cif.setCifCode(cifCode);

        if(cifRepository.findByCifCode(cif.getCifCode()) != null){
            throw new ServiceException("Cif Code is already have");
        }

        if(cifRepository.findByEmail(cif.getEmail()) != null){
            throw new ServiceException("Email is already exit");
        }

        if(cifRepository.findByPhoneNumber(cif.getPhoneNumber()) != null){
            throw new ServiceException("Phone Number is already register");
        }

        cif.setAddress(address);
        cif.setCreatedUser(createdUser);
        cif.setCreatedDate(LocalDateTime.now());
        cif.setUpdatedDate(LocalDateTime.now());

        return cifRepository.save(cif);
    }
    public Page<CIF> getCIFsByBranchCode(String branchCode, Pageable pageable) {
        return cifRepository.findByBranchCode(branchCode, pageable);

    }
    public Page<CIF> getByBranch(Integer branchid, Pageable pageable){
        Branch branch = branchRepository.findById(branchid) .orElseThrow(() -> new UserNotFoundException("Branch not found with id: " + branchid));
        return cifRepository.findByBranchCode(branch.getBranchCode(), pageable);
    }
    public List<CIF> getCIFForSelect(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return cifRepository.findAll();
        }
        return cifRepository.findByCifCodeContainingIgnoreCase(searchTerm);
    }

    public List<CIF> getCIFsToSelect(int userId, String searchTerm) {
        // Retrieve the user or throw an exception if not found.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // For RegularBranchLevel, filter by the branch.
        if (user.getRole().getAuthority().equals(Role.AUTHORITY.RegularBranchLevel)) {
            Branch branch = branchRepository.findById(user.getBranch().getId())
                    .orElseThrow(() -> new UserNotFoundException("Branch not found for user with id: " + userId));
            System.out.println("Branch Code: " + branch.getBranchCode());
            return cifRepository.findByBranchCodeAndCifCodeContainingIgnoreCase(branch.getBranchCode(), searchTerm);
        } else {
            // For MainBranchLevel, search all CIF records using the search term.
            return cifRepository.findByCifCodeContainingIgnoreCase(searchTerm);
        }
    }

    public CIF getById(Integer id){
        return cifRepository.findById(id).orElseThrow(() -> new CifNotFoundException("CIF not found"));
    }

    public Page<CIF> getAll(Pageable pageable){
        return cifRepository.findAll(pageable);
    }

    public Page<CIF> findCIFsByBranchCode(String branchCode, Pageable pageable) {
        return cifRepository.findByBranchCode(branchCode, pageable);
    }
    public Set<String> getAllUniqueEmails() {
        List<String> cifEmails = cifRepository.findAllUniqueEmails();
        List<String> userEmails = userRepository.findAllUniqueEmails();

        // Use a Set to avoid duplicates
        Set<String> allUniqueEmails = new HashSet<>();
        allUniqueEmails.addAll(cifEmails);
        allUniqueEmails.addAll(userEmails);

        return allUniqueEmails;
    }

    public Set<String> getAllUniquePhoneNumbers() {
        List<String> cifPhoneNumbers = cifRepository.findAllUniquePhoneNumbers();
        List<String> userPhoneNumbers = userRepository.findAllUniquePhoneNumbers();

        // Use a Set to avoid duplicates
        Set<String> allUniquePhoneNumbers = new HashSet<>();
        allUniquePhoneNumbers.addAll(cifPhoneNumbers);
        allUniquePhoneNumbers.addAll(userPhoneNumbers);

        return allUniquePhoneNumbers;
    }

    public Page<CIF> findByState(String state, Pageable pageable) {
        return cifRepository.findByState(state, pageable);
    }

    public CIF updateCif(Integer id, CIF updatedCif){
        CIF existingCIF = cifRepository.getById(id);
        if (existingCIF == null) {
            throw new CifNotFoundException("CIF record not found");
        }


        if (!existingCIF.getEmail().equals(updatedCif.getEmail()) &&
                cifRepository.findByEmail(updatedCif.getEmail()) != null) {
            throw new ServiceException("Email already exists");
        }
        if (!existingCIF.getPhoneNumber().equals(updatedCif.getPhoneNumber()) &&
                cifRepository.findByPhoneNumber(updatedCif.getPhoneNumber()) != null) {
            throw new ServiceException("Phone Number is already registered");
        }

        // Update CIF details
        existingCIF.setName(updatedCif.getName());
        existingCIF.setEmail(updatedCif.getEmail());
        existingCIF.setPhoneNumber(updatedCif.getPhoneNumber());
        existingCIF.setAddress(updatedCif.getAddress());
        existingCIF.setUpdatedDate(LocalDateTime.now());

        return cifRepository.save(existingCIF);
    }



}
