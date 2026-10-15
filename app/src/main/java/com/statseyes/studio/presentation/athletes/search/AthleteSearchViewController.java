package com.statseyes.studio.presentation.athletes.search;


import com.statseyes.studio.domain.model.AthleteLookupOption;
import com.statseyes.studio.domain.model.LookupFilterOption;
import com.statseyes.studio.domain.model.TransferStatusFilter;
import com.statseyes.studio.presentation.component.AthleteCard;
import com.statseyes.studio.presentation.template.TemplateViewController;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import org.springframework.stereotype.Controller;


@Controller
public class AthleteSearchViewController {

    // =================
    // FXML ENTITIES
    // =================

    @FXML private ChoiceBox<LookupFilterOption> teamFilterChoiceBox;
    @FXML private ChoiceBox<LookupFilterOption> positionFilterChoiceBox;
    @FXML private ChoiceBox<TransferStatusFilter> statusFilterChoiceBox;
    @FXML private ListView<AthleteLookupOption> athleteListView;
    @FXML private Label countLabel;
    @FXML private Label emptyLabel;
    @FXML private Label errorLabel;

    // ====================
    // INSTANCE VARIABLES
    // ====================

    private final TemplateViewController template;
    private final AthleteSearchViewModel viewModel;


    public AthleteSearchViewController(
            TemplateViewController template,
            AthleteSearchViewModel viewModel
    ) {
        this.template = template;
        this.viewModel = viewModel;
    }

    // ================
    // PUBLIC API
    // ================

    public void initialize() {

        setupFilters();
        setupResults();
        viewModel.load();
    }

    // =============
    // PRIVATE API
    // =============

    private void setupFilters(){
        teamFilterChoiceBox.setItems(
                viewModel.teamsProperty()
        );

        positionFilterChoiceBox.setItems(
                viewModel.positionsProperty()
        );

        statusFilterChoiceBox.setItems(
                FXCollections.observableArrayList(
                        TransferStatusFilter.values())
        );

        // ViewModel -> vue (état conservé d'une visite à l'autre)
        teamFilterChoiceBox.setValue(
                viewModel.teamProperty().get()
        );

        positionFilterChoiceBox.setValue(
                viewModel.positionProperty().get()
        );

        statusFilterChoiceBox.setValue(
                viewModel.statusProperty().get()
        );

        // Vue -> ViewModel. Un null transitoire (items remplacés) est ignoré.
        teamFilterChoiceBox.valueProperty().addListener(
                (o, ov, nv) -> {
                    if (nv != null) viewModel.teamProperty().set(nv);
                }
        );

        positionFilterChoiceBox.valueProperty().addListener(
                (o, ov, nv) -> {
                    if (nv != null) viewModel.positionProperty().set(nv);
                }
        );

        statusFilterChoiceBox.valueProperty().addListener(
                (o, ov, nv) -> {
                    if (nv != null) viewModel.statusProperty().set(nv);
                }
        );
    }

    private void setupResults(){

        athleteListView.setItems(viewModel.filteredAthleteProperty());
        athleteListView.setCellFactory( list -> new ListCell<>(){

            private final AthleteCard card = new AthleteCard();

            {
                card.setOnViewDetails(() -> {
                    AthleteLookupOption athlete = getItem();
                    if (athlete != null){
                        template.loadAthleteDetailsView(athlete.id());
                    }
                });
            }

            @Override
            protected void updateItem(AthleteLookupOption athlete, boolean empty){
                super.updateItem(athlete, empty);

                if(empty || athlete == null){
                    setGraphic(null);
                    return;
                }

                card.setAthleteName(athlete.firstname() + " " + athlete.lastname());
                card.setTeamName(athlete.teamName());
                card.setPositionName(athlete.positionName());
                card.setPhotoUrl(athlete.imageUrl());

                setGraphic(card);
            }
        });

        countLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            int n = viewModel.filteredAthleteProperty().size();
            return n + (n > 1 ? " résultats" : " résultat");
        }, viewModel.filteredAthleteProperty()));

        // "Aucun résultat" seulement une fois le chargement terminé
        BooleanBinding noResult = Bindings.isEmpty(viewModel.filteredAthleteProperty()).and(viewModel.loadedProperty());
        emptyLabel.visibleProperty().bind(noResult);
        emptyLabel.managedProperty().bind(noResult);

        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
        errorLabel.visibleProperty().bind(viewModel.errorMessageProperty().isNotEmpty());
        errorLabel.managedProperty().bind(viewModel.errorMessageProperty().isNotEmpty());

    }

    // ===============
    // PROTECTED API
    // ===============

    @FXML
    protected void onResetFilters() {
        teamFilterChoiceBox.setValue(AthleteSearchViewModel.ALL_TEAMS);
        positionFilterChoiceBox.setValue(AthleteSearchViewModel.ALL_POSITIONS);
        statusFilterChoiceBox.setValue(TransferStatusFilter.ALL);
    }
}
