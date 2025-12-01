package net.javaguids.popin.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import net.javaguids.popin.database.EventDAO;
import net.javaguids.popin.models.Event;
import net.javaguids.popin.models.User;
import net.javaguids.popin.utils.SceneManager;

public class AdminDashboardController {

    private User loggedInAdmin;

    // === TABLE PREVIEW IN DASHBOARD ===
    @FXML private TableView<Event> eventsTable;
    @FXML private TableColumn<Event, String> colTitle;
    @FXML private TableColumn<Event, String> colOrganizer;
    @FXML private TableColumn<Event, String> colDate;
    @FXML private TableColumn<Event, Number> colRegistrations;

    private final EventDAO eventDAO = new EventDAO();

    @FXML
    public void initialize() {

        if (eventsTable == null) {
            System.out.println("⚠ eventsTable is NULL — preview table not loaded in this view");
            return;
        }

        colTitle.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTitle())
        );

        colOrganizer.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getOrganizerId()))
        );

        colDate.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDateTime().toString())
        );

        colRegistrations.setCellValueFactory(data ->
                new SimpleIntegerProperty(eventDAO.countRegistered(data.getValue().getId()))
        );

        refreshPreview();
    }

    private void refreshPreview() {
        if (eventsTable != null) {
            eventsTable.getItems().setAll(eventDAO.findAll());
        }
    }

    // Injected after login
    public void setLoggedInUser(User user) {
        this.loggedInAdmin = user;
        refreshPreview();
    }

    // ---------------- BUTTON HANDLERS ---------------- //

    @FXML
    private void handleViewAllEvents() {
        SceneManager.switchTo("adminEventList", "All Events (Admin)");
    }

    @FXML
    private void handleViewAllUsers() {
        SceneManager.switchTo("userList", "All Users");
    }

    @FXML
    private void handleAnalytics() {
        SceneManager.switchTo("analytics", "Analytics");
    }

    @FXML
    private void handleManageReports() {
        SceneManager.switchTo("reports", "Reported Events");
    }

    @FXML
    private void handleProfile() {
        var controller = SceneManager.switchTo("profile", "My Profile");

        if (controller != null) {
            try {
                controller.getClass()
                        .getMethod("setLoggedInUser", User.class)
                        .invoke(controller, loggedInAdmin);
            } catch (Exception ignored) {}
        }
    }

    @FXML
    private void handleLogout() {
        SceneManager.switchTo("login", "PopIn – Login");
    }
}