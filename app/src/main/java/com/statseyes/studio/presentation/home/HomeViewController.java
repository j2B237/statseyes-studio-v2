package com.statseyes.studio.presentation.home;

import com.statseyes.studio.presentation.component.StatsCard;
import com.statseyes.studio.domain.model.AccountSummary;
import com.statseyes.studio.domain.model.AuthenticatedUser;
import com.statseyes.studio.infrastructure.security.SessionAdapter;
import com.statseyes.studio.presentation.navigation.ViewManager;
import com.statseyes.studio.presentation.navigation.ViewManagerAware;


import com.statseyes.studio.presentation.template.TemplateViewController;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.beans.value.ChangeListener;

import javafx.scene.input.MouseEvent;
import org.springframework.stereotype.Controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeViewController implements ViewManagerAware {

    // ===================
    // FXML ENTITIES
    // ===================

    @FXML private Label userNameLabel;
    @FXML private Label athletesLabel;
    @FXML private Label teamsLabel;
    @FXML private Label sessionsLabel;

    @FXML private ImageView clubLogoBackground;
    @FXML private StatsCard athletesCountCard;
    @FXML private StatsCard teamsCountCard;
    @FXML private StatsCard sessionsCountCard;
    @FXML private StatsCard lastImportCard;


    // =======================
    // INSTANCE VARIABLES
    // =======================

    private static final DateTimeFormatter DAY_FORMAT  = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter HOUR_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final HomeViewModel viewModel;
    private final SessionAdapter sessionService;
    private final TemplateViewController templateViewController;


    private List<Label> sidebarLabels = new ArrayList<>();

    private ViewManager viewManager;


    // Champ (et non lambda locale) : le contrôleur est un singleton Spring, donc
    // initialize() est rappelé à chaque navigation. Avec un listener stocké, on peut
    // le retirer avant de le rajouter et éviter l'accumulation de listeners vers
    // d'anciennes cartes (même piège que dans AthletesViewModel).
    private final ChangeListener<AccountSummary> summaryChangeListener =
            (obs, oldValue, newValue) ->
                    render(newValue);

    // ======================
    // PUBLIC API
    // ======================

    public HomeViewController(
            HomeViewModel viewModel,
            SessionAdapter sessionService,
            TemplateViewController templateViewController
    ){
        this.viewModel = viewModel;
        this.sessionService = sessionService;
        this.templateViewController = templateViewController;
    }

    public void initialize(){

        sidebarLabels.add(athletesLabel);
        sidebarLabels.add(teamsLabel);
        sidebarLabels.add(sessionsLabel);


        AuthenticatedUser user = sessionService.getCurrentUser();

        if(user != null){
            userNameLabel.setText(
                    user.firstname() + " " + user.lastname()
            );
        }

        athletesCountCard.setNoData("--");
        teamsCountCard.setNoData("--");
        sessionsCountCard.setNoData("--");
        lastImportCard.setNoData("--");

        bindViewModel();
        viewModel.load();
    }

    @Override
    public void setViewManager(ViewManager viewManager){
        this.viewManager = viewManager;
    }


    // ======================
    // PRIVATE API
    // =====================

    private void bindViewModel(){
        viewModel.summaryProperty().removeListener(summaryChangeListener);
        viewModel.summaryProperty().addListener(summaryChangeListener);
    }

    private void render(AccountSummary summary) {
        if (summary == null) return;

        athletesCountCard.setValue(String.valueOf(summary.athleteCount()));
        teamsCountCard.setValue(String.valueOf(summary.teamCount()));
        sessionsCountCard.setValue(String.valueOf(summary.importedSessionCount()));

        if (summary.lastImportedAt() == null) {
            lastImportCard.setNoData("Aucun");
        } else {
            lastImportCard.setValue(summary.lastImportedAt().format(DAY_FORMAT));
            lastImportCard.setUnit(summary.lastImportedAt().format(HOUR_FORMAT));
        }
    }


    // =======================
    // PROTECTED API
    // =======================

    @FXML
    private void handleSidebarClick(MouseEvent event) {
        Label clickedLabel = (Label) event.getSource();
        String selectedOption = clickedLabel.getText();

        // Logique de navigation ou d'action
        System.out.println("Option sélectionnée : " + selectedOption);
        switch (selectedOption) {
            case "Athletes":
                templateViewController.loadAthletesView();
                break;
            case "Équipes":
                templateViewController.loadTeamsView();
                break;
            case "Seances":
                templateViewController.loadSessionsView();
                break;
            default:
                break;
        }

        // Mettre à jour l'apparence de l'élément sélectionné
        //updateSelectedLabel(clickedLabel);
    }



}
