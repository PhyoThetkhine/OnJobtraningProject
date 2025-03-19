package com.prj.LoneHPManagement.Service;

import com.prj.LoneHPManagement.model.dto.ConfirmLoanData;
import com.prj.LoneHPManagement.model.dto.CreateHpLoanDto;
import com.prj.LoneHPManagement.model.dto.UpdateHPLoanRequest;
import com.prj.LoneHPManagement.model.entity.HpLoan;
import com.prj.LoneHPManagement.model.entity.HpTerm;
import com.prj.LoneHPManagement.model.entity.SMELoan;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface HpLoanService {
    Page<HpLoan> getHPLoansByCif(int cifId, int page, int size, String sortBy);
    List<HpTerm> getTermsByLoanId(int loanId);
    void confirmLoan(int loanId);
    Page<HpLoan> getLoans(int page, int size, String sortBy);
    LocalDate calculateEndDate(LocalDate startDate, int duration);
    List<HpTerm> generateTerms(HpLoan loan, LocalDate startDate, int duration);
    HpLoan save(HpLoan hpLoan);
    void confirm_Loan(int loanId, ConfirmLoanData confirmData);
    HpLoan getLoanById(Integer id);
    HpLoan createLoan(CreateHpLoanDto request);
    HpLoan updateLoanById(Integer id , HpLoan hpLoan);
    HpLoan updateLoan(int id, UpdateHPLoanRequest request);

    List<HpLoan> getAllLoans();

    HpLoan findByHpLoanCode(String hpLoanCode);
}