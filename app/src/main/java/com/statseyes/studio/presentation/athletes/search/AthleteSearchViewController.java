package com.statseyes.studio.presentation.athletes.search;

import com.statseyes.studio.domain.model.Athlete;
import com.statseyes.studio.presentation.navigation.Navigable;
import com.statseyes.studio.presentation.navigation.ViewManager;
import com.statseyes.studio.presentation.navigation.ViewManagerAware;
import com.statseyes.studio.presentation.template.TemplateViewController;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import org.springframework.stereotype.Controller;


@Controller
public class AthleteSearchViewController implements ViewManagerAware, Navigable<String> {

    // =================
    // FXML ENTITIES
    // =================

    @FXML
    private ChoiceBox teamFilterChoiceBox;
    @FXML
    private ChoiceBox positionFilterChoiceBox;
    @FXML
    private ChoiceBox statusFilterChoiceBox;
    @FXML
    private ListView<Athlete> athleteListView;

    // ====================
    // INSTANCE VARIABLES
    // ====================

    private ViewManager viewManager;
    private final TemplateViewController template;
    private final AthleteSearchViewModel viewModel;
    private String searchToken = null;


    public AthleteSearchViewController(
            TemplateViewController template,
            AthleteSearchViewModel viewModel
    ) {
        this.template = template;
        this.viewModel = viewModel;
    }

    // ================
    // PUBLIC API
    // ================

    @Override
    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }

    @Override
    public void onNavigate(String token) {
        this.searchToken = token;
    }

    public void initialize() {
    }


}
