package net.javaguids.popin.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.javaguids.popin.models.User;

public class AdminDashboardController {

    private User loggedInAdmin;

    public void setLoggedInUser(User user) {
        this.loggedInAdmin = user;
    }

    @FXML
    private void handleViewEvents() {
        // TODO: open a global event list for admin
        openScene("/net/javaguids/popin/views/event-list.fxml", "All Events");
    }

    @FXML
    private void handleViewUsers() {
        // NEW: open the admin user list screen
        openScene("/net/javaguids/popin/views/user-list.fxml", "All Users");
    }

    @FXML
    private void handleViewAnalytics() {
        System.out.println("OPEN ANALYTICS");
        // later: open analytics.fxml
    }

    @FXML
    private void handleViewFlags() {
        System.out.println("OPEN FLAGGED EVENTS");
        // later: open flags view
    }

    @FXML
    private void handleLogout() {
        openScene("/net/javaguids/popin/views/login.fxml", "PopIn Login");
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