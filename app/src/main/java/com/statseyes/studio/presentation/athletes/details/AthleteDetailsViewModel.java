package com.statseyes.studio.presentation.athletes.details;

import com.statseyes.studio.application.usecase.athlete.LoadAthleteDetailsUseCase;
import com.statseyes.studio.application.usecase.session.*;
import com.statseyes.studio.domain.config.ApplicationConfiguration;
import com.statseyes.studio.domain.exception.PodFileFormatException;
import com.statseyes.studio.domain.model.Athlete;
import com.statseyes.studio.domain.model.GpsPoint;
import com.statseyes.studio.domain.model.ImportedSession;
import com.statseyes.studio.domain.model.SessionComparison;
import com.statseyes.studio.infrastructure.security.SessionAdapter;
import com.statseyes.studio.presentation.concurrent.BackgroundTaskRunner;

import javafx.beans.property.*;
import java.io.InputStream;
import java.util.List;

import javafx.collections.FXCollections;
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
    private final ListProperty<GpsPoint> heatMapPoints =
            new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<List<ImportedSession>> sessionHistory =
            new SimpleObjectProperty<>(List.of());
    private final ObjectProperty<SessionComparison> comparison =
            new SimpleObjectProperty<>();

    // =======================
    // INSTANCE VARIABLES
    // =======================
    private final LoadAthleteDetailsUseCase loadAthleteDetailsUseCase;
    private final GetLatestImportedSessionUseCase getLatestImportedSessionUseCase;
    private final ImportSessionFromFileUseCase importPodFileUseCase;
    private final GetSessionHeatmapPointsUseCase sessionHeatmapPointsUseCase;
    private final ListSessionsForAthleteUseCase listSessionsForAthleteUseCase;
    private final CompareSessionsUseCase compareSessionsUseCase;
    private final SessionAdapter sessionService;
    private final BackgroundTaskRunner backgroundTaskRunner;

    // ===================
    // PUBLIC API
    // ===================

    public AthleteDetailsViewModel(
            LoadAthleteDetailsUseCase loadAthleteDetailsUseCase,
            GetLatestImportedSessionUseCase getLatestImportedSessionUseCase,
            GetSessionHeatmapPointsUseCase sessionHeatmapPointsUseCase,
            ListSessionsForAthleteUseCase listSessionsForAthleteUseCase,
            CompareSessionsUseCase compareSessionsUseCase,
            ImportSessionFromFileUseCase importPodFileUseCase,
            SessionAdapter sessionService,
            BackgroundTaskRunner backgroundTaskRunner
    ) {
        this.loadAthleteDetailsUseCase = loadAthleteDetailsUseCase;
        this.getLatestImportedSessionUseCase = getLatestImportedSessionUseCase;
        this.listSessionsForAthleteUseCase = listSessionsForAthleteUseCase;
        this.compareSessionsUseCase = compareSessionsUseCase;
        this.sessionHeatmapPointsUseCase = sessionHeatmapPointsUseCase;
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

    public void loadHeatmapPoints(Integer importedSessionId){
        backgroundTaskRunner.run(
            () -> sessionHeatmapPointsUseCase.execute(importedSessionId),
            heatMapPoints::setAll,
            error -> importStatus.set("Erreur :" + error.getMessage())
        );
    }

    public void loadSessionHistory(Integer athleteId){
        backgroundTaskRunner.run(
                () -> listSessionsForAthleteUseCase.execute(athleteId),
                sessionHistory::set,
                error -> importStatus.set("Erreur : " + error.getMessage())
        );
    }

    public void selectSession(ImportedSession session) {
        latestSession.set(session);
       /* statDistancePerMin.set(
                String.format("%.0f m/min", session.metrics().distancePerMinuteM())
        );*/
        loadHeatmapPoints(session.id());
    }

    // Calcul pur, pas d'I/O -- pas besoin de BackgroundTaskRunner
    public void compare(ImportedSession a, ImportedSession b) {
        comparison.set(compareSessionsUseCase.execute(a, b));
    }

    public void importDumpForCurrentAthlete(){
        Athlete current = athlete.get();

        if(current == null)return;

        Integer accountId = sessionService.getCurrentUser().id();
        Integer athleteId = current.id();

        importing.set(true);
        importStatus.set("Import en cours...");

        backgroundTaskRunner.run(
                () -> {
                    try(InputStream in = getClass().getResourceAsStream(
                            ApplicationConfiguration.DUMP_RESOURCE_PATH.getValue()
                    )){

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

    public ObjectProperty<Athlete> athleteProperty() { return athlete; }
    public ObjectProperty<ImportedSession> latestSessionProperty() { return latestSession; }
    public StringProperty importStatusProperty() { return importStatus; }
    public BooleanProperty importingProperty() { return importing; }
    public ListProperty<GpsPoint> heatMapPointsProperty(){return heatMapPoints;}
    public ObjectProperty<List<ImportedSession>> sessionHistoryProperty(){
        return sessionHistory;
    }
    public ObjectProperty<SessionComparison> comparisonProperty(){
        return comparison;
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
