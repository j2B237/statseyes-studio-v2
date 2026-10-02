package com.statseyes.studio.presentation.athletes.details;

import com.statseyes.studio.domain.config.ApplicationConfiguration;
import com.statseyes.studio.domain.model.Athlete;
import com.statseyes.studio.domain.model.ImportedSession;
import com.statseyes.studio.domain.model.SessionMetrics;
import com.statseyes.studio.infrastructure.cache.CacheType;
import com.statseyes.studio.presentation.navigation.Navigable;
import com.statseyes.studio.presentation.navigation.ViewManager;
import com.statseyes.studio.presentation.navigation.ViewManagerAware;
import com.statseyes.studio.presentation.component.StatsCard;
import com.statseyes.studio.presentation.template.TemplateViewController;
import com.statseyes.studio.infrastructure.cache.CacheStatsManager;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import org.springframework.stereotype.Controller;

@Controller
public class AthleteDetailsViewController implements ViewManagerAware, Navigable<Integer> {

    // ===================
    // FXML ENTITIES
    // ===================

    @FXML private Label athleteNameLabel;
    @FXML private Label athleteSubtitleLabel;
    @FXML private Label importStatusLabel;
    @FXML private Button importButton;
    @FXML private StatsCard distanceCard;
    @FXML private StatsCard vitesseCard;
    @FXML private StatsCard sprintsCard;
    @FXML private StatsCard directionCard;

    // ====================
    // INSTANCE VARIABLES
    // ===================

    private final TemplateViewController templateViewController;
    private final AthleteDetailsViewModel viewModel;
    private final CacheStatsManager cacheStatsManager;

    private ViewManager viewManager;
    private Integer pendingAthleteId;

    private final ChangeListener<Athlete> athleteListener =
            (o, ov, nv) -> renderAthlete(nv);
    private final ChangeListener<ImportedSession> sessionListener =
            (o, ov, nv) -> renderSession(nv);
    private final ChangeListener<String> statusListener =
            (o, ov, nv) -> importStatusLabel.setText(nv == null ? "" : nv);
    private final ChangeListener<Boolean> importingListener =
            (o, ov, nv) -> importButton.setDisable(nv);


    // ===================
    // PUBLIC API
    // ===================

    public AthleteDetailsViewController(
            AthleteDetailsViewModel viewModel,
            TemplateViewController templateViewController,
            CacheStatsManager cacheStatsManager
    ){
        this.viewModel = viewModel;
        this.templateViewController = templateViewController;
        this.cacheStatsManager = cacheStatsManager;
    }

    @Override
    public void setViewManager(ViewManager viewManager) { this.viewManager = viewManager; }

    @Override
    public void onNavigate(Integer athleteId) { this.pendingAthleteId = athleteId; }

    public void initialize() {

        bindViewModel();
        resetCards();

        if (pendingAthleteId != null) {
            viewModel.loadAthlete(pendingAthleteId);
        }

        renderAthlete(viewModel.athleteProperty().get());
        renderSession(viewModel.latestSessionProperty().get());

        cacheStatsManager.printStats(CacheType.IMPORTED_SESSIONS);
        System.out.println();
    }


    // ===================
    // PRIVATE API
    // ===================

    private void resetCards() {

        distanceCard.setNoData("--");
        vitesseCard.setNoData("--");
        sprintsCard.setNoData("--");
        directionCard.setNoData("--");
    }

    private void renderAthlete(Athlete athlete) {
        if (athlete == null) return;
        athleteNameLabel.setText(athlete.firstname() + " " + athlete.lastname());
        athleteSubtitleLabel.setText("Fiche athlete");
    }

    private void renderSession(ImportedSession session) {

        if (session == null) { resetCards(); return; }

        SessionMetrics metrics = session.metrics();

        distanceCard.setValue(metrics.totalDistanceMeters() / 1000.0, 2);
        distanceCard.setUnit(ApplicationConfiguration.DISTANCE_UNIT.getValue());

        vitesseCard.setValue(metrics.maxSpeedKmh(), 1);
        vitesseCard.setUnit(ApplicationConfiguration.SPEED_UNIT.getValue());

        sprintsCard.setValue(String.valueOf(metrics.sprintCount()));
        directionCard.setValue(metrics.dominantCourseDegrees(), 0);
        directionCard.setUnit(ApplicationConfiguration.DIRECTION_UNIT.getValue());
    }

    private void bindViewModel(){
        viewModel.athleteProperty().removeListener(athleteListener);
        viewModel.athleteProperty().addListener(athleteListener);

        viewModel.latestSessionProperty().removeListener(sessionListener);
        viewModel.latestSessionProperty().addListener(sessionListener);

        viewModel.importStatusProperty().removeListener(statusListener);
        viewModel.importStatusProperty().addListener(statusListener);

        viewModel.importingProperty().removeListener(importingListener);
        viewModel.importingProperty().addListener(importingListener);
    }


    // ===================
    // PROTECTED API
    // ===================

    @FXML
    protected void onImportDump(ActionEvent event){
        viewModel.importDumpForCurrentAthlete();
    }

    @FXML
    protected void onBack() {
        templateViewController.loadAthletesView();
    }
}
