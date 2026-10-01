package com.statseyes.studio.presentation.dashboard;

import com.statseyes.studio.application.usecase.club.*;
import com.statseyes.studio.application.usecase.home.GetAccountSummaryUseCase;
import com.statseyes.studio.domain.model.AccountSummary;
import com.statseyes.studio.domain.model.AuthenticatedUser;
import com.statseyes.studio.infrastructure.security.SessionAdapter;
import com.statseyes.studio.presentation.concurrent.BackgroundTaskRunner;


import javafx.scene.image.Image;
import org.springframework.stereotype.Component;
import javafx.beans.property.*;

import java.util.Objects;

@Component
public class DashboardViewModel {

    // ========================
    // INSTANCE VARIABLES
    // ========================

    private final ObjectProperty<AccountSummary> summary =
            new SimpleObjectProperty<>();
    private final StringProperty errorMessage =
            new SimpleStringProperty();
    private final BooleanProperty loading =
            new SimpleBooleanProperty(false);
    private final StringProperty athleteCount = new SimpleStringProperty();

    private final StringProperty clubLogoUrl = new SimpleStringProperty();
    private final SimpleObjectProperty<Image> clubLogo = new SimpleObjectProperty<>();
    private final StringProperty clubName = new SimpleStringProperty();

    private final GetClubLogoUseCase logoUseCase;
    private final GetClubInfoUseCase infoUseCase;
    private final GetAccountSummaryUseCase accountSummaryUseCase;
    private final SessionAdapter sessionService;
    private final BackgroundTaskRunner backgroundTaskRunner;

    // =====================
    // PUBLIC API
    // =====================

    public DashboardViewModel(
            GetClubInfoUseCase infoUseCase,
            GetClubLogoUseCase logoUseCase,
            GetAccountSummaryUseCase accountSummaryUseCase,
            SessionAdapter sessionService,
            BackgroundTaskRunner backgroundTaskRunner
    ){
        this.infoUseCase = infoUseCase;
        this.logoUseCase = logoUseCase;
        this.accountSummaryUseCase = accountSummaryUseCase;
        this.sessionService = sessionService;
        this.backgroundTaskRunner = backgroundTaskRunner;
    }

    public void loadClubLogo(){
        AuthenticatedUser user = sessionService.getCurrentUser();
        if(user == null)return;

        Integer accountId = user.id();

        backgroundTaskRunner.run(
                () -> Objects.requireNonNull(logoUseCase.execute(accountId).orElse(null)),
                url -> {
                    clubLogoUrl.set(url);
                    clubLogo.set(new Image(
                            Objects.requireNonNull(clubLogoUrl.get()),
                                    true));
                    },
                error -> {/* Pas grave, on remplace par un fond noir*/}
        );
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

        backgroundTaskRunner.run(
                () -> Objects.requireNonNull(infoUseCase.execute(accountId).orElse(null)),
                clubName::set,
                error -> clubName.set("NOM DU CLUB")
        );
    }

    public ObjectProperty<AccountSummary> summaryProperty() { return summary; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    public BooleanProperty loadingProperty() { return loading; }
    public StringProperty clubLogoUrlProperty() { return clubLogoUrl; }
    public ObjectProperty<Image> clubLogoProperty(){return clubLogo;}
    public StringProperty clubNameProperty(){return clubName;}
}
