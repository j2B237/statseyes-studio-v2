package com.statseyes.studio.presentation.home;

import com.statseyes.studio.presentation.component.StatsCard;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.springframework.stereotype.Controller;

@Controller
public class HomeViewController {

    // ===================
    // FXML ENTITIES
    // ===================

    @FXML private Label userNameLabel;
    @FXML private ImageView clubLogoBackground;
    @FXML private StatsCard athletesCountCard;
    @FXML private StatsCard teamsCountCard;
    @FXML private StatsCard sessionsCountCard;
    @FXML private StatsCard lastImportCard;

    public HomeViewController(){}

    public void initialize(){}
}
