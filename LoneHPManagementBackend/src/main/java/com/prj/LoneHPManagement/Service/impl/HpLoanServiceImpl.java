package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.Service.CodeGenerateService;
import com.prj.LoneHPManagement.Service.HpLoanService;
import com.prj.LoneHPManagement.Service.HpTermCalculationService;
import com.prj.LoneHPManagement.Service.MyanmarHolidayService;
import com.prj.LoneHPManagement.model.dto.ConfirmLoanData;
import com.prj.LoneHPManagement.model.dto.CreateHpLoanDto;
import com.prj.LoneHPManagement.model.dto.UpdateHPLoanRequest;
import com.prj.LoneHPManagement.model.entity.*;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import com.prj.LoneHPManagement.model.repo.*;
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
public class HpLoanServiceImpl implements HpLoanService {
    @Autowired
    private CodeGenerateService codeGenerateService;
    @Autowired
    private MyanmarHolidayService myanmarHolidayService;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private UserCurrentAccountRepository userCurrentAccountRepository;
    @Autowired
    private CIFCurrentAccountRepository cifCurrentAccountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private HpLoanRepository hpLoanRepository;
    @Autowired
    private CIFRepository cifRepository;
    @Autowired
    private DealerProductRepository dealerProductRepository;
    @Autowired
    private HpTermCalculationService hpTermCalculationService;
    @Autowired
    private HpTermRepository hpTermRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BranchRepository branchRepository;

    @Override
    public Page<HpLoan> getHPLoansByCif(int cifId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return hpLoanRepository.findByCifId(cifId, pageable);
    }

    @Override
    public List<HpTerm> getTermsByLoanId(int loanId) {
        return hpTermRepository.findByHpLoan_Id(loanId);
    }

    @Override
    public void confirmLoan(int loanId) {
        // 1. Retrieve the loan
        HpLoan loan = hpLoanRepository.findById(loanId)
                .orElseThrow(() -> new ServiceException("Loan not found with ID: " + loanId));

        // 2. Validate status
        if (loan.getStatus() != ConstraintEnum.UNDER_REVIEW.getCode()) {
            throw new ServiceException("Loan must be in UNDER_REVIEW status for confirmation");
        }

        // 3. Get accounts
        User confirmUser = loan.getConfirmUser();
        UserCurrentAccount userAccount = userCurrentAccountRepository.findByUserId(confirmUser.getId())
                .orElseThrow(() -> new ServiceException("User account not found"));

        CIF cif = loan.getCif();
        CIFCurrentAccount cifAccount = cifCurrentAccountRepository.findByCifId(cif.getId())
                .orElseThrow(() -> new ServiceException("CIF account not found"));

        // 4. Validate account conditions
        validateAccountConditions(userAccount, loan.getDisbursementAmount());

        // 5. Calculate dates (monthly frequency)
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = calculateEndDate(startDate, loan.getDuration());
        LocalDate adjustedEndDate = adjustForWorkingDay(endDate);

        // 6. Generate terms (monthly)
        List<HpTerm> terms = generateTerms(loan, startDate, loan.getDuration());


        // 7. Transfer funds
        transferFunds(userAccount, cifAccount, loan.getDisbursementAmount(), loan);

        // 8. Update loan status and dates
        updateLoanStatus(loan, startDate, adjustedEndDate);

        // 9. Save entities
        hpTermRepository.saveAll(terms);
        hpLoanRepository.save(loan);
    }
    @Override
    public List<HpTerm> generateTerms(HpLoan loan, LocalDate startDate, int duration) {
        List<HpTerm> terms = new ArrayList<>();
        BigDecimal totalPrincipal = loan.getDisbursementAmount();
        int interestRate = loan.getInterestRate();

        // Split principal into equal parts, handling rounding
        BigDecimal principalPerTerm = totalPrincipal.divide(
                BigDecimal.valueOf(duration), 2, RoundingMode.HALF_UP
        );
        BigDecimal remainingPrincipal = totalPrincipal;

        // Calculate end date and adjust for holidays
        LocalDate initialEndDate = calculateEndDate(startDate, duration);
        LocalDate adjustedEndDate = adjustForWorkingDay(initialEndDate);

        LocalDate termStartDate = startDate;

        for (int i = 0; i < duration; i++) {
            // Calculate term principal (adjust last term for rounding)
            BigDecimal termPrincipal = (i == duration - 1)
                    ? remainingPrincipal
                    : principalPerTerm;
            remainingPrincipal = remainingPrincipal.subtract(termPrincipal);

            // Calculate due date
            LocalDate originalDueDate = startDate.plusMonths(i + 1);
            LocalDate termDueDate = adjustForWorkingDay(originalDueDate);

            // Ensure last term uses adjusted end date
            if (i == duration - 1) {
                termDueDate = adjustedEndDate;
            }

            // Calculate days between term start and due date
            int days = (int) ChronoUnit.DAYS.between(termStartDate, termDueDate);

            // Calculate interest for this term's principal
            BigDecimal termInterest = termPrincipal
                    .multiply(BigDecimal.valueOf(interestRate))
                    .multiply(BigDecimal.valueOf(days))
                    .divide(BigDecimal.valueOf(365 * 100), 10, RoundingMode.HALF_UP);

            // Create and populate the term
            HpTerm term = new HpTerm();
            term.setPrincipal(termPrincipal);
            term.setInterest(termInterest);
            term.setDueDate(java.sql.Date.valueOf(termDueDate));
            term.setDays(days);
            term.setHpLoan(loan);

            // Set unnecessary fields to null or zero
            term.setPrincipalLateFee(BigDecimal.ZERO); // Set to zero if applicable
            term.setPrincipalOfOverdue(BigDecimal.ZERO); // Set to zero if applicable
            term.setInterestOfOverdue(BigDecimal.ZERO); // Assuming you have this field
            term.setInterestLateFee(BigDecimal.ZERO); // Assuming you have this field
            term.setInterestLateDays(0); // Assuming you have this field
            term.setTotalRepaymentAmount(BigDecimal.ZERO); // Assuming you have this field
            term.setOutstandingAmount(BigDecimal.ZERO); // Assuming you have this field
            term.setLastRepayment(BigDecimal.ZERO);
            term.setLastRepayDate(null); // Set to null if not applicable

            terms.add(term);

            // Update term start date for next iteration
            termStartDate = termDueDate;
        }

        return terms;
    }

