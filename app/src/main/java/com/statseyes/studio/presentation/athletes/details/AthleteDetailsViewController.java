package com.statseyes.studio.presentation.athletes.details;

import com.statseyes.studio.domain.config.ApplicationConfiguration;
import com.statseyes.studio.domain.model.*;
import com.statseyes.studio.infrastructure.cache.CacheType;
import com.statseyes.studio.presentation.component.ComparisonRow;
import com.statseyes.studio.presentation.component.HeatmapCanvas;
import com.statseyes.studio.presentation.navigation.Navigable;
import com.statseyes.studio.presentation.navigation.ViewManager;
import com.statseyes.studio.presentation.navigation.ViewManagerAware;
import com.statseyes.studio.presentation.component.StatsCard;
import com.statseyes.studio.presentation.template.TemplateViewController;
import com.statseyes.studio.infrastructure.cache.CacheStatsManager;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

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
    @FXML private HeatmapCanvas heatmapCanvas;
    @FXML private ListView<ImportedSession> sessionHistoryListView;
    @FXML private Label profileTeamLabel;
    @FXML private Label profilePositionLabel;
    @FXML private Label profileAgeLabel;
    @FXML private ImageView athletePhotoView;
    @FXML private Label athleteStatusBadge;
    @FXML private Label athleteFlagLabel;
    @FXML private Label athleteNationalityLabel;
    @FXML private Label athletePositionLabel;
    @FXML private Label athleteTeamLabel;
    @FXML private Label athleteJerseyLabel;
    @FXML private Label athleteAgeLabel;
    @FXML private Label athleteHeightLabel;
    @FXML private Label athleteWeightLabel;
    @FXML private ChoiceBox<ImportedSession> sessionAChoice;
    @FXML private ChoiceBox<ImportedSession> sessionBChoice;
    @FXML private VBox comparisonRowsContainer;

    // ====================
    // INSTANCE VARIABLES
    // ===================

    private final TemplateViewController templateViewController;
    private final AthleteDetailsViewModel viewModel;
    private final CacheStatsManager cacheStatsManager;

    private ViewManager viewManager;
    private Integer pendingAthleteId;

    private final ChangeListener<Athlete> athleteListener =
            (o, ov, nv) ->
            {renderAthlete(nv); renderProfile(nv);};
    private final ChangeListener<ImportedSession> sessionListener =
            (o, ov, nv) -> renderSession(nv);
    private final ChangeListener<String> statusListener =
            (o, ov, nv) -> importStatusLabel.setText(nv == null ? "" : nv);
    private final ChangeListener<Boolean> importingListener =
            (o, ov, nv) -> importButton.setDisable(nv);
    private final ChangeListener<List<GpsPoint>> heatmapPointsListener =
            (o, ov, nv) -> renderHeatmap(nv);
    private final ChangeListener<List<ImportedSession>> sessionHistoryListener =
            (o, ov, nv) -> {
                sessionHistoryListView.setItems(FXCollections.observableArrayList(nv));
                setComparisonChoices(nv);
    };
    private final ChangeListener<SessionComparison> sessionComparisonListener =
            (o, ov, nv) -> renderComparison(nv);
    private final ChangeListener<ImportedSession> selectionRefreshListener =
            (o, ov, nv) -> sessionHistoryListView.refresh();
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
        resetProfile();

        heatmapCanvas.clear();
        sessionHistoryListView.setCellFactory(list -> new SessionHistoryRowCell());

        if (pendingAthleteId != null) {
            viewModel.loadAthlete(pendingAthleteId);
            viewModel.loadSessionHistory(pendingAthleteId);
        }

        /*
            NOTE :
            Éviter d'utiliser synchroniquement des propriétés
            dont la valeur est chargee de manière asynchrone.
            Cela provoque des widgets figés avec les mauvaises
            valeurs.
            renderSession(viewModel.latestSessionProperty().get());
            renderHeatmap(viewModel.heatMapPointsProperty().get());
        */
        cacheStatsManager.printStats(CacheType.IMPORTED_SESSIONS);
        System.out.println();
        cacheStatsManager.printStats(CacheType.GPS_POINTS);
        System.out.println();
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

    @FXML
    protected void onCompare(ActionEvent event){
        ImportedSession a = sessionAChoice.getValue();
        ImportedSession b = sessionBChoice.getValue();

        if(a == null || b == null)return;
        viewModel.compare(a, b);
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

    private void resetProfile() {
        profileTeamLabel.setText("Équipe : —");
        profilePositionLabel.setText("Poste : —");
        profileAgeLabel.setText("Âge : —");
    }

    private void resetHeatmap(){

    }

    private String flagEmoji(String isoCode) {
        if (isoCode == null || isoCode.length() != 2) return "";
        int base = 0x1F1E6 - 'A';
        return new String(Character.toChars(base + Character.toUpperCase(isoCode.charAt(0))))
                + new String(Character.toChars(base + Character.toUpperCase(isoCode.charAt(1))));
    }

    private void renderAthlete(Athlete athlete) {
        if (athlete == null) return;
        athleteNameLabel.setText(athlete.firstname() + " " + athlete.lastname());

        athleteStatusBadge.setText(athlete.active() ? "● Actif" : "● Inactif");
        athleteStatusBadge.getStyleClass().setAll(
                athlete.active() ? "status-badge-active" : "status-badge-inactive"
        );

        athleteFlagLabel.setText(flagEmoji(athlete.nationalityCode()));
        athleteNationalityLabel.setText(athlete.nationalityCode() != null
                ? new Locale("", athlete.nationalityCode()).getDisplayCountry(Locale.FRENCH) : "—");

        athletePositionLabel.setText(athlete.positionName() != null ? athlete.positionName() : "—");
        athleteTeamLabel.setText(athlete.teamName() != null ? athlete.teamName() : "—");
        athleteJerseyLabel.setText(athlete.jerseyNumber() != null ? "N° " + athlete.jerseyNumber() : "—");

        long age = Period.between(athlete.birthday(), LocalDate.now()).getYears();
        athleteAgeLabel.setText(
                age + " ans (" + athlete.birthday().format(
                        DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.FRENCH)
                ) + ")"
        );

        athleteHeightLabel.setText(athlete.height() != null ? String.format("%.2f m", athlete.height()) : "—");
        athleteWeightLabel.setText(athlete.weight() != null ? String.format("%.0f kg", athlete.weight()) : "—");

        if (athlete.imageUrl() != null && !athlete.imageUrl().isBlank()) {
            athletePhotoView.setImage(
                    new Image(
                            athlete.imageUrl(),
                            110, 110,
                            false, true,
                            true)
            );
        }
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

        viewModel.loadHeatmapPoints(session.id());
    }

    private void renderProfile(Athlete athlete) {

        if (athlete == null) return;

        profileTeamLabel.setText("Équipe : " + (athlete.teamName() != null ? athlete.teamName() : "Aucune"));
        profilePositionLabel.setText("Poste : " + (athlete.positionName() != null ? athlete.positionName() : "Aucun"));

        long age = java.time.Period.between(athlete.birthday(), java.time.LocalDate.now()).getYears();
        profileAgeLabel.setText("Âge : " + age + " ans");
    }

    private void renderHeatmap(List<GpsPoint> points){
        if (points == null || points.size() < 2) return;

        heatmapCanvas.render(points);
    }

    private void renderComparison(SessionComparison sessionComparison){

        comparisonRowsContainer.getChildren().clear();
        if (sessionComparison == null)return;

        addRow(
            "Distance",
            sessionComparison.sessionA().totalDistanceMeters()/1000.0,
            sessionComparison.sessionB().totalDistanceMeters() / 1000.0,
            sessionComparison.distanceDeltaM() / 1000.0,
            "Km"
        );

        addRow(
            "vitesse max",
            sessionComparison.sessionA().maxSpeedKmh(),
            sessionComparison.sessionB().maxSpeedKmh(),
            sessionComparison.maxSpeedDeltaKmh(),
            "km/h"
        );

        addRow(
            "Sprints",
            sessionComparison.sessionA().sprintCount(),
            sessionComparison.sessionB().sprintCount(),
            sessionComparison.sprintCountDelta(),
            ""
        );
    }

    private void addRow(String label, double valueA, double valueB, double delta, String unit) {
        ComparisonRow row = new ComparisonRow();
        row.set(label, String.format("%.1f", valueA), String.format("%.1f", valueB), delta, unit);
        comparisonRowsContainer.getChildren().add(row);
    }

    private void setComparisonChoices(List<ImportedSession> sessions){
        StringConverter<ImportedSession> converter = new StringConverter<ImportedSession>() {
            @Override
            public String toString(ImportedSession s) {
                return s == null ? "" :
                        s.importedAt().format(
                            DateTimeFormatter.ofPattern(
                                    ApplicationConfiguration.SESSION_LABEL_FORMAT.getValue()
                            )
                        );
            }

            @Override
            public ImportedSession fromString(String string) {
                return null;
            }
        };

        sessionAChoice.setConverter(converter);
        sessionBChoice.setConverter(converter);
        sessionAChoice.setItems(FXCollections.observableArrayList(sessions));
        sessionBChoice.setItems(FXCollections.observableArrayList(sessions));
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

        viewModel.heatMapPointsProperty().removeListener(heatmapPointsListener);
        viewModel.heatMapPointsProperty().addListener(heatmapPointsListener);

        viewModel.sessionHistoryProperty().removeListener(sessionHistoryListener);
        viewModel.sessionHistoryProperty().addListener(sessionHistoryListener);

        viewModel.comparisonProperty().removeListener(sessionComparisonListener);
        viewModel.comparisonProperty().addListener(sessionComparisonListener);

        viewModel.latestSessionProperty().removeListener(selectionRefreshListener);
        viewModel.latestSessionProperty().addListener(selectionRefreshListener);
    }


    private final class SessionHistoryRowCell extends ListCell<ImportedSession> {

        // ==================
        // INSTANCE VARIABLES
        // ==================
        private static final PseudoClass SELECTED_PSEUDO_CLASS =
                PseudoClass.getPseudoClass("selected-session");
        private final Label dateLabel = new Label();
        private final Label distanceLabel = new Label();
        private final Label speedLabel = new Label();
        private final Label sprintsLabel = new Label();
        private final HBox content = new HBox(16, dateLabel, distanceLabel, speedLabel, sprintsLabel);

        private SessionHistoryRowCell() {

            content.getStyleClass().add("session-history-row");
            content.setAlignment(Pos.CENTER_LEFT);

            dateLabel.getStyleClass().add("session-history-date");
            distanceLabel.getStyleClass().add("session-history-stat");
            speedLabel.getStyleClass().add("session-history-stat");
            sprintsLabel.getStyleClass().add("session-history-stat");

            HBox.setHgrow(dateLabel, Priority.ALWAYS);

            content.setOnMouseClicked(e -> {
                ImportedSession session = getItem();
                if (session != null) {
                    viewModel.selectSession(session);
                }
            });
        }

        @Override
        protected void updateItem(ImportedSession session, boolean empty) {
            super.updateItem(session, empty);
            if (empty || session == null) { setGraphic(null); return; }

            SessionMetrics m = session.metrics();
            dateLabel.setText(session.importedAt().format(
                    DateTimeFormatter.ofPattern(
                            ApplicationConfiguration.SESSION_LABEL_FORMAT.getValue()
            )));
            distanceLabel.setText(String.format("%.1f km", m.totalDistanceMeters() / 1000.0));
            speedLabel.setText(String.format("%.1f km/h max", m.maxSpeedKmh()));
            sprintsLabel.setText(m.sprintCount() + " sprints");

            boolean isSelected = viewModel.latestSessionProperty().get() != null
                    && session.id().equals(viewModel.latestSessionProperty().get().id());
            content.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, isSelected);

            setGraphic(content);
        }
    }
}
