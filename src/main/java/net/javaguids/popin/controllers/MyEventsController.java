package net.javaguids.popin.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.javaguids.popin.database.EventDAO;
import net.javaguids.popin.database.RegistrationDAO;
import net.javaguids.popin.models.Event;
import net.javaguids.popin.models.User;
import net.javaguids.popin.services.EventService;

import java.io.IOException;
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
    private final RegistrationDAO registrationDAO = new RegistrationDAO();
    private final EventService eventService = new EventService();

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

        // how many registered for each event
        goingColumn.setCellValueFactory(c ->
                new SimpleIntegerProperty(
                        registrationDAO.countRegistered(c.getValue().getId())
                ));
    }

    private void loadEvents() {
        if (loggedInUser == null) return;
        List<Event> events = eventDAO.findByOrganizerId(loggedInUser.getId());
        eventTable.getItems().setAll(events);
    }

    // -------- EDIT --------
    @FXML
    private void handleEditEvent() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No selection",
                    "Select an event to edit.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/net/javaguids/popin/views/create-event.fxml"));
            Parent root = loader.load();

            CreateEventController controller = loader.getController();
            controller.setLoggedInUser(loggedInUser);
            controller.setEventToEdit(selected);

            Stage stage = new Stage();
            stage.setTitle("Edit Event");
            Scene scene = new Scene(root);

            try {
                scene.getStylesheets().add(
                        getClass().getResource("/net/javaguids/popin/styles/global.css")
                                .toExternalForm()
                );
            } catch (Exception ignored) {}

            stage.setScene(scene);
            stage.show();

            stage.setOnHiding(e -> loadEvents());

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Could not open edit window.");
        }
    }

    // -------- DELETE --------
    @FXML
    private void handleDeleteEvent() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No selection",
                    "Select an event to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Delete event?");
        confirm.setContentText("Are you sure you want to delete: " + selected.getTitle() + "?");
        confirm.showAndWait().ifPresent(result -> {
            if (result.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                boolean deleted = eventService.deleteEvent(selected.getId());
                if (deleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Deleted",
                            "Event was deleted.");
                    loadEvents();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error",
                            "Could not delete event.");
                }
            }
        });
    }

    // -------- VIEW ATTENDEES (Organizer) --------
    @FXML
    private void handleViewAttendees() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No selection",
                    "Select an event to view its attendees.");
            return;
        }

        int goingCount = registrationDAO.countRegistered(selected.getId());
        if (goingCount == 0) {
            showAlert(Alert.AlertType.INFORMATION,
                    "No Attendees",
                    "No one has registered for this event yet.\n(0 attendees)");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/net/javaguids/popin/views/attendee-list.fxml"));
            Parent root = loader.load();

            AttendeeListController controller = loader.getController();
            controller.setEvent(selected);

            Stage stage = new Stage();
            stage.setTitle("Attendees – " + selected.getTitle());
            Scene scene = new Scene(root);

            try {
                scene.getStylesheets().add(
                        getClass().getResource("/net/javaguids/popin/styles/global.css")
                                .toExternalForm()
                );
            } catch (Exception ignored) {}

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Could not open attendee list.");
        }
    }

    // -------- VIEW GUEST COUNT --------
    @FXML
    private void handleViewGuestList() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No selection",
                    "Select an event to view guests.");
            return;
        }

        int goingCount = registrationDAO.countRegistered(selected.getId());
        showAlert(Alert.AlertType.INFORMATION,
                "Guest List",
                "Number of users registered: " + goingCount);
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