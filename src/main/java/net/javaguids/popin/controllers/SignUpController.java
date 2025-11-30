package net.javaguids.popin.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.javaguids.popin.services.AuthService;

public class SignUpController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label errorLabel;
    @FXML private Button signUpButton;

    @FXML private VBox adminCodeBox;
    @FXML private PasswordField adminCodeField;

    private final AuthService authService = new AuthService();

    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final String ADMIN_SECRET_CODE = "POPIN";

    @FXML
    public void initialize() {
        if (errorLabel != null) {
            errorLabel.setText("");
            errorLabel.setVisible(false);
        }

        if (signUpButton != null) {
            signUpButton.setDisable(true);
        }

        // start fully hidden + not managed (no gap)
        if (adminCodeBox != null) {
            adminCodeBox.setVisible(false);
            adminCodeBox.setManaged(false);
        }

        // show/hide admin access code based on role
        roleComboBox.valueProperty().addListener((obs, oldV, newV) -> {
            boolean isAdmin = "admin".equalsIgnoreCase(newV);
            if (adminCodeBox != null) {
                adminCodeBox.setVisible(isAdmin);
                adminCodeBox.setManaged(isAdmin);   // key: no layout gap for non-admin
            }

            // gently re-fit window to new content
            try {
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.sizeToScene();
            } catch (Exception ignored) {}

            validateForm();
        });

        usernameField.textProperty().addListener((obs, o, n) -> validateForm());
        passwordField.textProperty().addListener((obs, o, n) -> validateForm());
        roleComboBox.valueProperty().addListener((obs, o, n) -> validateForm());
        if (adminCodeField != null) {
            adminCodeField.textProperty().addListener((obs, o, n) -> validateForm());
        }
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
        } else if ("admin".equalsIgnoreCase(role)) {
            String code = adminCodeField != null ? adminCodeField.getText() : null;
            if (code == null || code.isBlank()) {
                error = "Admin access code is required for admin sign-up.";
            } else if (!ADMIN_SECRET_CODE.equals(code)) {
                error = "Invalid admin access code.";
            }
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
        validateForm();
        if (signUpButton != null && signUpButton.isDisabled()) {
            return;
        }

        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        // extra safety for admin
        if ("admin".equalsIgnoreCase(role)) {
            String code = adminCodeField != null ? adminCodeField.getText() : null;
            if (code == null || code.isBlank() || !ADMIN_SECRET_CODE.equals(code)) {
                showInlineError("Invalid admin access code.");
                if (signUpButton != null) {
                    signUpButton.setDisable(true);
                }
                return;
            }
        }

        boolean success = authService.registerUser(username, password, role);
        if (!success) {
            showInlineError("Invalid fields or user already exists.");
            return;
        }

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
                    getClass().getResource("/net/javaguids/popin/views/login.fxml"));
            Scene scene = new Scene(loader.load());

            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.close();

            Stage loginStage = new Stage();
            loginStage.setScene(scene);
            loginStage.sizeToScene();
            loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}