package net.javaguids.popin.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import net.javaguids.popin.models.User;

import java.io.IOException;

public class OrganizerDashboardController {

    @FXML private Label welcomeLabel;

    private User loggedInUser;

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, organizer " + user.getUsername());
        }
    }

    @FXML
    private void handleOpenCreateEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/net/javaguids/popin/views/create-event.fxml"));
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
}
