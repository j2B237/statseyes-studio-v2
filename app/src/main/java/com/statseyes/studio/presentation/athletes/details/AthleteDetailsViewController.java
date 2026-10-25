package com.statseyes.studio.presentation.athletes.details;

import com.statseyes.studio.application.usecase.athlete.GetAthleteImageUseCase;
import com.statseyes.studio.domain.config.ApplicationConfiguration;
import com.statseyes.studio.domain.model.*;
import com.statseyes.studio.infrastructure.cache.CacheType;
import com.statseyes.studio.presentation.athletes.profile.AvailabilityDialog;

import com.statseyes.studio.presentation.athletes.importing.PodImportDialog;
import com.statseyes.studio.presentation.athletes.profile.AthleteProfileViewModel;
import com.statseyes.studio.presentation.component.ComparisonRow;
import com.statseyes.studio.presentation.component.HeatmapCanvas;
import com.statseyes.studio.presentation.navigation.Navigable;
import com.statseyes.studio.presentation.navigation.ViewManager;
import com.statseyes.studio.presentation.navigation.ViewManagerAware;
import com.statseyes.studio.presentation.component.StatsCard;
import com.statseyes.studio.presentation.template.TemplateViewController;
import com.statseyes.studio.infrastructure.cache.CacheStatsManager;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.StringConverter;

import lombok.extern.slf4j.Slf4j;

import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Slf4j
@Controller
public class AthleteDetailsViewController implements ViewManagerAware, Navigable<Integer> {

    // ===================
    // FXML ENTITIES
    // ===================

    @FXML private Label athleteNameLabel;
    @FXML private Label importStatusLabel;
    @FXML private Button importButton;

    // Stats-cards
    @FXML private StatsCard distanceCard;
    @FXML private StatsCard vitesseCard;
    @FXML private StatsCard sprintsCard;
    @FXML private StatsCard directionCard;
    @FXML private HeatmapCanvas heatmapCanvas;
    @FXML private ListView<ImportedSession> sessionHistoryListView;

    // Informations visibles sur la carte du joueur
    @FXML private ImageView athletePhotoView;
    @FXML private Label athleteFlagLabel;
    @FXML private Label athleteNationalityLabel;
    @FXML private Label athleteTeamLabel;
    @FXML private Label athleteJerseyLabel;

    @FXML private HBox availabilityBanner;
    @FXML private Label bannerLabel, physicalLine, statusBadge, profileStatusBadge;
    @FXML private Label reasonLabel, sinceLabel, returnLabel;
    @FXML private Label profileTeamLabel, profileBirthLabel, profileNationalityLabel;
    @FXML private FlowPane positionsPane, secondaryPane;
    @FXML private ToggleButton footLeft, footRight, footBoth;
    @FXML private ComboBox<String> mainPositionCombo, addSecondaryCombo;

    // Informations concernant le Pod
    @FXML private Label lastImportDateLabel;
    @FXML private Label importedSessionsCountLabel;

    // Informations visibles sur le bandeau au-dessus des stats cards
    @FXML private Label sessionInfoDateLabel;
    @FXML private Label sessionDurationLabel;
    @FXML private Label sessionDateLabel;
    @FXML private Label sessionLocationLabel;

    // Choices box pour la comparaison de sessions
    @FXML private ChoiceBox<ImportedSession> sessionAChoice;
    @FXML private ChoiceBox<ImportedSession> sessionBChoice;
    @FXML private VBox comparisonRowsContainer;

    // statistiques visibles sur le panneau laterals
    @FXML private Label statDistancePerMinLabel;
    @FXML private Label statAccelLabel;
    @FXML private Label statDecelLabel;
    @FXML private Label statDirectionChangesLabel;
    @FXML private Label statDurationLabel;
    @FXML private ToggleButton transferToggle;

    // ====================
    // INSTANCE VARIABLES
    // ===================

    private static final boolean DEMO_PROFILE_DATA = true;

