package com.statseyes.studio.presentation.athletes.listing;

import com.statseyes.studio.application.usecase.athlete.ListAthletesUseCase;
import com.statseyes.studio.domain.model.Athlete;
import com.statseyes.studio.infrastructure.security.SessionAdapter;
import com.statseyes.studio.presentation.concurrent.BackgroundTaskRunner;

import javafx.beans.property.*;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class AthletesViewModel {

    private final ObjectProperty<List<Athlete>> athletes = new SimpleObjectProperty<>(List.of());
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
                athletes::set,
                error -> errorMessage.set(error.getMessage())
        );
    }

    public ObjectProperty<List<Athlete>> athletesProperty() { return athletes; }
    public StringProperty errorMessageProperty() { return errorMessage; }


}