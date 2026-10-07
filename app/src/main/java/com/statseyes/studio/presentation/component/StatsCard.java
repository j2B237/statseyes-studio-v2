package com.statseyes.studio.presentation.component;

import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.Objects;
import org.kordamp.ikonli.javafx.FontIcon;

public class StatsCard extends VBox {

    // ==============
    // PROPERTIES
    // ==============

    private final StringProperty title      = new SimpleStringProperty();
    private final StringProperty value      = new SimpleStringProperty("--");
    private final StringProperty unit       = new SimpleStringProperty("");
    private final StringProperty iconGlyph  = new SimpleStringProperty();
    private final ObjectProperty<Color> accentColor = new SimpleObjectProperty<>(Color.web("#4F8EF7"));
    private final BooleanProperty dataAvailable = new SimpleBooleanProperty(true);


    // ==================
    // INSTANCE VARIABLES
    // ==================

    private static final double[] WAVEFORM_PATTERN = {
            0.25, 0.35, 0.3, 0.55, 0.9, 0.45, 0.3, 0.6, 0.4, 0.3
    };
    private final Circle iconBadge = new Circle(18);
    private final FontIcon iconNode = new FontIcon();
    private final Label titleLabel = new Label();
    private final Label valueLabel = new Label();
    private final Label unitLabel = new Label();
    private final Label deltaLabel = new Label();
    private final HBox sparkBar = new HBox(3);

    // =================
    // PUBLIC API
    // =================

    public StatsCard(){

        getStyleClass().add("stats-card");
        getStylesheets().add(
                Objects.requireNonNull(getClass().getResource(
                                "/com/statseyes/studio/static/css/component/stats-card.css"))
                        .toExternalForm()
        );

        setPadding(new Insets(16));
        setSpacing(12);
        setMaxWidth(Double.MAX_VALUE);
        setMaxHeight(Double.MAX_VALUE);

        iconNode.setIconSize(16);
        StackPane iconStack = new StackPane(iconBadge, iconNode);

        titleLabel.getStyleClass().add("stats-card-title");
        titleLabel.textProperty().bind(title);
        titleLabel.setWrapText(false);
        titleLabel.setMinWidth(0);

        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        HBox header = new HBox(10, iconStack, titleLabel, spacer(), sparkBar);
        header.setAlignment(Pos.CENTER_LEFT);

        HBox valueRow = new HBox(valueLabel, unitLabel);
        valueRow.setAlignment(Pos.BASELINE_LEFT);

        valueLabel.getStyleClass().add("stats-card-value");   // <- manquait aussi : jamais stylé
        valueLabel.textProperty().bind(value);
        unitLabel.getStyleClass().add("stats-card-unit");      // <- idem
        unitLabel.textProperty().bind(unit);

        deltaLabel.getStyleClass().add("stats-card-delta");
        deltaLabel.setManaged(false);
        deltaLabel.setVisible(false);

        getChildren().setAll(header, valueRow, deltaLabel);

        accentColor.addListener(
                (o, ov, nv) -> refreshVisuals());
        iconGlyph.addListener(
                (o, ov, nv) -> refreshVisuals());
        dataAvailable.addListener(
                (o, ov, nv) -> refreshVisuals());
        refreshVisuals();
    }

    public void setDelta(double percent) {
        deltaLabel.setManaged(true);
        deltaLabel.setVisible(true);
        deltaLabel.getStyleClass().removeAll("delta-up", "delta-down");

        String arrow = percent >= 0 ? "↑" : "↓";
        deltaLabel.setText(arrow + " " + String.format("%+.0f", percent) + "% vs. moyenne");
        deltaLabel.getStyleClass().add(percent >= 0 ? "delta-up" : "delta-down");
    }

    public void setTitle(String v) { title.set(v); }
    public String getTitle() { return title.get(); }
    public void setIconGlyph(String v) { iconGlyph.set(v); }
    public String getIconGlyph() { return iconGlyph.get(); }
    public void setAccentColor(Color v) { accentColor.set(v); }
    public Color getAccentColor() { return accentColor.get(); }
    public void setValue(String v) { value.set(v); dataAvailable.set(true); }
    public void setValue(double v, int decimals) {
        value.set(String.format("%." + decimals + "f", v));
        dataAvailable.set(true);
    }
    public void setUnit(String u) { unit.set(u); }
    public void setNoData(String label) { value.set(label); unit.set(""); dataAvailable.set(false); }

    public StringProperty valueProperty() { return value; }
    public StringProperty unitProperty() { return unit; }
    public BooleanProperty dataAvailableProperty() { return dataAvailable; }

    // ================
    // PRIVATE API
    // ================

    private Region spacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }

    private void refreshVisuals(){

        Color accent = accentColor.get();
        iconBadge.setFill(accent.deriveColor(0, 1, 1, 0.15));

        String glyph = iconGlyph.get();
        if (glyph != null && !glyph.isBlank()) {
            try {
                iconNode.setIconLiteral(glyph);
                iconNode.setIconColor(accent);
            } catch (Exception e) {
            }
        }

        boolean hasData = dataAvailable.get();
        sparkBar.getChildren().clear();

        for (double relativeHeight : WAVEFORM_PATTERN) {

            double h = hasData ? relativeHeight * 22 : 4;
            Rectangle bar = new Rectangle(4, Math.max(3, h));
            bar.setArcWidth(3); bar.setArcHeight(3);
            bar.setFill(hasData ? accent : Color.web("#E4E5EA"));
            sparkBar.getChildren().add(bar);
        }
    }

}