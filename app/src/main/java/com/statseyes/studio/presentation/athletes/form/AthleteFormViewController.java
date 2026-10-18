package com.statseyes.studio.presentation.athletes.form;

import com.statseyes.studio.domain.model.LookupOption;
import com.statseyes.studio.presentation.template.TemplateViewController;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.springframework.stereotype.Controller;

import java.util.*;

@Controller
public class AthleteFormViewController {

    // ==============
    // FXML ENTITIES
    // ==============

    @FXML private TextField firstnameField;
    @FXML private TextField lastnameField;
    @FXML private TextField heightField;
    @FXML private TextField weightField;
    @FXML private TextField jerseyField;
    @FXML private DatePicker birthdayPicker;
    @FXML private ChoiceBox<String> genderChoice;
    @FXML private ComboBox<LookupOption> teamCombo, positionCombo;
    @FXML private ComboBox<String> nationalityCombo;
    @FXML private Label errorLabel;
    @FXML private Button saveButton;

    private final AthleteFormViewModel viewModel;
    private final TemplateViewController template;

    private final ChangeListener<String> errorListener =
            (o, ov, nv) ->
            {
                boolean has = nv != null && !nv.isBlank();
                errorLabel.setText(has ? nv : "");
                errorLabel.setVisible(has);
                errorLabel.setManaged(has);
            };


    private final ChangeListener<Boolean> savingListener =
            (o, ov, nv) ->
                    saveButton.setDisable(nv);

    public AthleteFormViewController(
            AthleteFormViewModel viewModel,
            TemplateViewController template
    ) {
        this.viewModel = viewModel;
        this.template = template;
    }

    public void initialize() {

        genderChoice.getItems().setAll("Masculin", "Féminin");
        teamCombo.setItems(viewModel.teams());
        positionCombo.setItems(viewModel.positions());

        List<String> codes = new ArrayList<>(
                Arrays.asList(Locale.getISOCountries())
        );

        codes.sort(
                Comparator.comparing(
                        c -> new Locale("", c).getDisplayCountry(Locale.FRENCH))
        );

        nationalityCombo.setItems(FXCollections.observableArrayList(codes));
        nationalityCombo.setConverter(new StringConverter<>() {
            @Override public String toString(String c) {
                return c == null ? "" :
                        new Locale("", c).getDisplayCountry(Locale.FRENCH);
            }
            @Override public String fromString(String s) { return null; }
        });


        nationalityCombo.setValue("SN");

        viewModel.errorMessageProperty().removeListener(errorListener);
        viewModel.errorMessageProperty().addListener(errorListener);
        viewModel.savingProperty().removeListener(savingListener);
        viewModel.savingProperty().addListener(savingListener);
        errorListener.changed(null, null, null);

        viewModel.load();
    }

    @FXML protected void onSave() {
        viewModel.create(
                new AthleteFormInput(
                        firstnameField.getText(),
                        lastnameField.getText(),
                        birthdayPicker.getValue(),
                        genderChoice.getValue(),
                        teamCombo.getValue(),
                        positionCombo.getValue(),
                        heightField.getText(),
                        weightField.getText(),
                        jerseyField.getText(),
                        nationalityCombo.getValue()
                ),
                template::loadAthletesView
        );   // la liste se recharge, cache déjà vidé
    }

    @FXML protected void onCancel() { template.loadAthletesView(); }


    @FXML protected void onImageDownload(ActionEvent event) {
    }
}
