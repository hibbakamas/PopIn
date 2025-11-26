package net.javaguids.popin.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import net.javaguids.popin.database.EventDAO;
import net.javaguids.popin.models.Event;
import net.javaguids.popin.models.User;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrganizerDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label statsLabel;

    // mini preview table
    @FXML private TableView<Event> myEventsTable;
    @FXML private TableColumn<Event, String> titleColumn;
    @FXML private TableColumn<Event, String> dateColumn;

    private User loggedInUser;
    private final EventDAO eventDAO = new EventDAO();
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;

        if (welcomeLabel != null && user != null) {
            welcomeLabel.setText("Welcome, organizer " + user.getUsername());
        }

        initTable();
        updateStats();
        loadMyEventsPreview();
    }

    private void initTable() {
        if (myEventsTable == null || titleColumn == null || dateColumn == null) return;

        titleColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTitle()));

        dateColumn.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getDateTime() != null
                                ? c.getValue().getDateTime().format(formatter)
                                : ""
                ));
    }

    private void updateStats() {
        if (statsLabel != null && loggedInUser != null) {
            int count = eventDAO.findByOrganizerId(loggedInUser.getId()).size();
            statsLabel.setText("You have created " + count + " event(s).");
        }
    }

    private void loadMyEventsPreview() {
        if (myEventsTable == null || loggedInUser == null) return;

        List<Event> events = eventDAO.findByOrganizerId(loggedInUser.getId());
        // show first few
        if (events.size() > 5) {
            events = events.subList(0, 5);
        }
        myEventsTable.getItems().setAll(events);
    }

    @FXML
    private void handleOpenCreateEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/net/javaguids/popin/views/create-event.fxml")
            );
            Parent root = loader.load();

            CreateEventController controller = loader.getController();
            controller.setLoggedInUser(loggedInUser);

            Stage stage = new Stage();
            stage.setTitle("Create Event");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMyEvents() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/net/javaguids/popin/views/my-events.fxml")
            );
            Parent root = loader.load();

            MyEventsController controller = loader.getController();
            controller.setLoggedInUser(loggedInUser);

            Stage stage = new Stage();
            stage.setTitle("My Events");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}