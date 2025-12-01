package net.javaguids.popin.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.javaguids.popin.services.AuthService;
import net.javaguids.popin.utils.SceneManager;

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

        if (signUpButton != null)
            signUpButton.setDisable(true);

        // Hide admin code section by default
        if (adminCodeBox != null) {
            adminCodeBox.setVisible(false);
            adminCodeBox.setManaged(false);
        }

        // Toggle admin code field depending on selected role
        roleComboBox.valueProperty().addListener((obs, oldV, newV) -> {
            boolean isAdmin = "admin".equalsIgnoreCase(newV);

            if (adminCodeBox != null) {
                adminCodeBox.setVisible(isAdmin);
                adminCodeBox.setManaged(isAdmin);
            }

            validateForm();
        });

        usernameField.textProperty().addListener((obs, o, n) -> validateForm());
        passwordField.textProperty().addListener((obs, o, n) -> validateForm());
        roleComboBox.valueProperty().addListener((obs, o, n) -> validateForm());

        if (adminCodeField != null)
            adminCodeField.textProperty().addListener((obs, o, n) -> validateForm());
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
            String code = adminCodeField.getText();
            if (code == null || code.isBlank()) {
                error = "Admin access code is required for admin sign-up.";
            } else if (!ADMIN_SECRET_CODE.equals(code)) {
                error = "Invalid admin access code.";
            }
        }

        if (error != null) {
            showInlineError(error);
            signUpButton.setDisable(true);
        } else {
            clearInlineError();
            signUpButton.setDisable(false);
        }
    }

    @FXML
    private void handleSignUp() {
        validateForm();
        if (signUpButton.isDisabled()) return;

        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        if ("admin".equalsIgnoreCase(role)) {
            String code = adminCodeField.getText();
            if (code == null || !ADMIN_SECRET_CODE.equals(code)) {
                showInlineError("Invalid admin access code.");
                signUpButton.setDisable(true);
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
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }

    private void clearInlineError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }

    @FXML
    private void goToLogin() {
        // Clean SceneManager navigation
        SceneManager.switchTo("login", "PopIn – Login");
    }
}
