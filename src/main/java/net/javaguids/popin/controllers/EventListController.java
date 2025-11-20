package net.javaguids.popin.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import net.javaguids.popin.models.Event;
import net.javaguids.popin.services.EventService;

import java.io.IOException;
import java.util.List;

public class EventListController {

    @FXML
    private ListView<Event> eventListView;

    private final EventService eventService = new EventService();

    @FXML
    public void initialize() {
        loadUpcomingEvents();
    }

    private void loadUpcomingEvents() {
        try {
            List<Event> events = eventService.getUpcomingEvents();
            ObservableList<Event> observableList = FXCollections.observableArrayList(events);
            eventListView.setItems(observableList);

            // For display purposes: show title instead of Event.toString()
            eventListView.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
                @Override
                protected void updateItem(Event event, boolean empty) {
                    super.updateItem(event, empty);
                    if (empty || event == null) {
                        setText(null);
                    } else {
                        setText(event.getTitle() + " — " + event.getDateTime().toString());
                    }
                }
            });

            // Open details when clicked
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

    private void openEventDetails(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/net/javaguids/popin/views/event-details.fxml"));
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

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error");
        alert.setContentText(msg);
        alert.show();
    }
}
