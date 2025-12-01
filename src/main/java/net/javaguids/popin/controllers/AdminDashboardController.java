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

public class AdminDashboardController implements DashboardController {

    private User loggedInAdmin;

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
        System.out.println("Admin Dashboard → Initializing preview table");

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

    @Override
    public void setLoggedInUser(User user) {
        this.loggedInAdmin = user;
        System.out.println("Logged in admin: " + user.getUsername());
        refreshPreview();
    }

    @FXML
    private void handleViewAllEvents() {
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
        openScene("/net/javaguids/popin/views/reports-view.fxml", "Reported Events");
    }

    @FXML
    private void handleProfile() {
        if (loggedInAdmin == null) return;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/net/javaguids/popin/views/login.fxml"));
            Parent root = loader.load();

            // Reuse same Stage instead of opening a new one
            Stage stage = (Stage) eventsTable.getScene().getWindow();
            stage.setTitle("PopIn – Login");
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openScene(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}