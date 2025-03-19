package com.prj.LoneHPManagement.Service;

import com.prj.LoneHPManagement.model.dto.ConfirmLoanData;
import com.prj.LoneHPManagement.model.dto.CreateSMELoanRequest;
import com.prj.LoneHPManagement.model.dto.UpdateSMELoanRequest;
import com.prj.LoneHPManagement.model.entity.SMELoan;
import com.prj.LoneHPManagement.model.entity.SMETerm;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface SMELoanService {
    Page<SMELoan> getSMELoansByCif(int cifId, int page, int size, String sortBy);
    void updatePrincipalStatus(int loanId, String status);
    void confirm_Loan(int loanId, ConfirmLoanData confirmData);
     List<SMETerm> getTermsByLoanId(int loanId) ;
    Page<SMELoan> getSMELoans(int page, int size, String sortBy);
    void confirmLoan(Integer loanId);
     LocalDate calculateEndDate(LocalDate startDate, SMELoan.FREQUENCY frequency, int duration);
    List<SMETerm> generateTerms(SMELoan loan, LocalDate startDate,
                                SMELoan.FREQUENCY frequency, int duration);
    SMELoan createLoan(CreateSMELoanRequest request);
    SMELoan save(SMELoan smeLoan);

    SMELoan getLoanById(Integer id);

    SMELoan updateLoanById(Integer id , SMELoan smeLoan);
    SMELoan updateLoan(int loanId, UpdateSMELoanRequest request);
    List<SMELoan> getAllLoans();

    SMELoan findLoanBySmeLoanCode(String smeLoanCode);



}
