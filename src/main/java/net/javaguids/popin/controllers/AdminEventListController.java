package net.javaguids.popin.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import net.javaguids.popin.database.EventDAO;
import net.javaguids.popin.models.Event;
import net.javaguids.popin.utils.SceneManager;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminEventListController {

    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, Number> idColumn;
    @FXML private TableColumn<Event, String> titleColumn;
    @FXML private TableColumn<Event, String> dateColumn;
    @FXML private TableColumn<Event, String> venueColumn;
    @FXML private TableColumn<Event, Number> capacityColumn;
    @FXML private TableColumn<Event, Number> organizerIdColumn;

    private final EventDAO eventDAO = new EventDAO();
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {

        if (eventTable == null ||
                idColumn == null ||
                titleColumn == null ||
                dateColumn == null ||
                venueColumn == null ||
                capacityColumn == null ||
                organizerIdColumn == null) {
            return;
        }

        idColumn.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().getId()));

        titleColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTitle()));

        dateColumn.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getDateTime() != null
                                ? c.getValue().getDateTime().format(formatter)
                                : ""
                )
        );

        venueColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getVenue()));

        capacityColumn.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().getCapacity()));

        organizerIdColumn.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().getOrganizerId()));

        loadEvents();
    }

    private void loadEvents() {
        List<Event> events = eventDAO.findAll();
        eventTable.getItems().setAll(events);
    }

    @FXML
    private void handleDeleteEvent() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING,
                    "No selection",
                    "Please select an event to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Event");
        confirm.setHeaderText("Confirm delete");
        confirm.setContentText("Delete event: \"" + selected.getTitle() + "\"?");

        confirm.showAndWait().ifPresent(result -> {
            if (result.getButtonData() == ButtonBar.ButtonData.OK_DONE) {

                boolean deleted = eventDAO.deleteEvent(selected.getId());
                if (deleted) {
                    showAlert(Alert.AlertType.INFORMATION,
                            "Deleted",
                            "Event deleted successfully.");
                    loadEvents();
                } else {
                    showAlert(Alert.AlertType.ERROR,
                            "Error",
                            "Could not delete the event.");
                }
            }
        });
    }

    @FXML
    private void handleClose() {
        // MUST include a title
        SceneManager.switchTo("adminDashboard", "Admin Dashboard");
    }

    private void showAlert(Alert.AlertType type, String header, String msg) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
