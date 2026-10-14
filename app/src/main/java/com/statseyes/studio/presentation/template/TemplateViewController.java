package com.statseyes.studio.presentation.template;

import com.statseyes.studio.domain.config.ApplicationConfiguration;
import com.statseyes.studio.presentation.athletes.details.AthleteDetailsViewController;
import com.statseyes.studio.presentation.athletes.search.AthleteSearchViewController;
import com.statseyes.studio.presentation.navigation.SectionNavigator;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Controller;

@Controller
public class TemplateViewController {

    // ==================
    // FXML ENTITIES
    // ==================

    @FXML private VBox contentContainer;

    // ===================
    // INSTANCE VARIABLES
    // ===================

    private String currentPath;
    private String previousPath;

    private final SectionNavigator sectionNavigator;

    // =======================
    // PUBLIC API
    // =======================

    public TemplateViewController(
            SectionNavigator sectionNavigator
    ){
        this.sectionNavigator = sectionNavigator;
    }
    public void initialize(){
        loadDashboardView();
    }

    public boolean isShowingSearch() {
        return ApplicationConfiguration.ATHLETE_SEARCH_PATH.getValue()
                .equals(currentPath);
    }

    // Retour depuis la fiche : vers la recherche si on en venait, sinon vers la liste
    public void goBackToAthletes() {
        if (ApplicationConfiguration.ATHLETE_SEARCH_PATH.getValue().equals(previousPath))
            loadAthleteSearchView();
        else
            loadAthletesView();
    }
    public void loadDashboardView(){
        show(ApplicationConfiguration.DASHBOARD_PATH.getValue());
    }
    public void loadAthletesView(){
        show(ApplicationConfiguration.ATHLETES_PATH.getValue());
    }
    public void loadAthleteFormView() {
        show( ApplicationConfiguration.ATHLETE_FORM_PATH.getValue());
    }
    public void loadTeamsView(){
        show( ApplicationConfiguration.TEAMS_PATH.getValue());
    }
    public void loadSessionsView(){
        show(ApplicationConfiguration.SESSIONS_PATH.getValue());
    }
    public void loadAthleteDetailsView(Integer athleteId) {
        track(ApplicationConfiguration.ATHLETE_DETAILS_PATH.getValue());

        sectionNavigator.open(
                contentContainer,
                ApplicationConfiguration.ATHLETE_DETAILS_PATH.getValue(),
                AthleteDetailsViewController.class, athleteId
        );
    }
    public void loadAthleteSearchView(){
        show(ApplicationConfiguration.ATHLETE_SEARCH_PATH.getValue());
    }

    public void loadAnalyticsView(){
        show(ApplicationConfiguration.ANALYTICS_PATH.getValue());
    }

    // ===============
    // PRIVATE API
    // ===============

    /**
     * TemplateViewController doit savoir quelle vue est affichée.
     * Cela permet de ne pas rouvrir la recherche à chaque frappe,
     * et de faire revenir le bouton « Retour » de la fiche vers
     * les résultats.
     * @param path
     */
    private void track(String path) {
        previousPath = currentPath;
        currentPath = path;
    }

    private void show(String path) {
        track(path);
        sectionNavigator.open(contentContainer, path);
    }
}
