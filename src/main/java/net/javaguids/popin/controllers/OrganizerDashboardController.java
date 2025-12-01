package net.javaguids.popin.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import net.javaguids.popin.database.EventDAO;
import net.javaguids.popin.models.Event;
import net.javaguids.popin.models.User;
import net.javaguids.popin.utils.SceneManager;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrganizerDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label statsLabel;

    @FXML private TableView<Event> myEventsTable;
    @FXML private TableColumn<Event, String> titleColumn;
    @FXML private TableColumn<Event, String> dateColumn;

    private User loggedInUser;

    private final EventDAO eventDAO = new EventDAO();
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ------------------------------
    // SETUP
    // ------------------------------

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;

        if (welcomeLabel != null && user != null) {
            welcomeLabel.setText("Welcome, " + user.getUsername());
        }

        initTable();
        updateStats();
        loadMyEventsPreview();
    }

    private void initTable() {
        if (myEventsTable == null) return;

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
        if (events.size() > 5) {
            events = events.subList(0, 5);
        }

        myEventsTable.getItems().setAll(events);
    }

    // ------------------------------
    // BUTTON HANDLERS (SceneManager)
    // ------------------------------

    @FXML
    private void handleOpenCreateEvent() {
        var controller = SceneManager.switchTo("createEvent", "Create Event");
        if (controller != null) {
            try {
                controller.getClass()
                        .getMethod("setLoggedInUser", User.class)
                        .invoke(controller, loggedInUser);
            } catch (Exception ignored) {}
        }
    }

    @FXML
    private void handleMyEvents() {
        var controller = SceneManager.switchTo("myEvents", "My Events");
        if (controller != null) {
            try {
                controller.getClass()
                        .getMethod("setLoggedInUser", User.class)
                        .invoke(controller, loggedInUser);
            } catch (Exception ignored) {}
        }
    }

    @FXML
    private void handleProfile() {
        var controller = SceneManager.switchTo("profile", "My Profile");
        if (controller != null) {
            try {
                controller.getClass()
                        .getMethod("setLoggedInUser", User.class)
                        .invoke(controller, loggedInUser);
            } catch (Exception ignored) {}
        }
    }

    @FXML
    private void handleLogout() {
        SceneManager.switchTo("login", "PopIn – Login");
    }
}