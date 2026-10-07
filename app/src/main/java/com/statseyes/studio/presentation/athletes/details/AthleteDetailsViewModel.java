package com.statseyes.studio.presentation.athletes.details;

import com.statseyes.studio.application.usecase.athlete.LoadAthleteDetailsUseCase;
import com.statseyes.studio.application.usecase.session.*;
import com.statseyes.studio.domain.config.ApplicationConfiguration;
import com.statseyes.studio.domain.exception.PodFileFormatException;
import com.statseyes.studio.domain.model.*;
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

    private final ObjectProperty<TeamAverageMetrics> teamAverage = new SimpleObjectProperty<>();
    private final GetTeamAverageMetricsUseCase getTeamAverageMetricsUseCase;

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
            GetTeamAverageMetricsUseCase getTeamAverageMetricsUseCase, LoadAthleteDetailsUseCase loadAthleteDetailsUseCase,
            GetLatestImportedSessionUseCase getLatestImportedSessionUseCase,
            GetSessionHeatmapPointsUseCase sessionHeatmapPointsUseCase,
            ListSessionsForAthleteUseCase listSessionsForAthleteUseCase,
            CompareSessionsUseCase compareSessionsUseCase,
            ImportSessionFromFileUseCase importPodFileUseCase,
            SessionAdapter sessionService,
            BackgroundTaskRunner backgroundTaskRunner
    ) {
        this.getTeamAverageMetricsUseCase = getTeamAverageMetricsUseCase;
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
                forceset(athlete, result);
                loadLatestSession(athleteId);
                loadAverageTeam(result.teamId());
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
                result -> forceset(sessionHistory, result),
                error -> importStatus.set("Erreur : " + error.getMessage())
        );
    }
    public void loadAverageTeam(Integer teamId){
        if(teamId == null)return;
        backgroundTaskRunner.run(
                () -> getTeamAverageMetricsUseCase.execute(teamId),
                result -> forceset(teamAverage, result),
                error -> importStatus.set("Erreur : " + error.getMessage())
        );
    }

    public void selectSession(ImportedSession session) {
        forceset(latestSession,session);
        loadHeatmapPoints(session.id());
    }

    // Calcul pur, pas d'I/O -- pas besoin de BackgroundTaskRunner
    public void compare(ImportedSession a, ImportedSession b) {
        forceset(comparison, compareSessionsUseCase.execute(a, b));
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
    public ObjectProperty<TeamAverageMetrics> teamAverageProperty() { return teamAverage; }

    // ======================
    // PRIVATE API
    // ======================

    private void loadLatestSession(Integer athleteId){
        backgroundTaskRunner.run(
            () -> getLatestImportedSessionUseCase.execute(athleteId),
                result -> forceset(latestSession, result),
                error -> importStatus.set("Erreur : " + error.getMessage())
        );
    }

    /**
     * <p>Athlete, ImportedSession, TeamAverageMetrics sont des record
     * leur equals() généré automatiquement compare le contenu, pas la référence.
     * Or ObjectPropertyBase.set(nouvelleValeur) dans JavaFX contient une optimisation interne :
     * si la nouvelle valeur est equals() à l'ancienne, aucun listener n'est notifié,
     * JavaFX considère qu'il n'y a "rien à changer".</p>
     * <p>Comme athlete contient déjà exactement les mêmes valeurs (même nom, même date de naissance, etc.),
     * JavaFX juge que rien n'a changé et ne déclenche jamais athleteListener — donc
     * <ul>
     *      <li>renderAthlete(nv)</li>
     *      <li>renderProfile(nv)</li>
     * </ul>
     * ne s'exécutent jamais. Pareil pour latestSession, teamAverage, sessionHistory (un ObjectProperty<List<...>>,
     * où List.equals() est, lui aussi, structurel).
     * Et comme initialize() appelle resetCards()/resetProfile() en tout début (pour afficher un état "chargement..." propre),
     * l'écran reste bloqué sur cet état de reset — précisément les tirets "--"/"—":
     * athlete valait null au départ, et null.equals(...) ne s'applique jamais à null lui-même — JavaFX traite
     * null → valeur comme un changement réel, le listener se déclenche normalement.
     * Le bug n'apparaît que lors d'une revisite avec des données inchangées.</p>
     * La solution forceset, une methode génère qui accepte n'importe quelle propriété dérivée de ObjectProperty
     * et une valeur de type inconnu.
     * forceset permet de passer par null entre les deux. Ce qui force JavaFX à voir deux changements réels
     * (valeur → null est toujours différent, null → nouvelle valeur aussi), donc les deux appels de set déclenchent
     * bien leurs listeners.
     * @param property :
     * @param value
     * @param <T>
     */
    private <T> void forceset(ObjectProperty<T> property, T value){
        property.set(null);
        property.set(value);
    }

}
