package net.javaguids.popin.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;

import net.javaguids.popin.models.Event;
import net.javaguids.popin.models.PaidEvent;
import net.javaguids.popin.services.RegistrationService;

public class EventDetailsController {

    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label dateLabel;
    @FXML private Label venueLabel;
    @FXML private Label capacityLabel;
    @FXML private Label priceLabel;

    @FXML private Button registerButton;   // if attendee
    @FXML private Button checkInButton;    // if organizer/admin
    @FXML private Button editButton;       // if organizer
    @FXML private Button deleteButton;     // if organizer or admin

    private Event event;
    private RegistrationService registrationService = new RegistrationService();

    // Called by EventListController
    public void setEvent(Event event) {
        this.event = event;
        loadEventDetails();
    }

    private void loadEventDetails() {
        if (event == null) return;

        titleLabel.setText(event.getTitle());
        descriptionLabel.setText(event.getDescription());
        dateLabel.setText(event.getDateTime().toString());
        venueLabel.setText(event.getVenue());
        capacityLabel.setText("Capacity: " + event.getCapacity());

        if (event instanceof PaidEvent paid) {
            priceLabel.setText("Price: €" + paid.getPrice());
        } else {
            priceLabel.setText("Free event");
        }

        // Optional: Hide buttons based on role
        // This requires passing loggedInUser to controller
    }

    // ------------------------------------
    // BUTTON ACTIONS
    // ------------------------------------
    @FXML
    private void handleRegister() {
        showInfo("Registration is not yet implemented (RegistrationService coming next).");
    }

    @FXML
    private void handleCheckIn() {
        showInfo("Check-in flow coming soon.");
    }

    @FXML
    private void handleEdit() {
        showInfo("Edit event feature not implemented yet.");
    }

    @FXML
    private void handleDelete() {
        showInfo("Delete event feature not implemented yet.");
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Info");
        alert.setContentText(msg);
        alert.show();
    }
}
