package com.statseyes.studio.presentation.dashboard;

import com.statseyes.studio.domain.model.AccountSummary;
import com.statseyes.studio.presentation.component.StatsCard;
import com.statseyes.studio.presentation.navigation.ViewManager;
import com.statseyes.studio.presentation.navigation.ViewManagerAware;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import org.springframework.stereotype.Controller;

import java.time.format.DateTimeFormatter;

@Controller
public class DashboardViewController implements ViewManagerAware {

    private static final DateTimeFormatter DAY_FORMAT  = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter HOUR_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @FXML private StatsCard athletesCountCard;
    @FXML private StatsCard teamsCountCard;
    @FXML private StatsCard sessionsCountCard;
    @FXML private StatsCard lastImportCard;

    private final DashboardViewModel viewModel;
    private ViewManager viewManager;

    private final ChangeListener<AccountSummary> summaryListener =
            (obs, oldValue, newValue) -> render(newValue);

    public DashboardViewController(DashboardViewModel viewModel) {
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

        viewModel.summaryProperty().removeListener(summaryListener);
        viewModel.summaryProperty().addListener(summaryListener);

        viewModel.load();
    }

    private void render(AccountSummary summary) {
        if (summary == null) return;

        athletesCountCard.setValue(String.valueOf(summary.athleteCount()));
        teamsCountCard.setValue(String.valueOf(summary.teamCount()));
        sessionsCountCard.setValue(String.valueOf(summary.importedSessionCount()));

        if (summary.lastImportedAt() == null) {
            lastImportCard.setNoData("Aucun");
        } else {
            lastImportCard.setValue(summary.lastImportedAt().format(DAY_FORMAT));
            lastImportCard.setUnit(summary.lastImportedAt().format(HOUR_FORMAT));
        }
    }
}