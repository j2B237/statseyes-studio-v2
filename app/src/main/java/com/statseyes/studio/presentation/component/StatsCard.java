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
import javafx.scene.text.FontWeight;

public class StatsCard extends VBox {

    // ===================
    // Properties
    // ==================

    private final StringProperty title      = new SimpleStringProperty();
    private final StringProperty value      = new SimpleStringProperty("--");
    private final StringProperty unit       = new SimpleStringProperty("");
    private final StringProperty iconGlyph  = new SimpleStringProperty("●");
    private final ObjectProperty<Color> accentColor = new SimpleObjectProperty<>(Color.web("#4F8EF7"));

    private final Circle iconBadge = new Circle(16);
    private final Label iconGlyphLabel = new Label();
    private final Label titleLabel = new Label();
    private final Label valueLabel = new Label();
    private final Label unitLabel = new Label();
    private final HBox sparkBar = new HBox(2);

    // =====================
    // Constructor for FXML
    // =====================

    public StatsCard(){

        getStyleClass().add("stats-card");
        setPadding(new Insets(16));
        setSpacing(10);
        setPrefWidth(170);
        setPrefHeight(150);

        iconGlyphLabel.setFont(Font.font(14));
        StackPane iconStack = new StackPane(iconBadge, iconGlyphLabel);

        titleLabel.textProperty().bind(title);
        titleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        titleLabel.setWrapText(true);

        HBox header = new HBox(10, iconStack, titleLabel);
        header.setAlignment(Pos.CENTER_LEFT);

        sparkBar.setAlignment(Pos.BOTTOM_LEFT);
        sparkBar.setPrefHeight(24);

        valueLabel.textProperty().bind(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 26));
        unitLabel.textProperty().bind(unit);
        unitLabel.setStyle("-fx-text-fill: #9AA0A6;");

        HBox valueRow = new HBox(valueLabel, unitLabel);
        valueRow.setAlignment(Pos.BASELINE_LEFT);

        getChildren().addAll(header, sparkBar, valueRow);

        // Rebuild l'icône + le spark dès que la couleur ou le glyph changent
        accentColor.addListener(
                (o, ov, nv) -> refreshVisuals());
        iconGlyph.addListener(
                (o, ov, nv) -> refreshVisuals());
        refreshVisuals();
    }

    private void refreshVisuals(){

        Color accent = accentColor.get();
        iconBadge.setFill(
                accent.deriveColor(0, 1, 1, 0.15)
        );

        iconGlyphLabel.setText(iconGlyph.get());
        iconGlyphLabel.setTextFill(accent);

        sparkBar.getChildren().clear();

        for (int i = 0; i < 10; i++) {
            Rectangle bar = new Rectangle(4, i == 0 ? 20 : 4);
            bar.setArcWidth(2); bar.setArcHeight(2);
            bar.setFill(i == 0 ? accent : Color.web("#E8E9ED"));
            sparkBar.getChildren().add(bar);
        }
    }

    // ====================
    // SETTERS
    // ====================

    // --- Setters exploitables en FXML (title="..." iconGlyph="..." accentColor="#FF5A5F") ---
    public void setTitle(String v) { title.set(v); }
    public String getTitle() { return title.get(); }

    public void setIconGlyph(String v) { iconGlyph.set(v); }
    public String getIconGlyph() { return iconGlyph.get(); }

    public void setAccentColor(Color v) { accentColor.set(v); }
    public Color getAccentColor() { return accentColor.get(); }

    public void setValue(String v) { value.set(v); }
    public void setValue(double v, int decimals) { value.set(String.format("%." + decimals + "f", v)); }
    public void setUnit(String u) { unit.set(u); }
    public void setNoData(String label) { value.set(label); unit.set(""); }

    public StringProperty valueProperty() { return value; }
    public StringProperty unitProperty() { return unit; }

}
