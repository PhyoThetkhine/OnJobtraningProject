package com.prj.LoneHPManagement;
import com.prj.LoneHPManagement.Service.AutoPaymentService;
import com.prj.LoneHPManagement.Service.impl.AutoRunServiceImpl;
import com.prj.LoneHPManagement.Service.impl.SMETermCalculationServiceImpl;
import com.prj.LoneHPManagement.model.entity.*;
import com.prj.LoneHPManagement.model.repo.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AutoRunServiceImplTest {

    @Mock
    private SMETermRepository smeTermRepository;

    @Mock
    private SMETermCalculationServiceImpl smeTermCalculationService;

    @Mock
    private CIFCurrentAccountRepository cifCurrentAccountRepository;

    @Mock
    private HpTermRepository hpTermRepository;

    @Mock
    private AutoPaymentService autoPaymentService;

    @InjectMocks
    private AutoRunServiceImpl autoRunService;

    // Test cases below
    @Test
    void testDailyLateFeeCalculation_OverdueTermsProcessed() {
        // Arrange
        SMETerm overdueTerm = new SMETerm();
        overdueTerm.setStatus(7);
        List<SMETerm> overdueTerms = Collections.singletonList(overdueTerm);

        when(smeTermRepository.findByStatus(7)).thenReturn(overdueTerms);

        // Act
        autoRunService.dailyRun();

        // Assert
        verify(smeTermCalculationService).calculateLateFee(overdueTerm);
        verify(smeTermCalculationService).updateOutstandingAmount(overdueTerm);
        verify(smeTermRepository).save(overdueTerm);
    }
    @Test
    void testDailyLateFeeCalculation_GracePeriodExpired() {
        // Arrange
        SMETerm graceTerm = new SMETerm();
        graceTerm.setStatus(ConstraintEnum.GRACE_PERIOD.getCode()); // e.g., 12
        SMELoan smeLoan = new SMELoan();
        smeLoan.setGracePeriod(5);
        graceTerm.setSmeLoan(smeLoan);
        graceTerm.setDueDate(java.sql.Date.valueOf(LocalDate.now().minusDays(6)));

        // Stub both calls
        when(smeTermRepository.findByStatus(7)).thenReturn(Collections.emptyList());
        when(smeTermRepository.findByStatus(ConstraintEnum.GRACE_PERIOD.getCode()))
                .thenReturn(Collections.singletonList(graceTerm));

        // Act
        autoRunService.dailyRun();

        // Assert
        assertEquals(ConstraintEnum.PAST_DUE.getCode(), graceTerm.getStatus());
        verify(smeTermRepository).save(graceTerm);
    }
    @Test
    void testDailyLateFeeCalculation_ProcessAllAccounts() {
        // Arrange
        CIFCurrentAccount account = new CIFCurrentAccount();
        account.setBalance(BigDecimal.valueOf(1000));
        List<CIFCurrentAccount> accounts = Collections.singletonList(account);

        when(cifCurrentAccountRepository.findAll()).thenReturn(accounts);

        // Act
        autoRunService.dailyRun();

        // Assert
        verify(cifCurrentAccountRepository).save(account);
    }
    @Test
    void testProcessSmePayments() {
        // Arrange
        CIFCurrentAccount account = new CIFCurrentAccount();
        account.setBalance(BigDecimal.valueOf(1000));

        SMETerm term1 = new SMETerm();
        term1.setStatus(ConstraintEnum.PAST_DUE.getCode());
        SMETerm term2 = new SMETerm();
        term2.setStatus(ConstraintEnum.GRACE_PERIOD.getCode());

        when(smeTermRepository.findByStatus(ConstraintEnum.PAST_DUE.getCode()))
                .thenReturn(Collections.singletonList(term1));
        when(smeTermRepository.findByStatus(ConstraintEnum.GRACE_PERIOD.getCode()))
                .thenReturn(Collections.singletonList(term2));
        when(autoPaymentService.SmeProcessTerm(any(), any())).thenReturn(true);

        // Act
        boolean hasBalance = autoRunService.processSmePayments(account);

        // Assert
        assertTrue(hasBalance);
        verify(autoPaymentService, times(2)).SmeProcessTerm(any(), eq(account));
    }
    @Test
    void testProcessHpPayments() {
        // Arrange
        CIFCurrentAccount account = new CIFCurrentAccount();
        account.setBalance(BigDecimal.valueOf(500));

        HpTerm term1 = new HpTerm();
        term1.setStatus(ConstraintEnum.PAST_DUE.getCode());
        HpTerm term2 = new HpTerm();
        term2.setStatus(ConstraintEnum.GRACE_PERIOD.getCode());

        when(hpTermRepository.findByStatus(ConstraintEnum.PAST_DUE.getCode()))
                .thenReturn(Collections.singletonList(term1));
        when(hpTermRepository.findByStatus(ConstraintEnum.GRACE_PERIOD.getCode()))
                .thenReturn(Collections.singletonList(term2));
        when(autoPaymentService.HpProcessTerm(any(), any())).thenReturn(true);

        // Act
        boolean hasBalance = autoRunService.processHpPayments(account);

        // Assert
        assertTrue(hasBalance);
        verify(autoPaymentService, times(2)).HpProcessTerm(any(), eq(account));
    }
}
