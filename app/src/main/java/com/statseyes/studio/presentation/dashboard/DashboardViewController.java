package com.statseyes.studio.presentation.dashboard;

import com.statseyes.studio.domain.config.ApplicationConfiguration;
import com.statseyes.studio.domain.model.AccountSummary;
import com.statseyes.studio.presentation.component.StatsCard;
import com.statseyes.studio.presentation.navigation.ViewManager;
import com.statseyes.studio.presentation.navigation.ViewManagerAware;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.springframework.stereotype.Controller;

import java.time.format.DateTimeFormatter;

@Controller
public class DashboardViewController implements ViewManagerAware {

    @FXML private StatsCard athletesCountCard;
    @FXML private StatsCard teamsCountCard;
    @FXML private StatsCard sessionsCountCard;
    @FXML private StatsCard lastImportCard;
    @FXML private ImageView clubLogoBackground;
    @FXML private Label clubNameLabel;

    private final DashboardViewModel viewModel;
    private ViewManager viewManager;

    private final ChangeListener<AccountSummary> summaryListener =
            (obs, oldValue, newValue) -> render(newValue);

    public DashboardViewController(
            DashboardViewModel viewModel
    ) {
        this.viewModel = viewModel;
    }

    @Override
    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }

    public void initialize() {

        athletesCountCard.setNoData("--");
        teamsCountCard.setNoData("--");
        sessionsCountCard.setNoData("--");
        lastImportCard.setNoData("--");

        bindViewModel();
        viewModel.load();
        render(viewModel.summaryProperty().get());
    }

    // ==============
    // PRIVATE API
    // ==============

    private void bindViewModel(){

        viewModel.summaryProperty().removeListener(summaryListener);
        viewModel.summaryProperty().addListener(summaryListener);
    }

    private void render(AccountSummary summary) {

        if (summary == null) return;

        athletesCountCard.setValue(String.valueOf(summary.athleteCount()));
        teamsCountCard.setValue(String.valueOf(summary.teamCount()));
        sessionsCountCard.setValue(String.valueOf(summary.importedSessionCount()));

        if (summary.lastImportedAt() == null) {
            lastImportCard.setNoData("Aucun");
        } else {
            lastImportCard.setValue(summary.lastImportedAt().format(
                    DateTimeFormatter.ofPattern(ApplicationConfiguration.DAY_FORMAT.getValue())
            ));
            lastImportCard.setUnit(summary.lastImportedAt().format(
                    DateTimeFormatter.ofPattern(ApplicationConfiguration.HOUR_FORMAT.getValue())
            ));
        }
    }
}