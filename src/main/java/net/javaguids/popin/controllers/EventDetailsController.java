package net.javaguids.popin.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import net.javaguids.popin.database.ReportDAO;
import net.javaguids.popin.models.Event;
import net.javaguids.popin.models.PaidEvent;
import net.javaguids.popin.models.User;
import net.javaguids.popin.services.EventService;
import net.javaguids.popin.services.RegistrationService;

public class EventDetailsController {

    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label dateLabel;
    @FXML private Label venueLabel;
    @FXML private Label priceLabel;
    @FXML private Label capacityLabel;
    @FXML private Button registerButton;
    @FXML private Button cancelButton;
    @FXML private Button checkInButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    // NEW:
    @FXML private Button reportButton;

    private Event event;
    private User loggedInUser;
    private final RegistrationService registrationService = new RegistrationService();
    private final EventService eventService = new EventService();
    private final ReportDAO reportDAO = new ReportDAO(); // NEW

    // Called from EventListController
    public void setEvent(Event event) {
        this.event = event;
        loadEventDetails();
    }

    // Called from dashboard after login
    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        updateButtonsForRole();
    }

    private void loadEventDetails() {
        titleLabel.setText(event.getTitle());
        descriptionLabel.setText(event.getDescription());
        venueLabel.setText(event.getVenue());
        dateLabel.setText(event.getDateTime().toString());
        capacityLabel.setText("Capacity: " + event.getCapacity());

        if (event instanceof PaidEvent paid) {
            priceLabel.setText("Price: €" + paid.getPrice());
        } else {
            priceLabel.setText("Free Event");
        }
    }

    // -----------------------------------------
    // ROLE-BASED BUTTON LOGIC
    // -----------------------------------------
    private void updateButtonsForRole() {
        if (loggedInUser == null) return;
        String role = loggedInUser.getRole().getName();

        switch (role) {
            case "ATTENDEE" -> setupAttendeeButtons();
            case "ORGANIZER" -> setupOrganizerButtons();
            case "ADMIN" -> setupAdminButtons();
        }
    }

    private void setupAttendeeButtons() {
        registerButton.setVisible(!registrationService.isEventFull(event.getId())
                && !registrationService.isUserRegistered(event.getId(), loggedInUser.getId()));
        cancelButton.setVisible(registrationService.isUserRegistered(event.getId(), loggedInUser.getId()));
        checkInButton.setVisible(false);
        editButton.setVisible(false);
        deleteButton.setVisible(false);

        // attendees see Report button
        if (reportButton != null) {
            reportButton.setVisible(true);
            reportButton.setManaged(true);
        }
    }

    private void setupOrganizerButtons() {
        registerButton.setVisible(false);
        cancelButton.setVisible(false);
        checkInButton.setVisible(true);
        editButton.setVisible(true);
        deleteButton.setVisible(true);

        if (reportButton != null) {
            reportButton.setVisible(false);
            reportButton.setManaged(false); // no gap
        }
    }

    private void setupAdminButtons() {
        registerButton.setVisible(false);
        cancelButton.setVisible(false);
        checkInButton.setVisible(true);
        editButton.setVisible(false);
        deleteButton.setVisible(true);

        if (reportButton != null) {
            reportButton.setVisible(false);
            reportButton.setManaged(false); // no gap
        }
    }

    // -----------------------------------------
    // BUTTON ACTIONS
    // -----------------------------------------
    @FXML private void handleRegister() {
        try {
            registrationService.registerUser(event.getId(), loggedInUser.getId());
            showSuccess("You are now registered!");
            updateButtonsForRole();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML private void handleCancelRegistration() {
        try {
            registrationService.cancelRegistration(event.getId(), loggedInUser.getId());
            showSuccess("Your registration has been cancelled.");
            updateButtonsForRole();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML private void handleCheckIn() {
        try {
            registrationService.checkInUser(event.getId(), loggedInUser.getId());
            showSuccess("User checked in successfully.");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML private void handleEditEvent() {
        showInfo("Edit event not implemented yet.");
    }

    @FXML private void handleDeleteEvent() {
        showInfo("Delete event not implemented yet.");
    }

    @FXML private void handleViewAttendees() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/net/javaguids/popin/views/attendee-list.fxml"));
            Parent root = loader.load();
            AttendeeListController controller = loader.getController();
            controller.setEvent(event);

            Stage stage = new Stage();
            stage.setTitle("Attendee List");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showError("Could not open attendee list: " + e.getMessage());
        }
    }

    // NEW: attendee reporting
    @FXML private void handleReportEvent() {
        if (loggedInUser == null || !"ATTENDEE".equalsIgnoreCase(loggedInUser.getRole().getName())) {
            showError("Only attendees can report events.");
            return;
        }

        if (reportDAO.hasUserReported(event.getId(), loggedInUser.getId())) {
            showError("You have already reported this event.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Report Event");
        confirm.setContentText("Are you sure you want to report this event?");
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                boolean ok = reportDAO.addReport(event.getId(), loggedInUser.getId());
                if (ok) {
                    showSuccess("Event reported successfully.");
                } else {
                    showError("Could not report event. Please try again.");
                }
            }
        });
    }

    // -----------------------------------------
    // UI UTILITIES
    // -----------------------------------------
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error");
        alert.setContentText(msg);
        alert.show();
    }

    private void showSuccess(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Success");
        alert.setContentText(msg);
        alert.show();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Info");
        alert.setContentText(msg);
        alert.show();
    }
}