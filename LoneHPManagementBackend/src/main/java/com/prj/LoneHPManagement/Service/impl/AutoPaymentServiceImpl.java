package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.Service.AutoPaymentService;
import com.prj.LoneHPManagement.Service.HpTermCalculationService;
import com.prj.LoneHPManagement.Service.SMEAutoPaymentService;
import com.prj.LoneHPManagement.Service.SMETermCalculationService;
import com.prj.LoneHPManagement.model.entity.*;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import com.prj.LoneHPManagement.model.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AutoPaymentServiceImpl implements AutoPaymentService {
    @Autowired
    private CIFCurrentAccountRepository cifCurrentAccountRepository;

    @Autowired
    private SMETermRepository smeTermRepository;

    @Autowired
    private SMELoanHistoryRepository smeLoanHistoryRepository;

    @Autowired
    private HpLoanHistoryRepository hpLoanHistoryRepository;
    @Autowired
    private SMETermCalculationService smeTermCalculationService;

    @Autowired
    private HpTermCalculationService hpTermCalculationService;
    @Autowired
    private SMEAutoPaymentService smeAutoPaymentService;


    @Autowired
    private SMELoanRepository smeLoanRepository;

    @Autowired
    private HpLoanRepository hpLoanRepository;

    @Autowired
    private HpTermRepository hpTermRepository;

    @Override
    public void processTransaction(Transaction transaction) {

    }

    @Override
    public boolean SmeProcessTerm(SMETerm term, CIFCurrentAccount cifAccount) {
        return false;
    }

    @Override
    public boolean HpProcessTerm(HpTerm term, CIFCurrentAccount cifAccount) {
        return false;
    }

    @Override
    public void processLateLoan(SMELoan loan, List<SMETerm> allTerms, CIFCurrentAccount cifAccount) {

    }
}