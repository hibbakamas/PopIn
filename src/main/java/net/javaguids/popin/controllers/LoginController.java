package net.javaguids.popin.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import net.javaguids.popin.models.User;
import net.javaguids.popin.services.AuthService;
import net.javaguids.popin.utils.SceneManager;

import java.util.Optional;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void initialize() {
        if (errorLabel != null) {
            errorLabel.setText("");
            errorLabel.setVisible(false);
        }
    }

    @FXML
    private void handleLogin() {
        // Reset inline errors
        if (errorLabel != null) {
            errorLabel.setText("");
            errorLabel.setVisible(false);
        }

        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.isBlank()) {
            showInlineError("Username cannot be empty.");
            return;
        }

        if (password == null || password.isBlank()) {
            showInlineError("Password cannot be empty.");
            return;
        }

        Optional<User> userOpt = authService.login(username, password);
        if (userOpt.isEmpty()) {
            showInlineError("Invalid username or password.");
            return;
        }

        User user = userOpt.get();
        String role = user.getRole().getName();

        switch (role) {
            case "ADMIN" -> openAdminDashboard(user);
            case "ORGANIZER" -> openOrganizerDashboard(user);
            case "ATTENDEE" -> openAttendeeDashboard(user);
            default -> showInlineError("Unknown role: " + role);
        }
    }

    // ------------------------------
    // DASHBOARD OPENING (via SceneManager)
    // ------------------------------

    private void openAdminDashboard(User user) {
        AdminDashboardController controller =
                SceneManager.switchTo("adminDashboard", "PopIn – Admin");
        controller.setLoggedInUser(user);   // pass user
    }

    private void openOrganizerDashboard(User user) {
        OrganizerDashboardController controller =
                SceneManager.switchTo("organizerDashboard", "PopIn – Organizer");
        controller.setLoggedInUser(user);
    }

    private void openAttendeeDashboard(User user) {
        AttendeeDashboardController controller =
                SceneManager.switchTo("attendeeDashboard", "PopIn – Attendee");
        controller.setLoggedInUser(user);
    }

    // ------------------------------
    // SIGN UP
    // ------------------------------
    @FXML
    private void goToSignUp() {
        SceneManager.switchTo("signup", "Create Account");
    }

    // ------------------------------
    // ERROR HELPERS
    // ------------------------------

    private void showInlineError(String msg) {
        if (errorLabel != null) {
            errorLabel.setText(msg);
            errorLabel.setVisible(true);
        } else {
            showAlert(msg);
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Login error");
        alert.setContentText(msg);
        alert.show();
    }
}
