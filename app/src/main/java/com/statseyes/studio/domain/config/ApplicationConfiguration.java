package com.statseyes.studio.domain.config;

public enum ApplicationConfiguration {
    // WINDOW PARAMS
    TITLE("StatsEyes-Studio"),
    // TIME FORMAT
    DAY_FORMAT("dd/MM"),
    HOUR_FORMAT("HH:mm"),
    // METRICS UNIT
    DISTANCE_UNIT("km"),
    SPEED_UNIT("km/h"),
    DIRECTION_UNIT("°"),
    // FILE SYSTEM PATH
    USER_HOME("user.home"),
    UPLOAD_SUBDIR(".statseyes/uploads/clubs"),
    // CSS PATH
    CSS_PATH("/com/statseyes/studio/static/css/"),
    // FXML VIEWS PATH
    HOME_PATH("/com/statseyes/studio/view/home/HomeView.fxml"),
    LOGIN_PATH("/com/statseyes/studio/view/login/LoginView.fxml"),
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
