package com.statseyes.studio.presentation.athletes.importing;

import com.statseyes.studio.domain.model.ImportedSession;
import com.statseyes.studio.domain.model.PodDevice;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.function.Consumer;

@Component
public class PodImportDialog {

    // ===================
    // INSTANCE VARIABLES
    // ===================

    private static final String CSS = "/com/statseyes/studio/static/css/athlete/athlete-details.css";
    private final PodImportViewModel viewModel;

    public PodImportDialog(PodImportViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void show(
        Window owner,
        Integer athleteId,
        Consumer<ImportedSession> onImported
    ) {

        viewModel.reset();

        Stage stage = new Stage();
        stage.initOwner(owner);

        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Importer depuis le pod");

        Label title = new Label("Importer la dernière session");
        title.getStyleClass().add("athlete-name");
        Label hint = new Label("Branchez le pod en USB (il ne doit pas être en train d'enregistrer), puis sélectionnez-le.");
        hint.getStyleClass().add("athlete-subtitle");
        hint.setWrapText(true);

        ListView<PodDevice> deviceList = new ListView<>(viewModel.devices());
        deviceList.setPlaceholder(new Label("Aucun périphérique détecté"));
        deviceList.setPrefHeight(170);
        deviceList.disableProperty().bind(viewModel.importingProperty());

        CheckBox showAll = new CheckBox("Afficher tous les ports série");
        showAll.selectedProperty().addListener((o, ov, nv) -> {
            viewModel.showAllPortsProperty().set(nv);
            viewModel.refreshDevices();
        });

        Button refresh = new Button("Actualiser");
        refresh.getStyleClass().add("secondary-button");
        refresh.setOnAction(e -> viewModel.refreshDevices());

        showAll.disableProperty().bind(viewModel.importingProperty());
        refresh.disableProperty().bind(viewModel.importingProperty());

        HBox options = new HBox(16, showAll, new Region(), refresh);
        HBox.setHgrow(options.getChildren().get(1), Priority.ALWAYS);
        options.setAlignment(Pos.CENTER_LEFT);

        Label status = new Label();
        status.setWrapText(true);
        status.textProperty().bind(viewModel.statusProperty());

        ProgressBar bar = new ProgressBar();
        bar.setMaxWidth(Double.MAX_VALUE);

        bar.progressProperty().bind(viewModel.progressProperty());
        bar.visibleProperty().bind(viewModel.importingProperty());
        bar.managedProperty().bind(viewModel.importingProperty());

        Label error = new Label();
        error.setWrapText(true);
        error.getStyleClass().add("form-error");

        error.textProperty().bind(viewModel.errorMessageProperty());
        error.visibleProperty().bind(viewModel.errorMessageProperty().isNotEmpty());
        error.managedProperty().bind(viewModel.errorMessageProperty().isNotEmpty());

        Button importButton = new Button("Importer");
        importButton.getStyleClass().add("primary-button");

        importButton.setDefaultButton(true);
        importButton.disableProperty().bind(
                deviceList.getSelectionModel()
                        .selectedItemProperty()
                        .isNull()
                        .or(viewModel.importingProperty())
        );

        importButton.setOnAction(e -> viewModel.startImport(
                deviceList.getSelectionModel().getSelectedItem(),
                athleteId,
                imported -> {
                    onImported.accept(imported);
                    stage.close();
                })
        );

        Button cancel = new Button("Annuler");
        cancel.getStyleClass().add("secondary-button");
        cancel.setOnAction(e -> stage.close());   // la fermeture annule le transfert en cours

        HBox buttons = new HBox(12, cancel, importButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        // Un seul pod détecté : on le présélectionne
        ListChangeListener<PodDevice> autoSelect = c -> {
            if (viewModel.devices().size() == 1) deviceList.getSelectionModel().selectFirst();
        };

        viewModel.devices().addListener(autoSelect);
        stage.setOnHidden(e -> {
            viewModel.devices().removeListener(autoSelect);
            viewModel.cancel();
        });

        VBox root = new VBox(14, title, hint, deviceList, options, status, bar, error, buttons);
        root.getStyleClass().add("athlete-details-root");
        root.setPadding(new Insets(24));
        root.setPrefWidth(520);

        Scene scene = new Scene(root);
        URL css = getClass().getResource(CSS);

        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        stage.setScene(scene);

        viewModel.refreshDevices();
        stage.show();
    }
}
