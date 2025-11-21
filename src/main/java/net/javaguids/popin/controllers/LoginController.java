package net.javaguids.popin.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.javaguids.popin.models.User;
import net.javaguids.popin.services.AuthService;

import java.io.IOException;
import java.util.Optional;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        Optional<User> userOpt = authService.login(username, password);

        if (userOpt.isEmpty()) {
            showError("Invalid username or password.");
            return;
        }

        User user = userOpt.get();
        String roleName = user.getRole().getName();

        try {
            switch (roleName) {
                case "ADMIN" -> openAdminDashboard(user);
                case "ORGANIZER" -> openOrganizerDashboard(user);
                case "ATTENDEE" -> openAttendeeDashboard(user);
                default -> showError("Unknown role: " + roleName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not open dashboard.");
        }
    }

    private void openAdminDashboard(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/net/javaguids/popin/views/admin-dashboard.fxml"));
        Parent root = loader.load();

        AdminDashboardController controller = loader.getController();
        controller.setLoggedInUser(user);

        Stage stage = new Stage();
        stage.setTitle("PopIn – Admin");
        stage.setScene(new Scene(root));
        stage.show();

        closeCurrentWindow();
    }

    private void openOrganizerDashboard(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/net/javaguids/popin/views/organizer-dashboard.fxml"));
        Parent root = loader.load();

        OrganizerDashboardController controller = loader.getController();
        controller.setLoggedInUser(user);

        Stage stage = new Stage();
        stage.setTitle("PopIn – Organizer");
        stage.setScene(new Scene(root));
        stage.show();

        closeCurrentWindow();
    }

    private void openAttendeeDashboard(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/net/javaguids/popin/views/attendee-dashboard.fxml"));
        Parent root = loader.load();

        AttendeeDashboardController controller = loader.getController();
        controller.setLoggedInUser(user);

        Stage stage = new Stage();
        stage.setTitle("PopIn – Attendee");
        stage.setScene(new Scene(root));
        stage.show();

        closeCurrentWindow();
    }

    private void closeCurrentWindow() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Login error");
        alert.setContentText(msg);
        alert.show();
    }

    @FXML
    private void goToSignUp() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/net/javaguids/popin/views/sign-up.fxml")
            );

            // Load FIRST so we know it works
            Scene scene = new Scene(loader.load());

            // Now we can close the current stage
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.close();

            // Open signup window
            Stage signupStage = new Stage();
            signupStage.setScene(scene);
            signupStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
