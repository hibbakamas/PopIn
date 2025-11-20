package net.javaguids.popin.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import net.javaguids.popin.models.User;

public class AttendeeDashboardController {

    @FXML private Label welcomeLabel;

    private User loggedInUser;

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + user.getUsername());
        }
    }
}
