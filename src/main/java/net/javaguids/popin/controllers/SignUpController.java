package net.javaguids.popin.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.javaguids.popin.services.AuthService;

public class SignUpController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label errorLabel;
    @FXML private Button signUpButton;   // 🔹 new: we’ll control disabled state

    private final AuthService authService = new AuthService();

    private static final int MIN_PASSWORD_LENGTH = 6;

    @FXML
    public void initialize() {
        if (errorLabel != null) {
            errorLabel.setText("");
            errorLabel.setVisible(false);
        }

        // If roles are not set via FXML, you could do:
        // if (roleComboBox.getItems().isEmpty()) {
        //     roleComboBox.getItems().addAll("attendee", "organizer", "admin");
        // }

        // 🔹 disable sign up until form is valid
        if (signUpButton != null) {
            signUpButton.setDisable(true);
        }

        // 🔹 live validation on changes
        usernameField.textProperty().addListener((obs, oldV, newV) -> validateForm());
        passwordField.textProperty().addListener((obs, oldV, newV) -> validateForm());
        roleComboBox.valueProperty().addListener((obs, oldV, newV) -> validateForm());
    }

    private void validateForm() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        String error = null;

        if (username == null || username.isBlank()) {
            error = "Username cannot be empty.";
        } else if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            error = "Password must be at least " + MIN_PASSWORD_LENGTH + " characters.";
        } else if (role == null || role.isBlank()) {
            error = "Please select a role.";
        }

        if (error != null) {
            showInlineError(error);
            if (signUpButton != null) {
                signUpButton.setDisable(true);
            }
        } else {
            clearInlineError();
            if (signUpButton != null) {
                signUpButton.setDisable(false);
            }
        }
    }

    @FXML
    private void handleSignUp() {
        // Run validation again before submitting
        validateForm();
        if (signUpButton != null && signUpButton.isDisabled()) {
            return; // form invalid, don’t submit
        }

        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        boolean success = authService.registerUser(username, password, role);

        if (!success) {
            showInlineError("Invalid fields or user already exists.");
            return;
        }

        // success → go back to login
        goToLogin();
    }

    private void showInlineError(String msg) {
        if (errorLabel != null) {
            errorLabel.setText(msg);
            errorLabel.setVisible(true);
        }
    }

    private void clearInlineError() {
        if (errorLabel != null) {
            errorLabel.setText("");
            errorLabel.setVisible(false);
        }
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