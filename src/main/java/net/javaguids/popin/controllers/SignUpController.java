package net.javaguids.popin.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.javaguids.popin.services.AuthService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;


public class SignUpController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        errorLabel.setText("");

        // Optional: if you want a default selected role
        // roleComboBox.getSelectionModel().select("regular");
    }

    @FXML
    private void handleSignUp() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        if (role == null || role.isBlank()) {
            errorLabel.setText("Please select a role.");
            return;
        }

        boolean success = authService.registerUser(username, password, role);

        if (!success) {
            errorLabel.setText("Invalid fields or user already exists.");
            return;
        }

        goToLogin();
    }

    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/net/javaguids/popin/views/login.fxml")
            );

            Scene scene = new Scene(loader.load());

            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.close();

            Stage loginStage = new Stage();
            loginStage.setScene(scene);
            loginStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
