package com.statseyes.studio.presentation.dashboard;

import com.statseyes.studio.application.usecase.home.GetAccountSummaryUseCase;
import com.statseyes.studio.domain.model.AccountSummary;
import com.statseyes.studio.domain.model.AuthenticatedUser;
import com.statseyes.studio.infrastructure.security.SessionAdapter;
import com.statseyes.studio.presentation.concurrent.BackgroundTaskRunner;


import org.springframework.stereotype.Component;
import javafx.beans.property.*;

@Component
public class DashboardViewModel {

    // ========================
    // INSTANCE VARIABLES
    // ========================

    private final SimpleObjectProperty<AccountSummary> summary =
            new SimpleObjectProperty<>();

    private final SimpleStringProperty errorMessage =
            new SimpleStringProperty();
    private final BooleanProperty loading =
            new SimpleBooleanProperty(false);

    private final GetAccountSummaryUseCase accountSummaryUseCase;
    private final SessionAdapter sessionService;
    private final BackgroundTaskRunner backgroundTaskRunner;

    // =====================
    // PUBLIC API
    // =====================

    public DashboardViewModel(
            GetAccountSummaryUseCase accountSummaryUseCase,
            SessionAdapter sessionService,
            BackgroundTaskRunner backgroundTaskRunner
    ){
        this.accountSummaryUseCase = accountSummaryUseCase;
        this.sessionService = sessionService;
        this.backgroundTaskRunner = backgroundTaskRunner;
    }


    public void load(){
        // Lu sur le thread FX
        AuthenticatedUser user = sessionService.getCurrentUser();

        if(user == null){
            errorMessage.set("Aucun utilisateur connecte");
            return;
        }

        Integer accountId = user.id();

        loading.set(true);
        errorMessage.set(null);

        backgroundTaskRunner.run(
                () -> accountSummaryUseCase.execute(accountId),
                result -> {
                    summary.set(result);
                    loading.set(false);
                },
                error -> {
                    errorMessage.set(error.getMessage());
                    loading.set(false);
                }
        );
    }


    public ObjectProperty<AccountSummary> summaryProperty() { return summary; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    public BooleanProperty loadingProperty() { return loading; }

}
