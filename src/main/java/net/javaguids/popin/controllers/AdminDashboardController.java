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
        System.out.println("Logged in admin: " + user.getUsername());
    }

    @FXML
    private void handleViewEvents() {
        System.out.println("Admin clicked: View All Events");
        openScene("/net/javaguids/popin/views/admin-event-list.fxml",
                "All Events (Admin)");
    }


    @FXML
    private void handleViewUsers() {
        System.out.println("Admin clicked: View All Users");
        // this expects you already created user-list.fxml + controller
        openScene("/net/javaguids/popin/views/user-list.fxml",
                "All Users");
    }

    @FXML
    private void handleViewAnalytics() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/net/javaguids/popin/views/analytics.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Analytics");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleViewFlags() {
        System.out.println("Admin clicked: Manage Flags / Reports");
        // later: open flags/Reports view
    }

    @FXML
    private void handleLogout() {
        System.out.println("Admin clicked: Logout");
        openScene("/net/javaguids/popin/views/login.fxml", "PopIn Login");
    }

    private void openScene(String fxml, String title) {
        try {
            System.out.println("Trying to load FXML: " + fxml);
            var url = getClass().getResource(fxml);
            System.out.println("Resolved URL = " + url);

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            System.err.println("Error while opening scene: " + fxml);
            e.printStackTrace();
        }
    }
}
