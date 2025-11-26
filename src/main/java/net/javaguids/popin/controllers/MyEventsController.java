package net.javaguids.popin.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.javaguids.popin.database.AttendanceDAO;
import net.javaguids.popin.database.EventDAO;
import net.javaguids.popin.models.Event;
import net.javaguids.popin.models.User;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class MyEventsController {

    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, String> titleColumn;
    @FXML private TableColumn<Event, String> dateColumn;
    @FXML private TableColumn<Event, String> venueColumn;
    @FXML private TableColumn<Event, Number> capacityColumn;
    @FXML private TableColumn<Event, Number> goingColumn;

    private final EventDAO eventDAO = new EventDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private User loggedInUser;

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        initColumns();
        loadEvents();
    }

    private void initColumns() {
        if (titleColumn == null) return;

        titleColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTitle()));

        dateColumn.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getDateTime() != null
                                ? c.getValue().getDateTime().format(formatter)
                                : ""
                ));

        venueColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getVenue()));

        capacityColumn.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().getCapacity()));

        goingColumn.setCellValueFactory(c ->
                new SimpleIntegerProperty(
                        attendanceDAO.countGoingByEventId(c.getValue().getId())
                ));
    }

    private void loadEvents() {
        if (loggedInUser == null) return;
        List<Event> events = eventDAO.findByOrganizerId(loggedInUser.getId());
        eventTable.getItems().setAll(events);
    }

    @FXML
    private void handleCancelEvent() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No selection",
                    "Select an event to cancel.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Cancel event?");
        confirm.setContentText("Are you sure you want to cancel: " + selected.getTitle() + "?");
        confirm.showAndWait().ifPresent(result -> {
            if (result.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                boolean deleted = eventDAO.deleteEvent(selected.getId());
                if (deleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Cancelled",
                            "Event was cancelled.");
                    loadEvents();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error",
                            "Could not cancel event.");
                }
            }
        });
    }

    @FXML
    private void handleViewGuestList() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No selection",
                    "Select an event to view guests.");
            return;
        }

        // later: open a guest-list window. For now just stub:
        int goingCount = attendanceDAO.countGoingByEventId(selected.getId());
        showAlert(Alert.AlertType.INFORMATION,
                "Guest List",
                "Number of users marked as 'Going': " + goingCount);
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) eventTable.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String header, String msg) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}