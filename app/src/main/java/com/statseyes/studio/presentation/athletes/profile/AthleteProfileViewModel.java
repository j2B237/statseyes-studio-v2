package com.statseyes.studio.presentation.athletes.profile;


import com.statseyes.studio.domain.model.Availability;
import com.statseyes.studio.domain.model.AvailabilityStatus;
import com.statseyes.studio.domain.model.DominantFoot;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class AthleteProfileViewModel {

    // =====================
    // INSTANCE VARIABLES
    // ====================

    private static final Locale FR = Locale.FRANCE;
    private static final DateTimeFormatter LONG = DateTimeFormatter.ofPattern("d MMMM yyyy", FR);
    private static final DateTimeFormatter SHORT = DateTimeFormatter.ofPattern("dd MMM yyyy", FR);

    // =====================
    // PROPERTIES
    // =====================

    private final ObjectProperty<DominantFoot> dominantFoot = new SimpleObjectProperty<>(DominantFoot.RIGHT);
    private final StringProperty mainPosition = new SimpleStringProperty("");
    private final ListProperty<String> secondaryPositions =
            new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<Availability> availibilty = new SimpleObjectProperty<>(Availability.available());

    // Textes dérivés, prêts pour l'affichage
    private final StringProperty bannerText     = new SimpleStringProperty("");
    private final StringProperty statusLabel    = new SimpleStringProperty("Disponible");
    private final StringProperty statusStyle    = new SimpleStringProperty("status-available");
    private final StringProperty reasonText     = new SimpleStringProperty("—");
    private final StringProperty sinceText      = new SimpleStringProperty("—");
    private final StringProperty returnText     = new SimpleStringProperty("—");
    private final BooleanProperty bannerVisible = new SimpleBooleanProperty(false);

    public void setAvailability(Availability value) {
        LocalDate today = LocalDate.now();
        Availability a = (value == null ? Availability.available() : value).effectiveOn(today);
        availibilty.set(a);

        statusLabel.set(switch (a.status()){
            case AVAILABLE -> "Disponible";
            case INJURED -> "Blesse";
            case  UNAVAILABLE -> "Indisponible";
        });

        statusStyle.set(switch (a.status()) {
            case AVAILABLE -> "status-available";
            case INJURED -> "status-injured";
            case UNAVAILABLE -> "status-unavailable";
        });

        reasonText.set(a.reason() == null || a.reason().isBlank() ? "-" : a.reason());
        sinceText.set(a.since() == null ? "-" : a.since().format(SHORT));

        Long days = a.daysUntilReturn(today);
        returnText.set(a.expectedReturn() == null ? "-" :
                a.expectedReturn().format(SHORT) + ", dans " + days + (days > 1 ? " jours" : " jour"));

        bannerVisible.set(!a.isAvailable());
        bannerText.set(a.isAvailable() ? "" : buildBanner(a, days));
    }
    public void reset(){
        dominantFoot.set(DominantFoot.RIGHT);
        mainPosition.set("");
        secondaryPositions.clear();
        setAvailability(Availability.available());
    }

    ///////////////////////////////
    /// getters de propriétés  ///
    /////////////////////////////

    public ObjectProperty<DominantFoot> dominantFootProperty(){
        return dominantFoot;
    }
    public StringProperty mainPositionProperty(){
        return mainPosition;
    }
    public ListProperty<String> secondaryPositionsProperty(){
        return secondaryPositions;
    }
    public ObjectProperty<Availability> availabilityProperty(){
        return availibilty;
    }
    public StringProperty bannerTextProperty(){
        return bannerText;
    }
    public StringProperty statusLabelProperty(){
        return statusLabel;
    }
    public StringProperty statusStyleProperty(){
        return statusStyle;
    }
    public StringProperty reasonTextProperty(){
        return reasonText;
    }
    public StringProperty sinceTextProperty(){
        return sinceText;
    }
    public StringProperty returnTextProperty(){
        return returnText;
    }
    public BooleanProperty bannerVisibleProperty(){
        return bannerVisible;
    }

    // ========================
    // PRIVATE API
    // ========================

    private String buildBanner (Availability a, Long days) {
        String head = (a.status() == AvailabilityStatus.INJURED ? "Blessé" : "Indisponible")
                + (a.reason() == null || a.reason().isBlank() ? "" : " : " + a.reason()) + ". ";
        return a.expectedReturn() == null ? head + "Date de retour inconnue."
                : head + "Retour prévu le " + a.expectedReturn().format(LONG)
                  + " (dans " + days + (days > 1 ? " jours)." : " jour).");
    }


}
