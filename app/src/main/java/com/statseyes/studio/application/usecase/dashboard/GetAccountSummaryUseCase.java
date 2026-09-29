package com.statseyes.studio.application.usecase.dashboard;

import com.statseyes.studio.application.port.AccountSummaryPort;
import com.statseyes.studio.domain.model.AccountSummary;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class GetAccountSummaryUseCase {

    // =====================
    // INSTANCE VARIABLES
    // =====================

    private final AccountSummaryPort accountSummaryPort;


    // =======================
    // PUBLIC API
    // ======================

    public GetAccountSummaryUseCase(AccountSummaryPort accountSummaryPort){
        this.accountSummaryPort = accountSummaryPort;
    }

    public AccountSummary execute(Integer accountId){
        if(accountId == null){
            throw new IllegalArgumentException("accountId require.");
        }

       return accountSummaryPort.load(accountId);
    }


}
