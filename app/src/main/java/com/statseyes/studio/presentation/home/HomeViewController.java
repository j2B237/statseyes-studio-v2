package com.statseyes.studio.presentation.home;

import com.statseyes.studio.domain.config.ApplicationConfiguration;
import com.statseyes.studio.infrastructure.security.SessionAdapter;
import com.statseyes.studio.presentation.athletes.search.AthleteSearchViewModel;
import com.statseyes.studio.presentation.navigation.ViewManager;
import com.statseyes.studio.presentation.navigation.ViewManagerAware;
import com.statseyes.studio.presentation.template.TemplateViewController;

import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import javafx.util.Duration;
import org.springframework.stereotype.Controller;

@Controller
public class HomeViewController implements ViewManagerAware {

    // ==============
    // FXML ENTITIES
    // ==============

    @FXML private Label userNameLabel;
    @FXML private Label athletesLabel;
    @FXML private Label teamsLabel;
    @FXML private Label sessionsLabel;
    @FXML private TextField searchTextfield;

    // ===================
    // INSTANCE VARIABLES
    // ==================

    private final PauseTransition searchDebounce = new PauseTransition(Duration.millis(250));

    private final SessionAdapter sessionAdapter;
    private final TemplateViewController templateViewController;
    private final HomeViewModel viewModel;
    private final AthleteSearchViewModel searchViewModel;
    private ViewManager viewManager;

    public HomeViewController(
            HomeViewModel viewModel,
            SessionAdapter sessionAdapter,
            TemplateViewController templateViewController,
            AthleteSearchViewModel searchViewModel
    ) {
        this.viewModel = viewModel;
        this.sessionAdapter = sessionAdapter;
        this.templateViewController = templateViewController;
        this.searchViewModel = searchViewModel;
    }

    // ================
    // PUBLIC API
    // ================

    @Override
    public void setViewManager(ViewManager viewManager) { this.viewManager = viewManager; }

    public void initialize() {
        var user = sessionAdapter.getCurrentUser();
        if (user != null) {
            userNameLabel.setText(user.firstname()+ " " + user.lastname());
        }

        searchDebounce.setOnFinished(e -> runSearch());
        searchTextfield.textProperty().addListener((o, ov, nv) -> searchDebounce.playFromStart());
        searchTextfield.setOnAction(e -> { searchDebounce.stop(); runSearch(); });
    }

    private void runSearch() {
        String text = searchTextfield.getText() == null ? "" : searchTextfield.getText().trim();
        searchViewModel.queryProperty().set(text);
        if (!text.isEmpty() && !templateViewController.isShowingSearch()) {
            templateViewController.loadAthleteSearchView();
        }
    }

    // ================
    // PRIVATE API
    // ================



    // ================
    // PROTECTED API
    // ================
    @FXML
    protected void onDashboardClicked() {
        templateViewController.loadDashboardView();
    }

    @FXML
    protected void handleSidebarClick(MouseEvent event) {
        Label clicked = (Label) event.getSource();
        if (clicked == athletesLabel)      templateViewController.loadAthletesView();
        else if (clicked == teamsLabel)    templateViewController.loadTeamsView();
        else if (clicked == sessionsLabel) templateViewController.loadSessionsView();
    }

    @FXML
    protected void onLogout() {
        sessionAdapter.clear();   // confirmez le nom exact de la methode sur votre SessionAdapter
        viewManager.navigateTo(ApplicationConfiguration.LOGIN_PATH.getValue());
    }
}