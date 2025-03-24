package com.prj.LoneHPManagement.Service.impl;

import com.prj.LoneHPManagement.model.entity.HpLoanHistory;
import com.prj.LoneHPManagement.model.entity.HpLongOverPaidHistory;
import com.prj.LoneHPManagement.model.entity.SMELongOverPaidHistory;
import com.prj.LoneHPManagement.model.entity.SMELoanHistory;
import com.prj.LoneHPManagement.model.repo.HpLoanHistoryRepository;
import com.prj.LoneHPManagement.model.repo.HpLongOverPaidHistoryRepository;
import com.prj.LoneHPManagement.model.repo.SMELongOverPaidHistoryRepository;
import com.prj.LoneHPManagement.model.repo.SMELoanHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RepaymentHistoryService {
    private final SMELoanHistoryRepository smeHistoryRepo;
    private final SMELongOverPaidHistoryRepository smelongHistoryRepo;
    private final HpLongOverPaidHistoryRepository hpLongOverPaidHistoryRepository;
    private final HpLoanHistoryRepository hpLoanHistoryRepository;

    public Page<SMELoanHistory> getSMEUnder90History(Integer loanId, Pageable pageable) {
        return smeHistoryRepo.findByLoanId(loanId, pageable);
    }

    public Page<SMELongOverPaidHistory> getSMEOver90History(Integer loanId, Pageable pageable) {
        return smelongHistoryRepo.findByLoanId(loanId, pageable);
    }

    public Page<HpLoanHistory> getHpUnder90History(Integer loanId, Pageable pageable){
        return hpLoanHistoryRepository.findByLoanId(loanId,pageable);
    }

    public Page<HpLongOverPaidHistory> getHpOver90History(Integer loanId, Pageable pageable){
        return hpLongOverPaidHistoryRepository.findByLoanId(loanId, pageable);
    }
}