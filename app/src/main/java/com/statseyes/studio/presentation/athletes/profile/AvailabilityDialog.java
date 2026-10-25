package com.statseyes.studio.presentation.athletes.profile;

import com.statseyes.studio.domain.model.Availability;
import com.statseyes.studio.domain.model.AvailabilityStatus;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.net.URL;
import java.time.LocalDate;

public final class AvailabilityDialog {

    private static final String CSS = "/com/statseyes/studio/static/css/athlete/athlete-details.css";

    private AvailabilityDialog() {}

    public static void show(AthleteProfileViewModel vm, String athleteName, Window owner) {
        Availability current = vm.availabilityProperty().get();

        ToggleGroup group = new ToggleGroup();
        RadioButton ok  = radio("Disponible",   AvailabilityStatus.AVAILABLE,   "status-available-text",   group);
        RadioButton inj = radio("Blessé",       AvailabilityStatus.INJURED,     "status-injured-text",     group);
        RadioButton una = radio("Indisponible", AvailabilityStatus.UNAVAILABLE, "status-unavailable-text", group);
        for (Toggle t : group.getToggles()) {
            if (t.getUserData() == current.status()) t.setSelected(true);
        }

        DatePicker since = new DatePicker(current.since() != null ? current.since() : LocalDate.now());
        DatePicker back  = new DatePicker(current.expectedReturn());
        TextField reason = new TextField(current.reason() == null ? "" : current.reason());

        Label hint = new Label("Quand la date de retour arrive, l'athlète repasse automatiquement en « Disponible ».");
        hint.getStyleClass().add("dialog-hint");
        hint.setWrapText(true);
        Label error = new Label();
        error.getStyleClass().add("form-error");

        VBox sinceBox = new VBox(4, new Label("Depuis le"), since);
        VBox backBox  = new VBox(4, new Label("Retour prévu le"), back);
        HBox dates = new HBox(12, sinceBox, backBox);
        HBox.setHgrow(sinceBox, Priority.ALWAYS);
        HBox.setHgrow(backBox, Priority.ALWAYS);

        // Pas de dates ni de motif quand l'athlète est disponible
        dates.disableProperty().bind(ok.selectedProperty());
        reason.disableProperty().bind(ok.selectedProperty());

        Button cancel = new Button("Annuler");
        cancel.getStyleClass().add("secondary-button");
        Button save = new Button("Enregistrer");
        save.getStyleClass().add("primary-button");
        HBox actions = new HBox(10, cancel, save);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Label title = new Label("Disponibilité de " + athleteName);
        title.getStyleClass().add("dialog-title");

        VBox root = new VBox(10, title, ok, inj, una, dates,
                new Label("Motif (optionnel)"), reason, hint, error, actions);
        root.getStyleClass().add("availability-dialog");
        root.setPadding(new Insets(22));

        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Disponibilité");
        Scene scene = new Scene(root, 520, -1);
        URL css = AvailabilityDialog.class.getResource(CSS);
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        stage.setScene(scene);

        cancel.setOnAction(e -> stage.close());
        save.setOnAction(e -> {
            AvailabilityStatus status = (AvailabilityStatus) group.getSelectedToggle().getUserData();
            if (status != AvailabilityStatus.AVAILABLE
                    && back.getValue() != null && since.getValue() != null
                    && back.getValue().isBefore(since.getValue())) {
                error.setText("La date de retour doit être après la date de début.");
                return;
            }
            vm.setAvailability(status == AvailabilityStatus.AVAILABLE
                    ? Availability.available()
                    : new Availability(status, reason.getText().trim(), since.getValue(), back.getValue()));
            stage.close();
        });

        stage.showAndWait();
    }

    private static RadioButton radio(String text, AvailabilityStatus s, String style, ToggleGroup g) {
        RadioButton r = new RadioButton(text);
        r.setUserData(s);
        r.setToggleGroup(g);
        r.getStyleClass().addAll("availability-option", style);
        return r;
    }
}