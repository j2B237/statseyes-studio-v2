package com.statseyes.studio.application.port;

import com.statseyes.studio.domain.model.AccountSummary;

public interface AccountSummaryPort {
    AccountSummary load(Integer accountId);
}
