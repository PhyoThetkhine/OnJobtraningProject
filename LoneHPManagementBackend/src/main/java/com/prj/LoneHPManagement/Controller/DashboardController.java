package com.prj.LoneHPManagement.Controller;

import com.prj.LoneHPManagement.model.dto.BranchBalanceDTO;
import com.prj.LoneHPManagement.model.dto.DashboardDTO;
import com.prj.LoneHPManagement.model.dto.LoanStatusChartDTO;
import com.prj.LoneHPManagement.model.entity.ConstraintEnum;
import com.prj.LoneHPManagement.model.repo.*;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@AllArgsConstructor
public class DashboardController {

    private final CIFRepository cifRepository;
    private final UserRepository userRepository;
    private final SMELoanRepository smeLoanRepository;
    private final HpLoanRepository hpLoanRepository;
    private final BranchCurrentAccountRepository branchAccountRepository;

    // Constructor injection

    @GetMapping
    public ResponseEntity<DashboardDTO> getDashboardStats() {
        DashboardDTO dto = new DashboardDTO();

        dto.setTotalCif(cifRepository.count());
        dto.setTotalUsers(userRepository.count());
        dto.setTotalLoans(smeLoanRepository.count() + hpLoanRepository.count());
        dto.setTotalBranchAccounts(branchAccountRepository.count());
        dto.setActiveLoans(
                smeLoanRepository.countByStatus(ConstraintEnum.UNDER_SCHEDULE.getCode()) +
                        hpLoanRepository.countByStatus(ConstraintEnum.UNDER_SCHEDULE.getCode())
        );
        dto.setTotalLoanAmount(
                smeLoanRepository.sumLoanAmount()
                        .add(hpLoanRepository.sumLoanAmount())
        );

        return ResponseEntity.ok(dto);
    }
    @GetMapping("/loan-status")
    public List<LoanStatusChartDTO> getLoanStatusData() {
        List<LoanStatusChartDTO> result = new ArrayList<>();

        // Process SME Loans
        smeLoanRepository.getLoanStatusCounts().forEach(objects ->
                result.add(createDto("SME", objects))
        );

        // Process HP Loans
        hpLoanRepository.getLoanStatusCounts().forEach(objects ->
                result.add(createDto("HP", objects))
        );

        return result;
    }

    private LoanStatusChartDTO createDto(String loanType, Object[] row) {
        LoanStatusChartDTO dto = new LoanStatusChartDTO();
        dto.setYear((Integer) row[0]);
        dto.setStatus(convertStatus((Integer) row[1]));
        dto.setCount((Long) row[2]);
        dto.setLoanType(loanType);
        return dto;
    }

    private String convertStatus(int statusCode) {
        // Use the ConstraintEnum to map the status code to its description.
        ConstraintEnum statusEnum = ConstraintEnum.fromCode(statusCode);
        return (statusEnum != null) ? statusEnum.getDescription().toUpperCase() : "UNKNOWN";
    }

    @GetMapping("/branch-balances")
    public List<BranchBalanceDTO> getBranchBalances() {
        return branchAccountRepository.findBranchBalances();
    }
}