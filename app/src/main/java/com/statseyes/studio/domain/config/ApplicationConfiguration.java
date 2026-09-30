package com.statseyes.studio.domain.config;

public enum ApplicationConfiguration {

    TITLE("Statseyes-Studio"),
    DASHBOARD_PATH("/com/statseyes/studio/view/dashboard/DashboardView.fxml"),
    ATHLETES_PATH("/com/statseyes/studio/view/athletes/AthletesView.fxml"),
    TEAMS_PATH("/com/statseyes/studio/view/teams/TeamsView.fxml"),
    SESSIONS_PATH("/com/statseyes/studio/view/sessions/SessionsView.fxml"),
    ATHLETE_DETAILS_PATH("/com/statseyes/studio/view/athletes/AthleteDetailsView.fxml");
    private final String value;

    ApplicationConfiguration(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
