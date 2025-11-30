package net.javaguids.popin.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import net.javaguids.popin.database.EventDAO;
import net.javaguids.popin.models.Event;
import net.javaguids.popin.models.User;

public class AdminDashboardController {

    private User loggedInAdmin;

    // === TABLE PREVIEW IN DASHBOARD ===
    @FXML private TableView<Event> eventsTable;
    @FXML private TableColumn<Event, String> colTitle;
    @FXML private TableColumn<Event, String> colOrganizer;
    @FXML private TableColumn<Event, String> colDate;
    @FXML private TableColumn<Event, Number> colRegistrations;

    private final EventDAO eventDAO = new EventDAO();

    // Runs automatically when FXML loads
    @FXML
    public void initialize() {
        if (eventsTable == null) {
            System.out.println("⚠ eventsTable is NULL — preview table not loaded in this view");
            return;
        }
        System.out.println("Admin Dashboard → Initializing preview table");

        // Tell columns what to display
        colTitle.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));

        colOrganizer.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(data.getValue().getOrganizerId())
                ));

        colDate.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDateTime().toString()
                ));

        colRegistrations.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        eventDAO.countRegistered(data.getValue().getId())
                ));

        refreshPreview();
    }

    private void refreshPreview() {
        if (eventsTable != null) {
            System.out.println("Refreshing events preview...");
            eventsTable.getItems().setAll(eventDAO.findAll());
        }
    }

    // Called after login to inject admin user
    public void setLoggedInUser(User user) {
        this.loggedInAdmin = user;
        System.out.println("Logged in admin: " + user.getUsername());
        refreshPreview();
    }

    // BUTTON HANDLERS --------------

    @FXML
    private void handleViewAllEvents() {
        System.out.println("Admin clicked: View All Events");
        openScene("/net/javaguids/popin/views/admin-event-list.fxml", "All Events (Admin)");
    }

    @FXML
    private void handleViewAllUsers() {
        openScene("/net/javaguids/popin/views/user-list.fxml", "All Users");
    }

    @FXML
    private void handleAnalytics() {
        openScene("/net/javaguids/popin/views/analytics.fxml", "Analytics");
    }

    @FXML
    private void handleManageReports() {
        System.out.println("Admin clicked: Manage Flags / Reports");
        openScene("/net/javaguids/popin/views/reports-view.fxml", "Reported Events");
    }

    @FXML
    private void handleProfile() {
        if (loggedInAdmin == null) {
            System.err.println("No logged-in admin set for AdminDashboardController.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/net/javaguids/popin/views/profile.fxml"));
            Parent root = loader.load();

            ProfileController controller = loader.getController();
            controller.setLoggedInUser(loggedInAdmin);

            Stage stage = new Stage();
            stage.setTitle("My Profile");
            stage.setScene(new Scene(root));
            stage.show();

            System.out.println("Opened: My Profile");
        } catch (Exception e) {
            System.err.println("❌ Error loading profile.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        openScene("/net/javaguids/popin/views/login.fxml", "PopIn Login");
    }

    // OPEN NEW WINDOWS ----------------
    private void openScene(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
            System.out.println("Opened: " + title);
        } catch (Exception e) {
            System.err.println("❌ Error loading scene: " + fxml);
            e.printStackTrace();
        }
    }
}