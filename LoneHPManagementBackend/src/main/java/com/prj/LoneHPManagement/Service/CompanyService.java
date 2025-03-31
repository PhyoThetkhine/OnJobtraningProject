package com.prj.LoneHPManagement.Service;

import com.prj.LoneHPManagement.model.dto.CompanyCreateRequest;
import com.prj.LoneHPManagement.model.dto.CompanyDTO;
import com.prj.LoneHPManagement.model.dto.CompanyDTOUpdate;
import com.prj.LoneHPManagement.model.entity.BusinessPhoto;
import com.prj.LoneHPManagement.model.entity.Company;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CompanyService {
    Company createCompany(CompanyDTO request);
    Company updateCompany(int id, CompanyDTOUpdate request);
    Page<Company> getCompaniesByCifId(int cifId, int page, int size, String sortBy);
//    Company save(CompanyDTO companyDTO);
    List<BusinessPhoto> getPhotosByCompanyId(int companyId);
    List<Company> getAllCompanies();

    Company getCompanyById(int id);

    void deleteCompany(int id);




}
