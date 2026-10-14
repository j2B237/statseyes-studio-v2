package com.statseyes.studio.presentation.athletes.search;

import com.statseyes.studio.domain.model.AthleteLookupOption;
import com.statseyes.studio.domain.model.AthleteSearchCriteria;
import com.statseyes.studio.domain.model.LookupFilterOption;
import com.statseyes.studio.domain.model.TransferStatusFilter;
import com.statseyes.studio.infrastructure.security.SessionAdapter;
import com.statseyes.studio.presentation.concurrent.BackgroundTaskRunner;
import com.statseyes.studio.application.usecase.athlete.SearchAthleteUseCase;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AthleteSearchViewModel {

    public static final LookupFilterOption ALL_TEAMS =
            new LookupFilterOption(null, "Toutes les équipes");
    public static final LookupFilterOption ALL_POSITIONS =
            new LookupFilterOption(null, "Tous les postes");

    // ===============
    // PROPERTIES
    // ===============

    private final ObservableList<AthleteLookupOption> athletes =
            FXCollections.observableArrayList();
    private final FilteredList<AthleteLookupOption> filteredAthletes =
            new FilteredList<>(athletes);
    private final ObservableList<LookupFilterOption> teams =
            FXCollections.observableArrayList(ALL_TEAMS);
    private final ObservableList<LookupFilterOption> positions =
            FXCollections.observableArrayList(ALL_POSITIONS);

    private final StringProperty query = new SimpleStringProperty("");
    private final ObjectProperty<LookupFilterOption> team =
            new SimpleObjectProperty<>();
    private final ObjectProperty<LookupFilterOption> position =
            new SimpleObjectProperty<>();

    private final ObjectProperty<TransferStatusFilter> status =
            new SimpleObjectProperty<>();
    private final BooleanProperty loaded = new SimpleBooleanProperty(false);
    private final StringProperty errorMessage = new SimpleStringProperty();

    // ===================
    // INSTANCE VARIABLES
    // ===================

    private final BackgroundTaskRunner runner;
    private final SearchAthleteUseCase searchUseCase;
    private final SessionAdapter session;

    public AthleteSearchViewModel(
            BackgroundTaskRunner runner,
            SearchAthleteUseCase searchUseCase,
            SessionAdapter session
    ){
        this.runner = runner;
        this.searchUseCase = searchUseCase;
        this.session = session;

        // Branchés une seule fois : ce ViewModel est un singleton, son constructeur ne s'exécute qu'une fois.
        query.addListener(
                (o, ov, nv) -> applyFilter());
        team.addListener(
                (o, ov, nv) -> applyFilter());
        position.addListener(
                (o, ov, nv) -> applyFilter());
        status.addListener(
                (o, ov, nv) -> applyFilter());
        applyFilter();
    }

    public void load(){

        Integer accountId = session.getCurrentUser().id();

        runner.run(
                () -> searchUseCase.execute(accountId),
                result -> {
                    replaceIfChanged(athletes, result.athletes());
                    replaceIfChanged(teams, withFirst(ALL_TEAMS, result.teams()));
                    replaceIfChanged(positions, withFirst(ALL_POSITIONS, result.positions()));
                    loaded.set(true);
                },
                error -> {
                    errorMessage.set("Recherche indisponible: " + error.getMessage());
                }
        );
    }

    public void resetFilters(){
        team.set(ALL_TEAMS);
        position.set(ALL_POSITIONS);
        status.set(TransferStatusFilter.ALL);
    }
    // ===================
    // GETTERS
    // ===================

    public FilteredList<AthleteLookupOption> filteredAthleteProperty() { return filteredAthletes; }
    public ObservableList<LookupFilterOption> teamsProperty() { return teams; }
    public ObservableList<LookupFilterOption> positionsProperty() { return positions; }
    public StringProperty queryProperty() { return query; }
    public ObjectProperty<LookupFilterOption> teamProperty() { return team; }
    public ObjectProperty<LookupFilterOption> positionProperty() { return position; }
    public ObjectProperty<TransferStatusFilter> statusProperty() { return status; }
    public BooleanProperty loadedProperty() { return loaded; }
    public StringProperty errorMessageProperty() { return errorMessage; }

    // =============
    // PRIVATE API
    // =============

    private void applyFilter() {

        LookupFilterOption t = team.get();
        LookupFilterOption p = position.get();

        AthleteSearchCriteria criteria = new AthleteSearchCriteria(
                query.get(),
                t == null ? null : t.id(),     // null = pas de filtre
                p == null ? null : p.id(),
                status.get()
        );
        filteredAthletes.setPredicate(criteria::matches);
    }

    // Évite un événement (et une perte de sélection dans les ChoiceBox) quand rien n'a changé.
    private static <T> void replaceIfChanged(ObservableList<T> target, List<T> items) {
        if (!target.equals(items)) target.setAll(items);
    }

    private static <T> List<T> withFirst(T first, List<T> rest) {
        List<T> all = new ArrayList<>(rest.size() + 1);
        all.add(first);
        all.addAll(rest);
        return all;
    }
}
