package com.statseyes.studio.presentation.template;

import com.statseyes.studio.presentation.athletes.details.AthleteDetailsViewController;
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

    private static final String DASHBOARD_PATH        = "/com/statseyes/studio/view/dashboard/DashboardView.fxml";
    private static final String ATHLETES_PATH          = "/com/statseyes/studio/view/athletes/AthletesView.fxml";
    private static final String TEAMS_PATH             = "/com/statseyes/studio/view/teams/TeamsView.fxml";
    private static final String SESSIONS_PATH          = "/com/statseyes/studio/view/sessions/SessionsView.fxml";
    private static final String ATHLETE_DETAILS_PATH   = "/com/statseyes/studio/view/athletes/AthleteDetailsView.fxml";

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

    public void loadDashboardView(){
        sectionNavigator.open(contentContainer, DASHBOARD_PATH);
    }
    public void loadAthletesView(){
        sectionNavigator.open(contentContainer, ATHLETES_PATH);
    }

    public void loadTeamsView(){
        sectionNavigator.open(contentContainer, TEAMS_PATH);
    }

    public void loadSessionsView(){
        sectionNavigator.open(contentContainer, SESSIONS_PATH);
    }

    public void loadAthleteDetailsView(Integer athleteId) {
        sectionNavigator.open(
                contentContainer, ATHLETE_DETAILS_PATH,
                AthleteDetailsViewController.class, athleteId
        );
    }
}
