package com.statseyes.studio.presentation.home;

import com.statseyes.studio.infrastructure.security.SessionAdapter;
import com.statseyes.studio.presentation.concurrent.BackgroundTaskRunner;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class HomeViewModel {

    // =======================
    // PROPERTIES
    // =======================

    private final StringProperty searchAthlete = new SimpleStringProperty();

    // ========================
    // INSTANCE VARIABLES
    // ========================
    private final SessionAdapter sessionService;
    private final BackgroundTaskRunner backgroundTaskRunner;

    // =====================
    // PUBLIC API
    // =====================

    public HomeViewModel(
            SessionAdapter sessionService,
            BackgroundTaskRunner backgroundTaskRunner
    ){
        this.sessionService = sessionService;
        this.backgroundTaskRunner = backgroundTaskRunner;
    }

    public StringProperty searchAthleteProperty(){
        return searchAthlete;
    }

}
