package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.Service.FinancialService;
import com.prj.LoneHPManagement.model.entity.Company;
import com.prj.LoneHPManagement.model.entity.Financial;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import com.prj.LoneHPManagement.model.repo.CompanyRepository;
import com.prj.LoneHPManagement.model.repo.FinancialRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FinancialServiceImpl implements FinancialService {
    @Autowired
    private final FinancialRepository financialRepository;
    @Autowired
    private  CompanyRepository companyRepository;

    public FinancialServiceImpl(FinancialRepository financialRepository, CompanyRepository companyRepository) {
        this.financialRepository = financialRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public Financial createFinancial(int companyId, Financial financial) {
        Optional<Company> company = companyRepository.findById(companyId);
        if (company.isEmpty()) {
            throw new ServiceException("Company not found with ID: " + companyId);
        }
        financial.setCompany(company.get());
        return financialRepository.save(financial);
    }

    @Override
    public List<Financial> getAllFinancials() {
        return financialRepository.findAll();
    }

    @Override
    public Financial getFinancialById(int id) {
        return financialRepository.findById(id)
                .orElseThrow(() -> new ServiceException("Financial record not found with ID: " + id));
    }

    @Override
    public Financial updateFinancial(int id, Financial financialDetails) {
        Financial financial = financialRepository.findById(id)
                .orElseThrow(() -> new ServiceException("Financial record not found with ID: " + id));

        financial.setAverageIncome(financialDetails.getAverageIncome());
        financial.setAverageEmployees(financialDetails.getAverageEmployees());
        financial.setAverageSalaryPaid(financialDetails.getAverageSalaryPaid());
        financial.setRevenueProof(financialDetails.getRevenueProof());
        financial.setAverageExpenses(financialDetails.getAverageExpenses());
        financial.setExpectedIncome(financialDetails.getExpectedIncome());
        financial.setAverageInvestment(financialDetails.getAverageInvestment());

        return financialRepository.save(financial);
    }

    @Override
    public void deleteFinancial(int id) {
        if (!financialRepository.existsById(id)) {
            throw new ServiceException("Financial record not found with ID: " + id);
        }
        financialRepository.deleteById(id);
    }

    // ✅ Fetch financial records by company ID
    @Override
    public Financial getFinancialByCompanyId(int companyId) {
        return financialRepository.findByCompany_Id(companyId)
                .orElseThrow(() -> new ServiceException(
                        "Financial data not found for company ID: " + companyId));
    }

}
