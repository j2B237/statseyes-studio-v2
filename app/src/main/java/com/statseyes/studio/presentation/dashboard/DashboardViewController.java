package com.statseyes.studio.presentation.dashboard;

import com.statseyes.studio.domain.config.ApplicationConfiguration;
import com.statseyes.studio.domain.model.AccountSummary;
import com.statseyes.studio.infrastructure.cache.CacheStatsManager;
import com.statseyes.studio.infrastructure.cache.CacheType;
import com.statseyes.studio.presentation.component.StatsCard;
import com.statseyes.studio.presentation.navigation.ViewManager;
import com.statseyes.studio.presentation.navigation.ViewManagerAware;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
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
    private final CacheStatsManager cacheStatsManager;
    private ViewManager viewManager;

    private final ChangeListener<AccountSummary> summaryListener =
            (obs, oldValue, newValue) -> render(newValue);

    public DashboardViewController(
            DashboardViewModel viewModel,
            CacheStatsManager cacheStatsManager
    ) {
        this.viewModel = viewModel;
        this.cacheStatsManager = cacheStatsManager;
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
        viewModel.loadClubLogo();

        // After each screen navigation, dashboard card data need to
        // be refreshed.
        render(viewModel.summaryProperty().get());

        cacheStatsManager.printStats(CacheType.ACCOUNT_SUMMARY);
        System.out.println();

    }

    // ==============
    // PRIVATE API
    // ==============

    private void bindViewModel(){

        viewModel.summaryProperty().removeListener(summaryListener);
        viewModel.summaryProperty().addListener(summaryListener);
        clubLogoBackground.imageProperty().bindBidirectional(
                viewModel.clubLogoProperty()
        );
        clubNameLabel.textProperty().bindBidirectional(
                viewModel.clubNameProperty()
        );
    }

    private void render(AccountSummary summary) {

        if (summary == null) return;

        athletesCountCard.setValue(
                String.format(summary.athleteCount() + " \nathlete" +
                                (summary.athleteCount() > 1 ? "s" : ""))

        );
        teamsCountCard.setValue(
                String.format(summary.teamCount() + " \néquipe" +
                        (summary.teamCount() > 1 ? "s" : ""))
        );
        sessionsCountCard.setValue(String.format(
                summary.importedSessionCount() + " \nsession" +
                        (summary.importedSessionCount() > 1 ? "s importees" : " importee"))
        );

        if (summary.lastImportedAt() == null) {
            lastImportCard.setNoData("Aucun");
        } else {
            lastImportCard.setValue(summary.lastImportedAt().format(
                    DateTimeFormatter.ofPattern(ApplicationConfiguration.DAY_FORMAT.getValue())
            ));
            lastImportCard.setUnit(summary.lastImportedAt().format(
                    DateTimeFormatter.ofPattern(ApplicationConfiguration.HOUR_FORMAT.getValue())
            ));
            lastImportCard.setValue(
                    lastImportCard.valueProperty().getValue() + " " +
                    lastImportCard.unitProperty().getValue()  +
                            " \ndate dernière session."
            );
        }
    }
}