package net.javaguids.popin.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.javaguids.popin.models.Event;
import net.javaguids.popin.services.EventService;

import java.io.IOException;
import java.util.List;

public class EventListController {

    @FXML private ListView<Event> eventListView;
    @FXML private TextField searchField;

    private final EventService eventService = new EventService();

    // Holds ALL events so we can filter them
    private List<Event> fullEventList;

    @FXML
    public void initialize() {
        loadEvents();

        // Live search listener
        searchField.textProperty().addListener((obs, oldV, newV) -> {
            filterEvents(newV);
        });
    }

    /** Load full event list */
    private void loadEvents() {
        try {
            fullEventList = eventService.getUpcomingEvents();

            ObservableList<Event> observableList = FXCollections.observableArrayList(fullEventList);
            eventListView.setItems(observableList);

            // Cell formatting
            eventListView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Event event, boolean empty) {
                    super.updateItem(event, empty);
                    if (empty || event == null) {
                        setText(null);
                    } else {
                        setText(event.getTitle() + " — " + event.getDateTime());
                    }
                }
            });

            // Double‑click to open details
            eventListView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    Event selected = eventListView.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        openEventDetails(selected);
                    }
                }
            });

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    /** Filter events live by title */
    private void filterEvents(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            eventListView.setItems(FXCollections.observableArrayList(fullEventList));
            return;
        }

        String lower = keyword.toLowerCase();

        List<Event> filtered = fullEventList.stream()
                .filter(event -> event.getTitle().toLowerCase().contains(lower))
                .toList();

        eventListView.setItems(FXCollections.observableArrayList(filtered));
    }

    /** Open event details window */
    private void openEventDetails(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/net/javaguids/popin/views/event-details.fxml"
            ));
            Parent root = loader.load();

            EventDetailsController controller = loader.getController();
            controller.setEvent(event);

            Stage stage = new Stage();
            stage.setTitle("Event Details");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not open event details.");
        }
    }

    /** Error display */
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error");
        alert.setContentText(msg);
        alert.show();
    }
}
