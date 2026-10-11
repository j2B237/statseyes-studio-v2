package com.statseyes.studio.presentation.athletes.form;

import com.statseyes.studio.application.usecase.athlete.CreateAthleteUseCase;
import com.statseyes.studio.application.usecase.athlete.LoadAthleteFormOptionsUseCase;
import com.statseyes.studio.domain.exception.DomainValidationException;
import com.statseyes.studio.domain.model.LookupOption;
import com.statseyes.studio.domain.model.NewAthlete;
import com.statseyes.studio.infrastructure.security.SessionAdapter;
import com.statseyes.studio.presentation.concurrent.BackgroundTaskRunner;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Component;

@Component
public class AthleteFormViewModel {

    // ======================
    // PROPERTIES
    // ======================

    private final ObservableList<LookupOption> teams =
            FXCollections.observableArrayList();
    private final ObservableList<LookupOption> positions =
            FXCollections.observableArrayList();
    private final StringProperty errorMessage = new SimpleStringProperty();
    private final BooleanProperty saving = new SimpleBooleanProperty();

    // ========================
    // SPRING BEANS
    // ========================

    private final LoadAthleteFormOptionsUseCase loadOptions;
    private final BackgroundTaskRunner runner;
    private final CreateAthleteUseCase createAthlete;
    private final SessionAdapter session;

    // ========================
    // PUBLIC API
    // ========================

    public AthleteFormViewModel(
        LoadAthleteFormOptionsUseCase loadOptions,
        BackgroundTaskRunner runner,
        CreateAthleteUseCase createAthlete,
        SessionAdapter session
    ){
        this.loadOptions = loadOptions;
        this.runner = runner;
        this.createAthlete = createAthlete;
        this.session = session;
    }


    public void load(){
        errorMessage.set(null);
        Integer accountId = session.getCurrentUser().id();

        runner.run(
                () -> loadOptions.execute(accountId),
                result ->{
                    teams.setAll(result.teams());
                    positions.setAll(result.positions());
                },
                error -> errorMessage.set(
                        "Chargement impossible : " + error.getMessage()
                )
        );
    }

    public void create(AthleteFormInput in, Runnable onSuccess){
        errorMessage.set(null);

        NewAthlete athlete;
        try {
            athlete = new NewAthlete(
                    in.firstname(), in.lastname(), in.birthday(),
                    "Féminin".equals(in.gender()) ? 'F' : "Masculin".equals(in.gender()) ?
                    'M' : ' ',
                    in.team() == null ? null : in.team().id(),
                    in.position() == null ? null : in.position().id(),
                    number(in.height(), "Taille"), number(in.weight(), "Poids"),
                    integer(in.jersey()),
                    in.nationalityCode()
            );
        } catch (DomainValidationException e){
            errorMessage.set(e.getMessage());
            return;
        }

        Integer accountId = session.getCurrentUser().id();
        saving.set(true);

        runner.run(
                () -> createAthlete.execute(athlete, accountId),
                created -> {saving.set(false); onSuccess.run();},
                e -> {
                    saving.set(false);
                    errorMessage.set(
                            e instanceof DomainValidationException ? e.getMessage() :
                                    "Enregistrement impossible: " + e.getMessage());
                }
        );
    }

    public ObservableList<LookupOption> teams() { return teams; }
    public ObservableList<LookupOption> positions() { return positions; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    public BooleanProperty savingProperty() { return saving; }

    // ==============
    // PRIVATE API
    // ==============

    private Double number(String s, String label){
        if (s == null || s.isBlank()) return null;
        try { return Double.parseDouble(s.trim().replace(',', '.')); }
        catch (NumberFormatException e) {
            throw new DomainValidationException(label + " invalide.");
        }
    }

    private Integer integer(String s) {
        if (s == null || s.isBlank()) return null;
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { throw new DomainValidationException("Numéro de maillot invalide."); }
    }
}
