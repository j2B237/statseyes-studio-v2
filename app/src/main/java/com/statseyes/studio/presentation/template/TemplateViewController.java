package com.statseyes.studio.presentation.template;

import com.statseyes.studio.domain.config.ApplicationConfiguration;
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
        sectionNavigator.open(
                contentContainer, ApplicationConfiguration.DASHBOARD_PATH.getValue()
        );
    }
    public void loadAthletesView(){
        sectionNavigator.open(
                contentContainer, ApplicationConfiguration.ATHLETES_PATH.getValue()
        );
    }

    public void loadTeamsView(){
        sectionNavigator.open(
                contentContainer, ApplicationConfiguration.TEAMS_PATH.getValue()
        );
    }

    public void loadSessionsView(){
        sectionNavigator.open(
                contentContainer, ApplicationConfiguration.SESSIONS_PATH.getValue()
        );
    }

    public void loadAthleteDetailsView(Integer athleteId) {
        sectionNavigator.open(
                contentContainer,
                ApplicationConfiguration.ATHLETE_DETAILS_PATH.getValue(),
                AthleteDetailsViewController.class, athleteId
        );
    }

    public void loadAnalyticsView(){
        sectionNavigator.open(
                contentContainer,
                ApplicationConfiguration.ANALYTICS_PATH.getValue()
        );
    }
}
