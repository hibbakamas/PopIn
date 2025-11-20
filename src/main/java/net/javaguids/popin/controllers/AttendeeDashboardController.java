package net.javaguids.popin.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.javaguids.popin.models.User;

public class AttendeeDashboardController {

    private User loggedInUser;

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    @FXML
    private void handleBrowseEvents() {
        openScene("/net/javaguids/popin/views/event-list.fxml", "Available Events");
    }

    @FXML
    private void handleMyRegistrations() {
        // You can create a list showing only the user's events
        System.out.println("OPEN USER REGISTRATIONS");
    }

    @FXML
    private void handleProfile() {
        System.out.println("OPEN ATTENDEE PROFILE");
    }

    @FXML
    private void handleLogout() {
        openScene("/net/javaguids/popin/views/login.fxml", "Login");
    }

    private void openScene(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