    private static final DateTimeFormatter BANNER_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.FRENCH);
    private final TemplateViewController templateViewController;
    private final AthleteDetailsViewModel viewModel;
    private final CacheStatsManager cacheStatsManager;
    private final PodImportDialog podDialog;
    private final GetAthleteImageUseCase athleteImageUseCase;

    private final AthleteProfileViewModel profileViewModel;
    private ToggleGroup footGroup;
    private ViewManager viewManager;
    private Integer pendingAthleteId;

    // Event listeners
    private final ChangeListener<Athlete> athleteListener =
            (o, ov, nv) -> {
        renderAthlete(nv);
        renderProfile(nv);
    };
    private final ChangeListener<ImportedSession> sessionListener =
            (o, ov, nv) -> {
        renderSession(nv);
        applyDeltas();
    };
    private final ChangeListener<String> statusListener =
            (o, ov, nv) -> importStatusLabel.setText(nv == null ? "" : nv);
    private final ChangeListener<Boolean> importingListener =
            (o, ov, nv) -> importButton.setDisable(nv);
    private final ChangeListener<List<GpsPoint>> heatmapPointsListener =
            (o, ov, nv) -> renderHeatmap(nv);
    private final ChangeListener<List<ImportedSession>> sessionHistoryListener =
            (o, ov, nv) -> {
                List<ImportedSession> sessions = nv == null ? List.of() : nv;
                sessionHistoryListView.setItems(FXCollections.observableArrayList(sessions));
                setComparisonChoices(sessions);
                renderPodStatus(sessions);
    };
    private final ChangeListener<SessionComparison> sessionComparisonListener =
            (o, ov, nv) -> renderComparison(nv);
    private final ChangeListener<ImportedSession> selectionRefreshListener =
            (o, ov, nv) -> sessionHistoryListView.refresh();

    private final ChangeListener<TeamAverageMetrics> teamAverageListener =
            (o, ov, nv) -> applyDeltas();

    private final ChangeListener<Boolean> transferSavingListener =
            (o, ov, nv) -> transferToggle.setDisable(nv);
    private final ChangeListener<DominantFoot> footListener =
            (obs, ov, nv) -> {
                selectFoot (nv);
                renderPhysicalLine();
    };

    private final ListChangeListener<String> secondaryListener = c -> {
        renderSecondaryChips();
        renderPositionChips();
    };
    private final ChangeListener<String> mainPositionListener =
            (o, a, b) -> renderPositionChips();
    private final ChangeListener<String> statusStyleListener =
            (o, old, now) -> {
                swapStatusClass(statusBadge, old, now);
                swapStatusClass(profileStatusBadge, old, now);
    };

    // ===================
    // PUBLIC API
    // ===================

    public AthleteDetailsViewController(
            AthleteDetailsViewModel viewModel,
            TemplateViewController templateViewController,
            CacheStatsManager cacheStatsManager,
            PodImportDialog podDialog,
            GetAthleteImageUseCase athleteImageUseCase,
            AthleteProfileViewModel profileViewModel
    ){
        this.viewModel = viewModel;
        this.templateViewController = templateViewController;
        this.cacheStatsManager = cacheStatsManager;
        this.podDialog = podDialog;
        this.athleteImageUseCase = athleteImageUseCase;
        this.profileViewModel = profileViewModel;
    }

    @Override
    public void setViewManager(ViewManager viewManager) { this.viewManager = viewManager; }

    @Override
    public void onNavigate(Integer athleteId) { this.pendingAthleteId = athleteId; }

    public void initialize() {

        // Bloqué tant que l'athlète n'est pas chargé (évite d'agir sur l'athlète de la visite précédente)
        transferToggle.setDisable(true);

        bindViewModel();
        resetCards();
        profileViewModel.reset();
        bindProfile();
        
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
        cacheStatsManager.printStats(CacheType.TEAM_AVG_METRICS);
        System.out.println();
    }

    // ===================
    // PROTECTED API
    // ===================

    /*
    @FXML
    protected void onImportDump(ActionEvent event){
        viewModel.importDumpForCurrentAthlete();
    }*/

    @FXML
    protected void onImportDump(ActionEvent event) {
        Athlete athlete = viewModel.athleteProperty().get();
        if (athlete == null) return;
        Window owner = importButton.getScene().getWindow();
        podDialog.show(owner, athlete.id(),
                session -> viewModel.onSessionImported(session, athlete.id()));
    }

    @FXML
    protected void onBack() {
        templateViewController.goBackToAthletes();
    }

    @FXML
    protected void onCompare(ActionEvent event){
        ImportedSession a = sessionAChoice.getValue();
        ImportedSession b = sessionBChoice.getValue();

        if(a == null || b == null)return;
        viewModel.compare(a, b);
    }

    @FXML
    protected void onViewAnalytics(MouseEvent event){
        templateViewController.loadAnalyticsView();
    }
    @FXML
    protected void onToggleTransfer() {
        viewModel.setTransferAvailability(
                transferToggle.isSelected());
    }
    @FXML
    protected void onEditAvailability(ActionEvent event){
        AvailabilityDialog.show(profileViewModel, athleteNameLabel.getText(),athleteNameLabel.getScene().getWindow());
    }
    @FXML
    protected void onSaveProfile(){}

    // ===================
    // PRIVATE API
    // ===================

    private void resetCards() {

        distanceCard.setNoData("--");
        vitesseCard.setNoData("--");
        sprintsCard.setNoData("--");
        directionCard.setNoData("--");
    }

    private void renderPodStatus(List<ImportedSession> sessions){
        if( sessions == null || sessions.isEmpty()){
            lastImportDateLabel.setText("Aucun import");
            importedSessionsCountLabel.setText("0 séance");
            return;
        }

        ImportedSession latest = sessions.getLast();
        lastImportDateLabel.setText(
                latest.importedAt().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm"))
        );
        importedSessionsCountLabel.setText(
                sessions.size() + " seance" + (sessions.size() > 1 ? "s" : "")
        );
    }
    private void renderAthlete(Athlete athlete) {

        if (athlete == null) return;
        athleteNameLabel.setText(athlete.firstname() + " " + athlete.lastname());

        athleteFlagLabel.setText(flagEmoji(athlete.nationalityCode()));
        athleteNationalityLabel.setText(athlete.nationalityCode() != null
                ? new Locale("", athlete.nationalityCode()).getDisplayCountry(Locale.FRENCH) : "—");

        String code = athlete.nationalityCode();
        boolean hasCode = code != null && !code.isBlank();

        athleteFlagLabel.setText(hasCode ? code.toUpperCase() : "");
        athleteFlagLabel.setVisible(hasCode);
        athleteFlagLabel.setManaged(hasCode);

        athleteTeamLabel.setText(athlete.teamName() != null ? athlete.teamName() : "—");
        athleteJerseyLabel.setText(athlete.jerseyNumber() != null ? "N° " + athlete.jerseyNumber() : "—");

        if (athlete.imageUrl() != null && !athlete.imageUrl().isBlank()) {
            System.out.println("From Athlete details: " +  Objects.requireNonNull(
                    athleteImageUseCase.execute(athlete.imageUrl())
                            .orElse(null)));

            athletePhotoView.setImage(
                    new Image(
                            Objects.requireNonNull(
                                    athleteImageUseCase.execute(athlete.imageUrl())
                                            .orElse(null)),
                            110, 110,
                            false, true,
                            true)
            );
        }

        boolean onList = athlete.available();
        transferToggle.setSelected(onList);   // ne déclenche pas onAction
        transferToggle.setText(onList ? "Sur la liste des transferts" : "Mettre sur la liste des transferts");
        transferToggle.setGraphic(new FontIcon(onList ? "fas-check" : "fas-plus"));
        transferToggle.setDisable(false);
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

        sessionInfoDateLabel.setText(session.importedAt().format(
                DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.FRENCH)));
        sessionDurationLabel.setText(formatDuration(session.metrics().durationSeconds()));

        // Session banner
        sessionDateLabel.setText(
                "Seance du " + session.importedAt().format(BANNER_DATE_FORMAT)
        );
        sessionLocationLabel.setText("Terrain principal");


        // Affichage Informations complementaires
        statDurationLabel.setText(
                formatDuration(session.metrics().durationSeconds())
        );

        // Affichage des statistiques
        statDistancePerMinLabel.setText(String.format("%.0f m/min", metrics.distancePerMinuteM()));
        statAccelLabel.setText(String.valueOf(metrics.accelerationCount()));
        statDecelLabel.setText(String.valueOf(metrics.decelerationCount()));
        statDirectionChangesLabel.setText(String.valueOf(metrics.directionChangeCount()));

        viewModel.loadHeatmapPoints(session.id());
    }

    private void renderProfile(Athlete athlete) {

        if (athlete == null) return;

        profileViewModel.mainPositionProperty().set(athlete.positionName() != null ? athlete.positionName() : "");
        profileBirthLabel.setText(athlete.birthday().format(BANNER_DATE_FORMAT));
        profileNationalityLabel.setText(athleteNationalityLabel.getText());
        profileTeamLabel.setText(athlete.teamName() != null ? athlete.teamName() : "Aucune");

        if (DEMO_PROFILE_DATA) {
            profileViewModel.secondaryPositionsProperty().setAll("Ailier droit", "Attaquant");
            profileViewModel.setAvailability(new Availability(AvailabilityStatus.INJURED, "Blessure à la cuisse",
                    LocalDate.now().minusDays(14), LocalDate.now().plusDays(20)));
            log.info("Disponibilité appliquée : {}", profileViewModel.availabilityProperty().get());
        }

        renderPhysicalLine();
        renderPositionChips();

    }

    private void renderPositionChips(){
        positionsPane.getChildren().clear();
        String main = profileViewModel.mainPositionProperty().get();
        if (main != null && !main.isBlank()) {
            Label l = new Label(main);
            l.getStyleClass().add("chip-main");
            positionsPane.getChildren().add(l);
        }
        for (String p : profileViewModel.secondaryPositionsProperty()) {
            Label l = new Label(p);
            l.getStyleClass().add("chip");
            positionsPane.getChildren().add(l);
        }
    }
    private void renderSecondaryChips() {

        secondaryPane.getChildren().clear();
        for (String p : profileViewModel.secondaryPositionsProperty()) {

            Label name = new Label(p);
            Button remove = new Button(); remove.setGraphic(new FontIcon("fas-times"));
            remove.getStyleClass().add("chip-remove");

            remove.setOnAction(e -> profileViewModel.secondaryPositionsProperty().remove(p));
            HBox chip = new HBox(6, name, remove); chip.getStyleClass().add("chip");
            secondaryPane.getChildren().add(chip);
        }
    }
    private void renderPhysicalLine() {
        Athlete a = viewModel.athleteProperty().get();

        if(a == null) {physicalLine.setText("");return;};
        long age = Period.between(a.birthday(), LocalDate.now()).getYears();

        String foot = switch (profileViewModel.dominantFootProperty().get()){
           case DominantFoot.LEFT   -> "Gauche";
           case DominantFoot.RIGHT  -> "Droit";
           case DominantFoot.BOTH   -> "Les deux";
        };

        physicalLine.setText(age + " ans , " +
                (a.height() != null ? String.format("%.0f cm", a.height()) : "-") + ", " +
                (a.weight() != null ? String.format("%.0f Kg", a.weight()) : "-") +
                ", Pied fort : " + foot
        );
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
    private void applyDeltas(){
        ImportedSession session = viewModel.latestSessionProperty().get();
        TeamAverageMetrics avg = viewModel.teamAverageProperty().get();
        if (session == null || avg == null) return;

        SessionMetrics m = session.metrics();
        distanceCard.setDelta(percentDelta(m.totalDistanceMeters(), avg.avgDistanceM()));
        vitesseCard.setDelta(percentDelta(m.maxSpeedKmh(), avg.avgMaxSpeedKmh()));
        sprintsCard.setDelta(percentDelta(m.sprintCount(), avg.avgSprintCount()));
    }
    private double percentDelta(double value, double average) {
        if (average == 0) return 0;
        return ((value - average) / average) * 100.0;
    }

    private String flagEmoji(String isoCode) {
        if (isoCode == null || isoCode.length() != 2) return "";
        int base = 0x1F1E6 - 'A';
        return new String(Character.toChars(base + Character.toUpperCase(isoCode.charAt(0))))
                + new String(Character.toChars(base + Character.toUpperCase(isoCode.charAt(1))));
    }

    private String formatDuration(long totalSeconds) {
        long h = totalSeconds / 3600, m = (totalSeconds % 3600) / 60;
        return h > 0 ? h + "h " + m + "m" : m + "m";
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

    private void swapStatusClass(Label l, String old, String now) {
        if (old != null) l.getStyleClass().remove(old);
        if (now != null && !l.getStyleClass().contains(now)) l.getStyleClass().add(now);
    }
    private void selectFoot(DominantFoot f) {
        if (f == null) return;
        for (Toggle t : footGroup.getToggles()) if (t.getUserData() == f) t.setSelected(true);
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

        viewModel.teamAverageProperty().removeListener(teamAverageListener);
        viewModel.teamAverageProperty().addListener(teamAverageListener);

        viewModel.transferSavingProperty().removeListener(transferSavingListener);
        viewModel.transferSavingProperty().addListener(transferSavingListener);
    }

    private void bindProfile() {

        availabilityBanner.visibleProperty().bind(profileViewModel.bannerVisibleProperty());
        availabilityBanner.managedProperty().bind(availabilityBanner.visibleProperty());
        bannerLabel.textProperty().bind(profileViewModel.bannerTextProperty());
        reasonLabel.textProperty().bind(profileViewModel.reasonTextProperty());
        sinceLabel.textProperty().bind(profileViewModel.sinceTextProperty());
        returnLabel.textProperty().bind(profileViewModel.returnTextProperty());
        statusBadge.textProperty().bind(profileViewModel.statusLabelProperty().map(s -> "● " + s));
        profileStatusBadge.textProperty().bind(profileViewModel.statusLabelProperty().map(s -> "● " + s));

        profileViewModel.statusStyleProperty().removeListener(statusStyleListener);
        profileViewModel.statusStyleProperty().addListener(statusStyleListener);
        swapStatusClass(statusBadge, null, profileViewModel.statusStyleProperty().get());
        swapStatusClass(profileStatusBadge, null, profileViewModel.statusStyleProperty().get());

        footGroup = new ToggleGroup();
        footLeft.setToggleGroup(footGroup);
        footRight.setToggleGroup(footGroup);
        footBoth.setToggleGroup(footGroup);
        footLeft.setUserData(DominantFoot.LEFT);
        footRight.setUserData(DominantFoot.RIGHT);
        footBoth.setUserData(DominantFoot.BOTH);
        footGroup.selectedToggleProperty().addListener((ob, ov, nv) -> {
            if (nv != null) profileViewModel.dominantFootProperty().set((DominantFoot) nv.getUserData());
            else if (ov != null) ov.setSelected(true);          // jamais de désélection totale
        });
        profileViewModel.dominantFootProperty().removeListener(footListener);
        profileViewModel.dominantFootProperty().addListener(footListener);
        selectFoot(profileViewModel.dominantFootProperty().get());   // état initial

        profileViewModel.secondaryPositionsProperty().removeListener(secondaryListener);
        profileViewModel.secondaryPositionsProperty().addListener(secondaryListener);
        profileViewModel.mainPositionProperty().removeListener(mainPositionListener);
        profileViewModel.mainPositionProperty().addListener(mainPositionListener);

        List<String> positions = List.of("Gardien", "Défenseur central", "Latéral", "Milieu défensif",
                "Milieu offensif", "Ailier droit", "Ailier gauche", "Attaquant");
        mainPositionCombo.setItems(FXCollections.observableArrayList(positions));
        addSecondaryCombo.setItems(FXCollections.observableArrayList(positions));
        mainPositionCombo.valueProperty().bindBidirectional(profileViewModel.mainPositionProperty());

        addSecondaryCombo.setOnAction(e -> {
            String p = addSecondaryCombo.getValue();
            if (p == null) return;
            if (!p.equals(profileViewModel.mainPositionProperty().get())
                    && !profileViewModel.secondaryPositionsProperty().contains(p)) {
                profileViewModel.secondaryPositionsProperty().add(p);
            }
            Platform.runLater(() -> addSecondaryCombo.setValue(null));
        });

        renderSecondaryChips();
        renderPositionChips();
    }
    ///////////////////////////////////
    ///
    /// PRIVATE INNER CLASSES
    ///
    ///////////////////////////////////
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
