package com.statseyes.studio.presentation.athletes.details;

import com.statseyes.studio.application.usecase.athlete.LoadAthleteDetailsUseCase;
import com.statseyes.studio.application.usecase.session.GetLatestImportedSessionUseCase;
import com.statseyes.studio.application.usecase.session.ImportSessionFromFileUseCase;
import com.statseyes.studio.domain.exception.PodFileFormatException;
import com.statseyes.studio.domain.model.Athlete;
import com.statseyes.studio.domain.model.ImportedSession;
import com.statseyes.studio.infrastructure.security.SessionAdapter;
import com.statseyes.studio.presentation.concurrent.BackgroundTaskRunner;

import javafx.beans.property.*;
import java.io.InputStream;

import org.springframework.stereotype.Component;

@Component
public class AthleteDetailsViewModel {

    // ======================
    // PROPERTIES
    // ======================

    private final ObjectProperty<Athlete> athlete = new SimpleObjectProperty<>();
    private final ObjectProperty<ImportedSession> latestSession = new SimpleObjectProperty<>();
    private final StringProperty importStatus = new SimpleStringProperty();
    private final BooleanProperty importing = new SimpleBooleanProperty(false);

    // =======================
    // INSTANCE VARIABLES
    // =======================
    private final LoadAthleteDetailsUseCase loadAthleteDetailsUseCase;
    private final GetLatestImportedSessionUseCase getLatestImportedSessionUseCase;
    private final ImportSessionFromFileUseCase importPodFileUseCase;
    private final SessionAdapter sessionService;
    private final BackgroundTaskRunner backgroundTaskRunner;
    private static final String DUMP_RESOURCE_PATH = "/com/statseyes/studio/data/dump.bin";

    // ===================
    // PUBLIC API
    // ===================

    public AthleteDetailsViewModel(
            LoadAthleteDetailsUseCase loadAthleteDetailsUseCase,
            GetLatestImportedSessionUseCase getLatestImportedSessionUseCase,
            ImportSessionFromFileUseCase importPodFileUseCase,
            SessionAdapter sessionService,
            BackgroundTaskRunner backgroundTaskRunner
    ) {
        this.loadAthleteDetailsUseCase = loadAthleteDetailsUseCase;
        this.getLatestImportedSessionUseCase = getLatestImportedSessionUseCase;
        this.importPodFileUseCase = importPodFileUseCase;
        this.sessionService = sessionService;
        this.backgroundTaskRunner = backgroundTaskRunner;
    }

    public void loadAthlete(Integer athleteId){
        backgroundTaskRunner.run(
                () -> loadAthleteDetailsUseCase.execute(athleteId),
                result ->{
                    athlete.set(result); loadLatestSession(athleteId);
                },
                error ->{
                    importStatus.set("Erreur : " + error.getMessage());
                }
        );
    }


    public ObjectProperty<Athlete> athleteProperty() { return athlete; }
    public ObjectProperty<ImportedSession> latestSessionProperty() { return latestSession; }
    public StringProperty importStatusProperty() { return importStatus; }
    public BooleanProperty importingProperty() { return importing; }


    public void importDumpForCurrentAthlete(){
        Athlete current = athlete.get();

        if(current == null)return;

        Integer accountId = sessionService.getCurrentUser().id();
        Integer athleteId = current.id();

        importing.set(true);
        importStatus.set("Import en cours...");

        backgroundTaskRunner.run(
                () -> {
                    try(InputStream in = getClass().getResourceAsStream(DUMP_RESOURCE_PATH)){

                        if (in == null){
                            throw new PodFileFormatException("dump.bin introuvable dans les ressources");
                        }
                        return importPodFileUseCase.execute(in, "dump.bin", accountId, athleteId);
                    }
                },
                sessions -> {
                    importing.set(false);
                    importStatus.set(sessions.size() + " session(s) importee(s)");
                    if (!sessions.isEmpty()) {
                        latestSession.set(sessions.getLast());
                    }
                },
                error -> {
                    importing.set(false);
                    importStatus.set("Erreur d'import : " + error.getMessage());
                }
        );
    }


    // ======================
    // PRIVATE API
    // ======================

    private void loadLatestSession(Integer athleteId){
        backgroundTaskRunner.run(
                () -> getLatestImportedSessionUseCase.execute(athleteId), latestSession::set,
                error -> importStatus.set("Erreur : " + error.getMessage())
        );
    }


}
