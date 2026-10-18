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
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.File;

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
    private final StringProperty imageValue = new SimpleStringProperty();

    // ======================
    // INSTANCE VARIABLES
    // ======================

    @Setter
    private Window window;

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
                created -> {
                    saving.set(false);
                    // TODO: ICI APPELLER SaveAthleteUseCase pour enregistrer ImageUrl
                    // pour l'athlete cree avant de charger la vue athleteDetails

                    onSuccess.run();},
                e -> {
                    saving.set(false);
                    errorMessage.set(
                            e instanceof DomainValidationException ? e.getMessage() :
                                    "Enregistrement impossible: " + e.getMessage());
                }
        );
    }
    public void openDownloadDialog(){

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );

        File file = fileChooser.showOpenDialog(window);

        if (file != null) {
            try {
                /*Integer clubId = (clubToBeCached != null && clubToBeCached.getId() != null)
                        ? clubToBeCached.getId()
                        : 0;

                String filename = logoUploaderService.saveLogo(file.toPath(), clubId);
                Image image = new Image(logoUploaderService.getLogoUrl(filename));
                imageView.set(image);

                if (clubToBeCached != null) {
                    clubToBeCached.setLogo_url(filename);
                }*/
                imageValueProperty().set(file.toString());
            } catch (Exception e) {
                throw new RuntimeException("Erreur lors du chargement du logo", e);
            }
        }
    }

    public ObservableList<LookupOption> teams() { return teams; }
    public ObservableList<LookupOption> positions() { return positions; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    public BooleanProperty savingProperty() { return saving; }
    public StringProperty imageValueProperty(){return imageValue;}

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
