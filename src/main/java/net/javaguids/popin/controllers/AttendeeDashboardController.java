package net.javaguids.popin.controllers;

import javafx.fxml.FXML;
import net.javaguids.popin.models.User;
import net.javaguids.popin.utils.SceneManager;

public class AttendeeDashboardController {

    private User loggedInUser;

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    @FXML
    private void handleBrowseEvents() {
        if (loggedInUser == null) {
            System.err.println("No logged-in user set for AttendeeDashboardController.");
            return;
        }

        EventListController controller =
                SceneManager.switchTo("eventList", "Available Events");

        if (controller != null) {
            controller.setLoggedInUser(loggedInUser);
        }
    }

    @FXML
    private void handleMyRegistrations() {
        if (loggedInUser == null) {
            System.err.println("No logged-in user set for AttendeeDashboardController.");
            return;
        }

        MyRegistrationsController controller =
                SceneManager.switchTo("myRegistrations", "My Registrations");

        if (controller != null) {
            controller.setLoggedInUser(loggedInUser);
        }
    }

    @FXML
    private void handleProfile() {
        if (loggedInUser == null) {
            System.err.println("No logged-in user set for AttendeeDashboardController.");
            return;
        }

        ProfileController controller =
                SceneManager.switchTo("profile", "My Profile");

        if (controller != null) {
            controller.setLoggedInUser(loggedInUser);
        }
    }

    @FXML
    private void handleLogout() {
        SceneManager.switchTo("login", "PopIn – Login");
    }
}