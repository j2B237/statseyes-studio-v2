package com.statseyes.studio.presentation.template;

import com.statseyes.studio.presentation.athletes.details.AthleteDetailsViewController;
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

    private final AthleteDetailsViewController athleteDetailsViewController;

    public TemplateViewController(
            AthleteDetailsViewController athleteDetailsViewController
    ){
        this.athleteDetailsViewController = athleteDetailsViewController;
    }


    public void loadAthletesView(){
        System.out.println("Loading athletes view...");
    }

    public void loadTeamsView(){
        System.out.println("Loading teams view...");
    }

    public void loadSessionsView(){
        System.out.println("Loading sessions view...");
    }
}
