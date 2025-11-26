package net.javaguids.popin.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.javaguids.popin.models.User;
import net.javaguids.popin.services.EventService;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class CreateEventController {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;        // expects "HH:mm"
    @FXML private TextField venueField;
    @FXML private TextField capacityField;
    @FXML private TextField priceField;       // optional

    private final EventService eventService = new EventService();
    private User loggedInUser; // set from OrganizerDashboardController

    // Called by OrganizerDashboardController after loading FXML
    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    @FXML
    private void handleCreateEvent() {
        try {
            String title = titleField.getText();
            String description = descriptionArea.getText();
            String venue = venueField.getText();

            // Capacity
            int capacity = Integer.parseInt(capacityField.getText());

            // Date + Time
            if (datePicker.getValue() == null || timeField.getText().isBlank()) {
                showError("Please select a date and enter a valid time (HH:mm).");
                return;
            }

            LocalTime time = LocalTime.parse(timeField.getText()); // "HH:mm"
            LocalDateTime dateTime = datePicker.getValue().atTime(time);

            // Price (optional)
            Double price = null;
            if (!priceField.getText().isBlank()) {
                price = Double.parseDouble(priceField.getText());
            }

            if (loggedInUser == null) {
                showError("Logged-in user is missing (organizerId).");
                return;
            }

            int organizerId = loggedInUser.getId();

            boolean success = eventService.createEvent(
                    title,
                    description,
                    dateTime,
                    venue,
                    capacity,
                    organizerId,
                    price
            );

            if (success) {
                showSuccess("Event created successfully!");
                closeWindow();
            } else {
                showError("Failed to create event. Please try again.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error: " + e.getMessage());
        }
    }

    // UI helpers
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

    private void closeWindow() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }
}