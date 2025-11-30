package net.javaguids.popin.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.javaguids.popin.database.UserDAO;
import net.javaguids.popin.models.User;
import net.javaguids.popin.utils.PasswordHasher;

import java.util.Optional;

public class ProfileController {

    @FXML private Label usernameLabel;
    @FXML private Label roleLabel;
    @FXML private Label emailStatusLabel;

    @FXML private TextField newUsernameField;

    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML private CheckBox emailNotificationsCheck;
    @FXML private VBox emailSection; // whole email card to hide/show

    private final UserDAO userDAO = new UserDAO();
    private User loggedInUser;

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;

        if (user != null) {
            String roleName = user.getRole().getName();
            usernameLabel.setText("Username: " + user.getUsername());
            roleLabel.setText("Role: " + roleName);

            boolean isAttendee = "ATTENDEE".equalsIgnoreCase(roleName);

            // Show/hide entire email card for attendees only
            if (emailSection != null) {
                emailSection.setVisible(isAttendee);
                emailSection.setManaged(isAttendee);
            }

            // Show/hide the read-only email info line in Account Information
            if (emailStatusLabel != null) {
                if (isAttendee) {
                    emailStatusLabel.setVisible(true);
                    emailStatusLabel.setManaged(true);

                    boolean enabled = userDAO.getEmailNotifications(user.getId());
                    emailNotificationsCheck.setSelected(enabled);
                    emailStatusLabel.setText(
                            "Email notifications: " + (enabled ? "Enabled" : "Disabled")
                    );
                } else {
                    // For non-attendees: no email info line at all
                    emailStatusLabel.setVisible(false);
                    emailStatusLabel.setManaged(false);
                }
            }
        }
    }

    // -------------------- USERNAME --------------------
    @FXML
    private void handleSaveUsername() {
        if (loggedInUser == null) {
            showError("No logged-in user found.");
            return;
        }

        String newUsername = newUsernameField.getText();
        if (newUsername == null || newUsername.isBlank()) {
            showError("New username cannot be empty.");
            return;
        }
        if (newUsername.equals(loggedInUser.getUsername())) {
            showError("New username must be different from the current one.");
            return;
        }

        Optional<User> existingOpt = userDAO.findByUsername(newUsername);
        if (existingOpt.isPresent()) {
            showError("This username is already taken.");
            return;
        }

        boolean updated = userDAO.updateUsername(loggedInUser.getId(), newUsername);
        if (!updated) {
            showError("Could not update username. Please try again.");
            return;
        }

        loggedInUser.setUsername(newUsername);
        usernameLabel.setText("Username: " + newUsername);
        newUsernameField.clear();

        showInfo("Username updated successfully.");
    }

    // -------------------- PASSWORD --------------------
    @FXML
    private void handleSavePassword() {
        if (loggedInUser == null) {
            showError("No logged-in user found.");
            return;
        }

        String oldPw = oldPasswordField.getText();
        String newPw = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();

        if (oldPw == null || oldPw.isBlank()
                || newPw == null || newPw.isBlank()
                || confirm == null || confirm.isBlank()) {
            showError("All password fields are required.");
            return;
        }

        if (newPw.equals(oldPw)) {
            showError("New password must be different from the old one.");
            return;
        }

        if (!newPw.equals(confirm)) {
            showError("New password and confirmation do not match.");
            return;
        }

        if (newPw.length() < 6) {
            showError("New password must be at least 6 characters long.");
            return;
        }

        String storedHash = loggedInUser.getPasswordHash();
        if (!PasswordHasher.matchPassword(oldPw, storedHash)) {
            showError("Old password is incorrect.");
            return;
        }

        String newHash = PasswordHasher.hashPassword(newPw);
        boolean updated = userDAO.updatePassword(loggedInUser.getId(), newHash);
        if (!updated) {
            showError("Could not update password. Please try again.");
            return;
        }

        loggedInUser.setPasswordHash(newHash);

        oldPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();

        showInfo("Password updated successfully.");
    }

    // -------------------- EMAIL PREFERENCES (ATTENDEE only) --------------------
    @FXML
    private void handleSavePreferences() {
        if (loggedInUser == null) {
            showError("No logged-in user found.");
            return;
        }

        String roleName = loggedInUser.getRole().getName();
        if (!"ATTENDEE".equalsIgnoreCase(roleName)) {
            // should normally be hidden, but just in case
            showError("Email notifications are only available for attendees.");
            return;
        }

        boolean enabled = emailNotificationsCheck.isSelected();
        boolean updated = userDAO.updateEmailNotifications(loggedInUser.getId(), enabled);
        if (!updated) {
            showError("Could not update email preference. Please try again.");
            return;
        }

        // Update label (only visible for attendees anyway)
        if (emailStatusLabel != null) {
            emailStatusLabel.setText(
                    "Email notifications: " + (enabled ? "Enabled" : "Disabled")
            );
        }

        showInfo("Email notification preferences saved.");
    }

    // -------------------- HELPERS --------------------
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Info");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) usernameLabel.getScene().getWindow();
        stage.close();
    }
}