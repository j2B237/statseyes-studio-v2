package com.statseyes.studio.presentation.athletes.listing;

import com.statseyes.studio.domain.model.Athlete;
import com.statseyes.studio.infrastructure.cache.CacheType;
import com.statseyes.studio.presentation.component.AthleteCard;
import com.statseyes.studio.presentation.template.TemplateViewController;
import com.statseyes.studio.infrastructure.cache.CacheStatsManager;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;


import org.springframework.stereotype.Controller;

@Controller
public class AthletesViewController {

    @FXML private ListView<Athlete> athleteListView;
    @FXML private Label errorLabel;

    private final AthletesViewModel viewModel;
    private final TemplateViewController templateViewController;
    private final CacheStatsManager cacheStatsManager;

    private final ChangeListener<String> errorListener = (obs, oldValue, newValue) -> {
        boolean hasError = newValue != null && !newValue.isBlank();
        errorLabel.setText(newValue);
        errorLabel.setVisible(hasError);
        errorLabel.setManaged(hasError);
    };


    public AthletesViewController(
            AthletesViewModel viewModel,
            TemplateViewController templateViewController,
            CacheStatsManager cacheStatsManager
    ) {
        this.viewModel = viewModel;
        this.templateViewController = templateViewController;
        this.cacheStatsManager = cacheStatsManager;
    }

    public void initialize() {

        athleteListView.setCellFactory(list -> new ListCell<>() {
            private final AthleteCard card = new AthleteCard();

            {
                card.setOnViewDetails(() -> {
                    Athlete athlete = getItem();
                    if (athlete != null) {
                        templateViewController.loadAthleteDetailsView(athlete.id());
                    }
                });
            }

            @Override
            protected void updateItem(Athlete athlete, boolean empty) {
                super.updateItem(athlete, empty);
                if (empty || athlete == null) { setGraphic(null); return; }

                card.setAthleteName(athlete.firstname() + " " + athlete.lastname());
                card.setTeamName(athlete.teamName());
                card.setPositionName(athlete.positionName());
                card.setPhotoUrl(athlete.imageUrl());

                setGraphic(card);
            }
        });

        bindViewModel();
        viewModel.load();

        cacheStatsManager.printStats(CacheType.ATHLETES);
    }

    @FXML
    protected void onAddAthlete() {
        // TODO : formulaire d'ajout, pas encore construit
    }

    // ===============
    // PRIVATE API
    // ===============

    private void bindViewModel(){
        
        athleteListView.setItems(viewModel.athletesProperty());
        viewModel.errorMessageProperty().removeListener(errorListener);
        viewModel.errorMessageProperty().addListener(errorListener);
    }
    
}