    @Override
    public LocalDate calculateEndDate(LocalDate startDate, int duration) {
        return startDate.plusMonths(duration);
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

    private void transferFunds(UserCurrentAccount from, CIFCurrentAccount to, BigDecimal amount, HpLoan loan) {
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

    private void updateLoanStatus(HpLoan loan, LocalDate startDate, LocalDate endDate) {
        loan.setStatus(ConstraintEnum.UNDER_SCHEDULE.getCode());
        loan.setStartDate(java.sql.Date.valueOf(startDate));
        loan.setEndDate(java.sql.Date.valueOf(endDate));
        loan.setConfirmDate(LocalDateTime.now());
    }
    @Override
    public HpLoan save(HpLoan hpLoan) {
        return hpLoanRepository.save(hpLoan);
    }

    @Override
    public HpLoan getLoanById(Integer id) {
        return hpLoanRepository.findById(id).orElseThrow(() -> new ServiceException("HPLoan not found"));
    }

    @Override
    public HpLoan createLoan(CreateHpLoanDto request) {
        // Fetch CIF
        CIF cif = cifRepository.findById(request.getCifId())
                .orElseThrow(() -> new ServiceException("CIF not found"));

        // Fetch createdUser
        User createdUser  = userRepository.findById(request.getCreatedUserId())
                .orElseThrow(() -> new ServiceException("User  not found"));
        DealerProduct product = dealerProductRepository.findById(request.getProductId())
                .orElseThrow(() -> new ServiceException("Product not found"));
        String hpLoanCode = codeGenerateService.generateHpLoanCode(cif);
        HpLoan loan = new HpLoan();
        loan.setHpLoanCode(hpLoanCode);
        loan.setDownPayment(request.getDownPayment());
        loan.setProduct(product);
        loan.setLoanAmount(request.getLoanAmount());
       loan.setDisbursementAmount(BigDecimal.ZERO);
       loan.setInterestRate(0);
         loan.setGracePeriod(0);
         loan.setApplicationDate(LocalDateTime.now());
         loan.setLateFeeRate(0);
         loan.setDefaultedRate(0);
         loan.setLongTermOverdueRate(0);
         loan.setDuration(request.getDuration());
         loan.setStatus(ConstraintEnum.UNDER_REVIEW.getCode());
         loan.setDocumentFeeRate(0);
         loan.setDocumentFee(BigDecimal.ZERO);
         loan.setServiceChargeFeeRate(0);
            loan.setServiceCharge(BigDecimal.ZERO);
            loan.setCif(cif);
            loan.setCreatedUser(createdUser);
            loan.setUpdatedDate(LocalDateTime.now());

            return hpLoanRepository.save(loan);

    }

    @Override
    public void confirm_Loan(int loanId, ConfirmLoanData confirmData) {
        // Validate input data
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

        HpLoan loan = hpLoanRepository.findById(loanId)
                .orElseThrow(() -> new ServiceException("Loan not found with id: " + loanId));

        User confirmUser  = userRepository.findById(confirmData.getConfirmUserId())
                .orElseThrow(() -> new ServiceException("Confirm user not found with id: " + confirmData.getConfirmUserId()));

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
        loan.setConfirmUser(confirmUser );


        LocalDate startDate = LocalDate.now();
        LocalDate endDate = calculateEndDate(startDate, loan.getDuration());
        LocalDate adjustedEndDate = adjustForWorkingDay(endDate);
        updateLoanStatus(loan, startDate, adjustedEndDate);
        // Save the updated loan
        HpLoan hpLoan = hpLoanRepository.save(loan);


        // Generate terms (monthly)
        List<HpTerm> terms = generateTerms(hpLoan, startDate, loan.getDuration());
        hpTermRepository.saveAll(terms);
    }

    @Override
    public HpLoan updateLoanById(Integer id, HpLoan hpLoan) {
        if(hpLoanRepository.existsById(id)) {
            hpLoan.setId(id);
            return hpLoanRepository.save(hpLoan);
        }
        return null;
    }

    @Override
    public HpLoan updateLoan(int id, UpdateHPLoanRequest request) {
        HpLoan loan = hpLoanRepository.findById(id)
                .orElseThrow(() -> new ServiceException("HP Loan not found"));
        loan.setLoanAmount(request.getLoanAmount());
        loan.setDownPayment(request.getDownPayment());
        loan.setDuration(request.getDuration());
        loan.setUpdatedDate(LocalDateTime.now());

        // Update the associated product if a new productId is provided
        if (request.getProductId() != null) {
            DealerProduct product = dealerProductRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ServiceException("Dealer Product not found"));
            loan.setProduct(product);
        }
        // Save and return the updated loan
        return hpLoanRepository.save(loan);
    }

    @Override
    public List<HpLoan> getAllLoans() {
        return hpLoanRepository.findAll();
    }

    @Override
    public Page<HpLoan> getLoans(int page, int size, String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return hpLoanRepository.findAllSortedByApplicationDate(pageable);
    }

    @Override
    public Page<HpLoan> getHpLoansByBranch(int page, int size, String sortBy, Integer branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ServiceException("Branch not found with ID: " + branchId));
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return hpLoanRepository.findByBranchCode(branch.getBranchCode(), pageable);
    }
    @Override
    public Page<HpLoan> getHpLoansByBranchAndStatus(int page, int size, String sortBy, Integer branchId, String status) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ServiceException("Branch not found with ID: " + branchId));
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        int statusCode = convertStatusToCode(status);
        return hpLoanRepository.findByBranchCodeAndStatus(branch.getBranchCode(), pageable, statusCode);
    }
    @Override
    public Page<HpLoan> getHpLoansByStatus(int page, int size, String sortBy, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        int statusCode = convertStatusToCode(status);
        return hpLoanRepository.findByStatus(statusCode, pageable);
    }
    @Override
    public HpLoan findByHpLoanCode(String hpLoanCode) {
        return hpLoanRepository.findByHpLoanCode(hpLoanCode);
    }

    private int convertStatusToCode(String status) {
        switch (status.toLowerCase()) {
            case "under_review":
                return ConstraintEnum.UNDER_REVIEW.getCode();
            case "rejected":
                return ConstraintEnum.REJECTED.getCode();
            case "paid_off":
                return ConstraintEnum.PAID_OFF.getCode();
            case "under_schedule":
                return ConstraintEnum.UNDER_SCHEDULE.getCode();
            default:
                throw new ServiceException("Invalid status: " + status);
        }
    }
}