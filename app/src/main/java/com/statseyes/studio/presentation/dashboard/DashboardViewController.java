package com.statseyes.studio.presentation.dashboard;

import com.statseyes.studio.presentation.component.StatsCard;
import com.statseyes.studio.presentation.navigation.ViewManager;
import com.statseyes.studio.presentation.navigation.ViewManagerAware;
import org.springframework.stereotype.Controller;

import javafx.fxml.FXML;

@Controller
public class DashboardViewController implements ViewManagerAware {

    // ===============
    // FXML ENTITIES
    // ===============

    @FXML private StatsCard vitesseCard;
    @FXML private StatsCard distanceCard;
    @FXML private StatsCard sprintsCard;
    @FXML private StatsCard directionCard;

    // ===================
    // INSTANCE VARIABLES
    // ===================

    private ViewManager viewManager;

    public DashboardViewController(){

    }


    public void initialize(){

    }

    public void setViewManager(ViewManager viewManager){
        this.viewManager = viewManager;
    }


}
