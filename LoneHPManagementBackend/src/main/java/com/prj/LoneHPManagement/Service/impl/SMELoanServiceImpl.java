package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.Service.CodeGenerateService;
import com.prj.LoneHPManagement.Service.MyanmarHolidayService;
import com.prj.LoneHPManagement.Service.SMELoanService;
import com.prj.LoneHPManagement.Service.SMETermCalculationService;
import com.prj.LoneHPManagement.model.dto.ConfirmLoanData;
import com.prj.LoneHPManagement.model.dto.CreateSMELoanRequest;
import com.prj.LoneHPManagement.model.dto.UpdateSMELoanRequest;
import com.prj.LoneHPManagement.model.entity.*;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import com.prj.LoneHPManagement.model.repo.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SMELoanServiceImpl implements SMELoanService {
    @Autowired
    private MyanmarHolidayService myanmarHolidayService;

    @Autowired
    private SMELoanRepository smeLoanRepository;

    @Autowired
    private UserCurrentAccountRepository userCurrentAccountRepository;

    @Autowired
    private CIFCurrentAccountRepository cifCurrentAccountRepository;
    @Autowired
    private CollateralRepository collateralRepository;

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private SMETermCalculationService smeTermCalculationService;
    @Autowired
    private SMETermRepository smeTermRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private CIFRepository cifRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CodeGenerateService codeGenerateService;
    @Autowired
    private LoanCollateralRepository loanCollateralRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private BranchCurrentAccountRepository branchCurrentAccountRepository;
    @Override
    public Page<SMELoan> getSMELoansByCif(int cifId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return smeLoanRepository.findByCifId(cifId, pageable);
    }

    @Override
    public void updatePrincipalStatus(int loanId, String status) {
        int statusCode;
        if ("paid".equalsIgnoreCase(status)) {
            statusCode = ConstraintEnum.PAID.getCode();
        } else if ("not_paid".equalsIgnoreCase(status)) {
            statusCode = ConstraintEnum.NOT_PAID.getCode();
        } else {
            throw new ServiceException("Invalid principal status: " + status);
        }

        // Call the repository method to update the status directly
        smeLoanRepository.updatePaidPrincipalStatus(statusCode, loanId);
    }
    @Override
    public void confirm_Loan(int loanId, ConfirmLoanData confirmData) {
        if (confirmData.getDisbursementAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("Disbursement amount must be greater than zero.");
        }
        BigDecimal disbursementAmount = confirmData.getDisbursementAmount();
        int documentFeeRate = confirmData.getDocumentFeeRate();
        int serviceChargeRate = confirmData.getServiceChargeRate();
        // Calculate Document Fee
        BigDecimal documentFee = disbursementAmount.multiply(BigDecimal.valueOf(documentFeeRate))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // Calculate Service Charge
        BigDecimal serviceCharge = disbursementAmount.multiply(BigDecimal.valueOf(serviceChargeRate))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        SMELoan loan = smeLoanRepository.findById(loanId)
                .orElseThrow(() -> new ServiceException("Loan not found with ID: " + loanId));
        User confirmUser = userRepository.findById(confirmData.getConfirmUserId())
                .orElseThrow(() -> new ServiceException("User not found with ID: " + confirmData.getConfirmUserId()));
        BranchCurrentAccount branchCurrentAccount= branchCurrentAccountRepository.findByBranch_Id(confirmUser.getBranch().getId()) .orElseThrow(() -> new ServiceException("branch account  not found with ID: " + confirmData.getConfirmUserId()));
       CIFCurrentAccount cifaccount = cifCurrentAccountRepository.findByCifId(loan.getCif().getId()) .orElseThrow(() -> new ServiceException("cif acccount not found"));

        // Update loan details with confirmation data
        loan.setDisbursementAmount(disbursementAmount);
        loan.setDocumentFeeRate(documentFeeRate);
        loan.setServiceChargeFeeRate(serviceChargeRate);
        loan.setGracePeriod(confirmData.getGracePeriod());
        loan.setInterestRate(confirmData.getInterestRate());
        loan.setLateFeeRate(confirmData.getLateFeeRate());
        loan.setDefaultedRate(confirmData.getDefaultRate());
        loan.setLongTermOverdueRate(confirmData.getLongTermOverdueRate());
        loan.setDocumentFee(documentFee);
        loan.setServiceCharge(serviceCharge);
        loan.setConfirmUser(confirmUser);
        // Convert paidPrincipalStatus from string to int
        if ("paid".equalsIgnoreCase(confirmData.getPaidPrincipalStatus())) {
            loan.setPaidPrincipalStatus(ConstraintEnum.PAID.getCode());
        } else if ("not_paid".equalsIgnoreCase(confirmData.getPaidPrincipalStatus())) {
            loan.setPaidPrincipalStatus(ConstraintEnum.NOT_PAID.getCode());
        } else {
            throw new ServiceException("Invalid paid principal status: " + confirmData.getPaidPrincipalStatus());
        }


        LocalDate startDate = LocalDate.now();
        LocalDate endDate = calculateEndDate(startDate, loan.getFrequency(), loan.getDuration());
        LocalDate adjustEndDate = adjustForWorkingDay(endDate);
        // 8. Update loan status and dates
        updateLoanStatus(loan, startDate, adjustEndDate);
        SMELoan smeLoan = smeLoanRepository.save(loan);
        // 6. Generate terms
        List<SMETerm> terms = generateTerms(smeLoan, startDate, loan.getFrequency(), loan.getDuration());
        smeTermRepository.saveAll(terms);
        // Transfer disbursementAmount from branch to CIF account
        if (branchCurrentAccount.getBalance().compareTo(disbursementAmount) < 0) {
            throw new ServiceException("Branch account has insufficient funds.");
        }

        // Update balances
        branchCurrentAccount.setBalance(
                branchCurrentAccount.getBalance().subtract(disbursementAmount)
        );
        cifaccount.setBalance(
                cifaccount.getBalance().add(disbursementAmount)
        );

        // Save the updated accounts
        branchCurrentAccountRepository.save(branchCurrentAccount);
        cifCurrentAccountRepository.save(cifaccount);
    }

    @Override
    public List<SMETerm> getTermsByLoanId(int loanId) {
        return smeTermRepository.findBySmeLoan_Id(loanId);
    }
    @Override
    public Page<SMELoan> getSMELoans(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return smeLoanRepository.findAllSortedByApplicationDate(pageable);
    }

    @Override
    public Page<SMELoan> getSMELoansByBranch(int page, int size, String sortBy, Integer branchId) {
        Branch branch = branchRepository.findById(branchId) .orElseThrow(() -> new ServiceException("User not found with ID: " +branchId ));
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return smeLoanRepository.findByBranchCode(branch.getBranchCode(),pageable);
    }
    @Override
    public Page<SMELoan> getSMELoansByBranchAndStatus(int page, int size, String sortBy, Integer branchId,String status) {
        Branch branch = branchRepository.findById(branchId) .orElseThrow(() -> new ServiceException("User not found with ID: " +branchId ));
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        int statusCode = 0;
        switch(status.toLowerCase()) {
            case "under_review":
                statusCode = ConstraintEnum.UNDER_REVIEW.getCode();
                break;
            case "rejected":
                statusCode = ConstraintEnum.REJECTED.getCode();
                break;
            case "paid_off":
                statusCode = ConstraintEnum.PAID_OFF.getCode();
                break;
            case "under_schedule":
                statusCode = ConstraintEnum.UNDER_SCHEDULE.getCode();
                break;
            default:
        }
        return smeLoanRepository.findByBranchCodeAndStatus(branch.getBranchCode(),pageable,statusCode);
    }

    @Override
    public Page<SMELoan> getSMELoansByStatus(int page, int size, String sortBy, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        int statusCode = 0;
        switch(status.toLowerCase()) {
            case "under_review":
                statusCode = ConstraintEnum.UNDER_REVIEW.getCode();
                break;
            case "rejected":
                statusCode = ConstraintEnum.REJECTED.getCode();
                break;
            case "paid_off":
                statusCode = ConstraintEnum.PAID_OFF.getCode();
                break;
            case "under_schedule":
                statusCode = ConstraintEnum.UNDER_SCHEDULE.getCode();
                break;
            default:
        }
        return smeLoanRepository.findByStatus(statusCode,pageable);
    }


    @Override
    public void confirmLoan(Integer loanId) {
        // 1. Retrieve the loan with necessary relationships
        SMELoan loan = smeLoanRepository.findById(loanId)
                .orElseThrow(() -> new ServiceException("Loan not found with ID: " + loanId));

        // 2. Validate loan status
        if (loan.getStatus() != ConstraintEnum.UNDER_REVIEW.getCode()) {
            throw new ServiceException("Loan must be in UNDER_REVIEW status for confirmation");
        }
        // 3. Get related accounts
        User confirmUser = loan.getConfirmUser();
        UserCurrentAccount userAccount = userCurrentAccountRepository.findByUserId(confirmUser.getId())
                .orElseThrow(() -> new ServiceException("User current account not found for user ID: " + confirmUser.getId()));

        CIF cif = loan.getCif();
        CIFCurrentAccount cifAccount = cifCurrentAccountRepository.findByCifId(cif.getId())
                .orElseThrow(() -> new ServiceException("CIF current account not found for CIF ID: " + cif.getId()));

        // 4. Validate account conditions
        validateAccountConditions(userAccount, loan.getDisbursementAmount());

        // 5. Calculate dates
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = calculateEndDate(startDate, loan.getFrequency(), loan.getDuration());
        LocalDate adjustEndDate = adjustForWorkingDay(endDate);
        // 6. Generate terms
        List<SMETerm> terms = generateTerms(loan, startDate, loan.getFrequency(), loan.getDuration());
        for (SMETerm term : terms) {
            smeTermCalculationService.updateOutstandingAmount(term); // Calculate and set the outstanding amount
        }
        // 7. Perform fund transfer
        transferFunds(userAccount, cifAccount, loan.getDisbursementAmount(), loan);
        // 8. Update loan status and dates
        updateLoanStatus(loan, startDate, adjustEndDate);
        // 9. Save all entities
        smeTermRepository.saveAll(terms);
        smeLoanRepository.save(loan);
    }

    @Override
    public LocalDate calculateEndDate(LocalDate startDate, SMELoan.FREQUENCY frequency, int duration) {
        return switch (frequency) {
            case MONTHLY -> startDate.plusMonths(duration);
            case YEARLY -> startDate.plusYears(duration);
        };
    }

    @Override
    public List<SMETerm> generateTerms(SMELoan loan, LocalDate startDate, SMELoan.FREQUENCY frequency, int duration) {
        List<SMETerm> terms = new ArrayList<>();
        BigDecimal principal = loan.getDisbursementAmount();
        int interestRate = loan.getInterestRate();
        LocalDate initialEndDate = calculateEndDate(startDate, frequency, duration);
        LocalDate adjustedEndDate = adjustForWorkingDay(initialEndDate);
        LocalDate termStartDate = startDate;

        for (int i = 0; i < duration; i++) {
            // Corrected line: Use (i + 1) for both MONTHLY and YEARLY frequencies
            LocalDate originalDueDate = startDate.plus(i + 1,
                    frequency == SMELoan.FREQUENCY.MONTHLY ? ChronoUnit.MONTHS : ChronoUnit.YEARS);
            LocalDate termDueDate = adjustForWorkingDay(originalDueDate);

            if (i == duration - 1) {
                termDueDate = adjustedEndDate;
            }

            int days = (int) ChronoUnit.DAYS.between(termStartDate, termDueDate);
            BigDecimal termInterest = principal.multiply(BigDecimal.valueOf(interestRate))
                    .multiply(BigDecimal.valueOf(days))
                    .divide(BigDecimal.valueOf(365 * 100), 10, RoundingMode.HALF_UP);

            SMETerm term = new SMETerm();
            term.setPrincipal(principal);
            term.setInterest(termInterest);
            term.setDueDate(java.sql.Date.valueOf(termDueDate));
            term.setDays(days);
            term.setSmeLoan(loan);

            terms.add(term);
            termStartDate = termDueDate;
        }

        return terms;
    }

    @Override
    public SMELoan createLoan(CreateSMELoanRequest request) {

        // Fetch CIF
        CIF cif = cifRepository.findById(request.getCifId())
                .orElseThrow(() -> new ServiceException("CIF not found"));

        // Fetch createdUser
        User createdUser  = userRepository.findById(request.getCreatedUserId())
                .orElseThrow(() -> new ServiceException("User  not found"));

        // Generate smeLoanCode
        String smeLoanCode = codeGenerateService.generateLoanCode(cif);
        // Set default values for fields not provided by the frontend
        SMELoan loan = new SMELoan();
        loan.setSmeLoanCode(smeLoanCode);
        loan.setPurpose(request.getPurpose());
        loan.setFrequency(request.getFrequency());
        loan.setLoanAmount(request.getLoanAmount());
        loan.setDisbursementAmount(BigDecimal.ZERO);
        loan.setDuration(request.getDuration());
        loan.setInterestRate(0);
        loan.setGracePeriod(0);
        loan.setLateFeeRate(0);
        loan.setDefaultedRate(0);
        loan.setLongTermOverdueRate(0);
        loan.setDocumentFeeRate(0);
        loan.setServiceChargeFeeRate(0);
        loan.setApplicationDate(LocalDateTime.now());
        loan.setDocumentFee(BigDecimal.ZERO);
        loan.setServiceCharge(BigDecimal.ZERO);
        loan.setUpdatedDate(LocalDateTime.now());
        loan.setStatus(ConstraintEnum.UNDER_REVIEW.getCode());
        loan.setCif(cif);

        loan.setCreatedUser(createdUser);
        SMELoan savedLoan = smeLoanRepository.save(loan);
        // Process collaterals
        List<Collateral> collaterals = collateralRepository.findAllById(request.getCollateralIds());
        if (collaterals.size() != request.getCollateralIds().size()) {
            throw new ServiceException("One or more collaterals not found");
        }
        List<LoanCollateral> loanCollaterals = new ArrayList<>();
        for (Collateral collateral : collaterals) {
            LoanCollateral loanCollateral = new LoanCollateral();
            loanCollateral.setId(new LoanCollateral.LoanCollateralPK(savedLoan.getId(), collateral.getId()));
            loanCollateral.setLoan(savedLoan);
            loanCollateral.setCollateral(collateral);
            loanCollaterals.add(loanCollateral);
        }
        loanCollateralRepository.saveAll(loanCollaterals);
        return savedLoan;
    }

    @Override
    public SMELoan save(SMELoan smeLoan) {
        return null;
    }

    @Override
    public SMELoan getLoanById(Integer id) {
        return smeLoanRepository.findById(id).orElseThrow(() -> new ServiceException("Loan not found with ID: " + id));
    }

    @Override
    public SMELoan updateLoanById(Integer id, SMELoan smeLoan) {
        return null;
    }

    @Override
    public SMELoan updateLoan(int loanId, UpdateSMELoanRequest request) {
        SMELoan loan = smeLoanRepository.findById(loanId)
                .orElseThrow(() -> new ServiceException("Loan not found"));
        loan.setPurpose(request.getPurpose());
        loan.setFrequency(request.getFrequency());
        loan.setLoanAmount(request.getLoanAmount());
        loan.setDuration(request.getDuration());
        loan.setUpdatedDate(LocalDateTime.now());
        loanCollateralRepository.deleteByLoanId(loan.getId());
        List<Collateral> collaterals = collateralRepository.findAllById(request.getCollateralIds());
        if (collaterals.size() != request.getCollateralIds().size()) {
            throw new ServiceException("One or more collaterals not found");
        }
        // 3. Create new LoanCollateral associations
        List<LoanCollateral> loanCollaterals = new ArrayList<>();
        for (Collateral collateral : collaterals) {
            LoanCollateral loanCollateral = new LoanCollateral();
            loanCollateral.setId(new LoanCollateral.LoanCollateralPK(loan.getId(), collateral.getId()));
            loanCollateral.setLoan(loan);
            loanCollateral.setCollateral(collateral);
            loanCollaterals.add(loanCollateral);
        }
        loanCollateralRepository.saveAll(loanCollaterals);

        // Save the updated loan
        SMELoan updatedLoan = smeLoanRepository.save(loan);
        return updatedLoan;
    }

    @Override
    public List<SMELoan> getAllLoans() {
        return List.of();
    }

    @Override
    public SMELoan findLoanBySmeLoanCode(String smeLoanCode) {
        return null;
    }

    private LocalDate adjustForWorkingDay(LocalDate date) {
        while (myanmarHolidayService.isHoliday(date)) {
            date = date.plusDays(1);
        }
        return date;
    }

    private void validateAccountConditions(UserCurrentAccount account, BigDecimal amount) {
        // Check account freeze status
        if (account.getIsFreeze() == ConstraintEnum.IS_FREEZE.getCode()) {
            throw new ServiceException("Account is frozen");
        }

        // Check sufficient balance (considering 2 decimal precision)
        if (account.getBalance().compareTo(amount) < 0) {
            throw new ServiceException("Insufficient balance in user account");
        }
    }

    private void transferFunds(UserCurrentAccount from, CIFCurrentAccount to, BigDecimal amount, SMELoan loan) {
        // Deduct from user account
        from.setBalance(from.getBalance().subtract(amount));

        // Add to CIF account
        to.setBalance(to.getBalance().add(amount));

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setFromAccountId(from.getId());
        transaction.setToAccountId(to.getId());
        transaction.setFromAccountType(Transaction.AccountType.USER);
        transaction.setToAccountType(Transaction.AccountType.CIF);
        transaction.setAmount(amount);
        transaction.setPaymentMethod(paymentMethodRepository.findById(1).orElseThrow(() -> new RuntimeException("Payment Method not found")));

        // Save all changes
        userCurrentAccountRepository.save(from);
        cifCurrentAccountRepository.save(to);
        transactionRepository.save(transaction);
    }

    private void updateLoanStatus(SMELoan loan, LocalDate startDate, LocalDate endDate) {
        loan.setStatus(ConstraintEnum.UNDER_SCHEDULE.getCode());
        loan.setStartDate(java.sql.Date.valueOf(startDate));
        loan.setEndDate(java.sql.Date.valueOf(endDate));
        loan.setConfirmDate(LocalDateTime.now());
    }
}
