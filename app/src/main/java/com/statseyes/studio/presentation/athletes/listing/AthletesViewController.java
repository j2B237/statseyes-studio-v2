package com.statseyes.studio.presentation.athletes.listing;

import com.statseyes.studio.domain.model.Athlete;
import com.statseyes.studio.presentation.template.TemplateViewController;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import org.springframework.stereotype.Controller;
import java.util.List;

@Controller
public class AthletesViewController {

    @FXML private ListView<Athlete> athleteListView;
    @FXML private Label errorLabel;

    private final AthletesViewModel viewModel;
    private final TemplateViewController templateViewController;

    // Champ, pas lambda locale : controleur singleton, initialize() rappele
    // a chaque retour sur cette vue -- meme precaution qu'ailleurs.
    /*private final ChangeListener<List<Athlete>> athletesListener =
            (obs, oldValue, newValue) ->
                    athleteListView.setItems(FXCollections.observableArrayList(newValue));*/

    private final ChangeListener<String> errorListener = (obs, oldValue, newValue) -> {
        boolean hasError = newValue != null && !newValue.isBlank();
        errorLabel.setText(newValue);
        errorLabel.setVisible(hasError);
        errorLabel.setManaged(hasError);
    };


    public AthletesViewController(AthletesViewModel viewModel, TemplateViewController templateViewController) {
        this.viewModel = viewModel;
        this.templateViewController = templateViewController;
    }

    public void initialize() {

        athleteListView.setCellFactory(list -> new AthleteRowCell());
        bindViewModel();
        viewModel.load();
    }

    @FXML
    protected void onAddAthlete() {
        // TODO : formulaire d'ajout, pas encore construit
    }

    // ===============
    // PRIVATE API
    // ===============

    private void bindViewModel(){
        //viewModel.athletesProperty().removeListener(athletesListener);
        //viewModel.athletesProperty().addListener(athletesListener);
        athleteListView.setItems(viewModel.athletesProperty());
        viewModel.errorMessageProperty().removeListener(errorListener);
        viewModel.errorMessageProperty().addListener(errorListener);
    }

    private final class AthleteRowCell extends ListCell<Athlete> {
        private final Label nameLabel = new Label();
        private final Button viewButton = new Button("Voir");
        private final HBox content = new HBox(12, nameLabel, viewButton);

        private AthleteRowCell() {
            content.getStyleClass().add("athlete-row");
            HBox.setHgrow(nameLabel, Priority.ALWAYS);

            viewButton.setOnAction(e -> {
                Athlete athlete = getItem();
                if (athlete != null) {
                    templateViewController.loadAthleteDetailsView(athlete.id());
                }
            });
        }

        @Override
        protected void updateItem(Athlete athlete, boolean empty) {
            super.updateItem(athlete, empty);

            if (empty || athlete == null) { setGraphic(null); return; }
            nameLabel.setText(athlete.firstname() + " " + athlete.lastname());
            setGraphic(content);
        }
    }
}