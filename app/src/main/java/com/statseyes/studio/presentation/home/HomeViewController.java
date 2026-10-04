package com.statseyes.studio.presentation.home;

import com.statseyes.studio.domain.config.ApplicationConfiguration;
import com.statseyes.studio.infrastructure.security.SessionAdapter;
import com.statseyes.studio.presentation.navigation.ViewManager;
import com.statseyes.studio.presentation.navigation.ViewManagerAware;
import com.statseyes.studio.presentation.template.TemplateViewController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import org.springframework.stereotype.Controller;

@Controller
public class HomeViewController implements ViewManagerAware {

    @FXML private Label userNameLabel;
    @FXML private Label athletesLabel;
    @FXML private Label teamsLabel;
    @FXML private Label sessionsLabel;

    private final SessionAdapter sessionAdapter;
    private final TemplateViewController templateViewController;
    private final HomeViewModel viewModel;

    private ViewManager viewManager;

    public HomeViewController(
            HomeViewModel viewModel,
            SessionAdapter sessionAdapter,
            TemplateViewController templateViewController
    ) {
        this.viewModel = viewModel;
        this.sessionAdapter = sessionAdapter;
        this.templateViewController = templateViewController;
    }

    @Override
    public void setViewManager(ViewManager viewManager) { this.viewManager = viewManager; }

    public void initialize() {
        var user = sessionAdapter.getCurrentUser();
        if (user != null) {
            userNameLabel.setText(user.firstname()+ " " + user.lastname());
        }
    }

    // ================
    // PRIVATE API
    // ================

    private void bindViewModel(){

    }


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