package com.statseyes.studio.presentation.athletes.listing;

import com.statseyes.studio.application.usecase.athlete.ListAthletesUseCase;
import com.statseyes.studio.domain.model.Athlete;
import com.statseyes.studio.infrastructure.security.SessionAdapter;
import com.statseyes.studio.presentation.concurrent.BackgroundTaskRunner;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class AthletesViewModel {

    private final ObservableList<Athlete> athletes =
            FXCollections.observableArrayList();
    private final StringProperty errorMessage = new SimpleStringProperty();

    private final ListAthletesUseCase listAthletesUseCase;
    private final SessionAdapter sessionAdapter;
    private final BackgroundTaskRunner backgroundTaskRunner;

    public AthletesViewModel(
            ListAthletesUseCase listAthletesUseCase,
            SessionAdapter sessionAdapter,
            BackgroundTaskRunner backgroundTaskRunner
    ) {
        this.listAthletesUseCase = listAthletesUseCase;
        this.sessionAdapter = sessionAdapter;
        this.backgroundTaskRunner = backgroundTaskRunner;
    }

    public void load() {
        var user = sessionAdapter.getCurrentUser();
        if (user == null) { errorMessage.set("Aucun utilisateur connecte"); return; }

        Integer accountId = user.id();

        backgroundTaskRunner.run(
                () -> listAthletesUseCase.execute(accountId),
                athletes::setAll,
                error -> errorMessage.set(error.getMessage())
        );
    }

    public ObservableList<Athlete> athletesProperty() { return athletes; }
    public StringProperty errorMessageProperty() { return errorMessage; }


}