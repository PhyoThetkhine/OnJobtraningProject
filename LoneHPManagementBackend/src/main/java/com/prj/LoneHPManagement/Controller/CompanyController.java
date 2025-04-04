package com.prj.LoneHPManagement.Controller;

import com.prj.LoneHPManagement.Service.CompanyService;
import com.prj.LoneHPManagement.Service.impl.CompanyServiceImpl;
import com.prj.LoneHPManagement.model.dto.*;
import com.prj.LoneHPManagement.model.entity.BusinessPhoto;
import com.prj.LoneHPManagement.model.entity.Company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@CrossOrigin(origins = "http://localhost:4200")
public class CompanyController {
    @Autowired
    private CompanyService companyService;
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<Company>> createCompany(@RequestBody CompanyDTO request) {
        Company company = companyService.createCompany(request);
        ApiResponse<Company> response = ApiResponse.success(HttpStatus.OK.value(),
                "Company created successfully", company);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Company>> updateCompany(@PathVariable int id, @RequestBody CompanyDTOUpdate request) {
        Company company = companyService.updateCompany(id, request);
        ApiResponse<Company> response = ApiResponse.success(HttpStatus.OK.value(), "Company updated successfully", company);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/byCompany/{companyId}")
    public ResponseEntity<ApiResponse<List<BusinessPhoto>>> getBusinessPhotosByCompany(
            @PathVariable int companyId) {

        List<BusinessPhoto> photos = companyService.getPhotosByCompanyId(companyId);

        ApiResponse<List<BusinessPhoto>> response = ApiResponse.success(
                200,
                "Business photos for company " + companyId + " retrieved successfully",
                photos
        );

        return ResponseEntity.ok(response);
    }
    @GetMapping("/byCif/{cifId}")
    public ResponseEntity<ApiResponse<PagedResponse<Company>>> getCompaniesByCif(
            @PathVariable int cifId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Page<Company> companies = companyService.getCompaniesByCifId(cifId, page, size, sortBy);

        PagedResponse<Company> pagedResponse = new PagedResponse<>(
                companies.getContent(),
                companies.getTotalPages(),
                companies.getTotalElements(),
                companies.getSize(),
                companies.getNumber(),
                companies.getNumberOfElements(),
                companies.isFirst(),
                companies.isLast(),
                companies.isEmpty()
        );

        ApiResponse<PagedResponse<Company>> response = ApiResponse.success(
                200,
                "Companies for CIF " + cifId + " retrieved successfully",
                pagedResponse
        );

        return ResponseEntity.ok(response);
    }

//    @PostMapping("/save")
//    public ResponseEntity<ApiResponse<Company>> save(@RequestBody CompanyDTO companyDTO) {
//        // Map DTO to entity, ensure you fetch referenced entities
//        Company savedCompany = companyService.save(companyDTO);
//        ApiResponse<Company> response = ApiResponse.success(HttpStatus.OK.value(),
//                "Company created successfully", savedCompany);
//        return ResponseEntity.ok(response);
//    }



    // ✅ Get all companies
    @GetMapping("/list")
    public ResponseEntity<?> getAllCompanies() {
        List<Company> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Companies fetched successfully", companies));
    }

    // ✅ Get company by ID
    @GetMapping("list/{id}")
    public ResponseEntity<?> getCompanyById(@PathVariable int id) {
        Company company = companyService.getCompanyById(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Company found", company));
    }

    // ✅ Update company by ID
//    @PutMapping("/update/{id}")
//    public ResponseEntity<?> updateCompany(@PathVariable int id, @RequestBody CompanyDTO companyDTO) {
//        try {
//            Company updatedCompany = companyService.updateCompany(id, companyDTO);
//            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Company updated successfully", updatedCompany).getData());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
//        }
//    }

    // ✅ Delete company by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCompany(@PathVariable int id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.NO_CONTENT.value(), "Company deleted successfully", null));
    }


}

