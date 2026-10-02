package com.statseyes.studio.presentation.component;

import com.statseyes.studio.domain.config.ApplicationConfiguration;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Objects;

public class AthleteCard extends HBox {

    // ===================
    // INSTANCE VARIABLES
    // ===================

    private static final double PHOTO_SIZE = 64;
    private final StringProperty athleteName   = new SimpleStringProperty();
    private final StringProperty teamName      = new SimpleStringProperty();
    private final StringProperty positionName  = new SimpleStringProperty();
    private final StringProperty photoUrl      = new SimpleStringProperty();

    private final StackPane photoArea = new StackPane();
    private final Pane stripedBackground = new Pane();
    private final Label initialsLabel = new Label();
    private final ImageView photoView = new ImageView();

    private final Label nameLabel = new Label();
    private final Text teamText = new Text();
    private final Text positionText = new Text();

    private Runnable onViewDetails;

    // ===================
    // PUBLIC API
    // ==================

    public AthleteCard(){

        getStyleClass().add("athlete-card");
        getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource(
                                ApplicationConfiguration.CSS_PATH.getValue() +
                                        "component/athlete-card.css"
                        )
                ).toExternalForm()
        );

        setSpacing(16);
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(12));

        buildPhoto();
        buildText();

        Button detailsButton = new Button("Voir détails  ›");
        detailsButton.getStyleClass().add("primary-button");
        detailsButton.setOnAction(e -> { if (onViewDetails != null) onViewDetails.run(); });

        TextFlow subtitleFlow = new TextFlow(teamText, new Text(" · "), positionText);
        VBox textArea = new VBox(4, nameLabel, subtitleFlow);

        textArea.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textArea, Priority.ALWAYS);

        getChildren().addAll(photoArea, textArea, detailsButton);

        photoUrl.addListener((o, ov, nv) -> refreshPhoto());
        athleteName.addListener((o, ov, nv) -> refreshInitials());

    }

    public void setAthleteName(String v)  { athleteName.set(v); }
    public void setTeamName(String v)     { teamName.set(v == null ? "Sans equipe" : v); }
    public void setPositionName(String v) { positionName.set(v == null ? "Sans poste" : v); }
    public void setPhotoUrl(String v)     { photoUrl.set(v); }
    public void setOnViewDetails(Runnable callback) { this.onViewDetails = callback; }

    // ================
    // PRIVATE API
    // ================

    private void buildPhoto() {

        photoArea.getStyleClass().add("athlete-card-photo");
        photoArea.setPrefSize(PHOTO_SIZE, PHOTO_SIZE);
        photoArea.setMinSize(PHOTO_SIZE, PHOTO_SIZE);
        photoArea.setMaxSize(PHOTO_SIZE, PHOTO_SIZE);
        photoArea.setClip(new Rectangle(PHOTO_SIZE, PHOTO_SIZE));

        stripedBackground.getStyleClass().add("athlete-card-photo-placeholder");
        stripedBackground.setPrefSize(PHOTO_SIZE, PHOTO_SIZE);

        initialsLabel.getStyleClass().add("athlete-card-initials");

        photoView.setFitWidth(PHOTO_SIZE);
        photoView.setFitHeight(PHOTO_SIZE);
        photoView.setPreserveRatio(false);
        photoView.setVisible(false);

        photoArea.getChildren().addAll(stripedBackground, initialsLabel, photoView);
    }

    private void buildText() {

        nameLabel.getStyleClass().add("athlete-card-name");
        nameLabel.textProperty().bind(athleteName);
        teamText.getStyleClass().add("athlete-card-team");
        positionText.getStyleClass().add("athlete-card-subtitle");
        teamText.textProperty().bind(teamName);
        positionText.textProperty().bind(positionName);
    }

    private void refreshPhoto() {

        boolean hasPhoto = photoUrl.get() != null && !photoUrl.get().isBlank();
        photoView.setVisible(hasPhoto);

        stripedBackground.setVisible(!hasPhoto);
        initialsLabel.setVisible(!hasPhoto);

        if (hasPhoto) {
            photoView.setImage(
                    new Image(
                            photoUrl.get(),
                            PHOTO_SIZE,
                            PHOTO_SIZE,
                            false,
                            true,
                            true
                    )
            );
        }
    }

    private void refreshInitials() {

        String name = athleteName.get();
        if (name == null || name.isBlank()) { initialsLabel.setText(""); return; }

        StringBuilder initials = new StringBuilder();
        for (String part : name.trim().split("\\s+")) {

            if (!part.isEmpty()) initials.append(Character.toUpperCase(part.charAt(0)));
            if (initials.length() >= 2) break;
        }

        initialsLabel.setText(initials.toString());
    }
}
