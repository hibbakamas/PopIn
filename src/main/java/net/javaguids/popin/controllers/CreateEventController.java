package net.javaguids.popin.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.javaguids.popin.models.Event;
import net.javaguids.popin.models.PaidEvent;
import net.javaguids.popin.models.User;
import net.javaguids.popin.services.EventService;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class CreateEventController {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField; // expects "HH:mm"
    @FXML private TextField venueField;
    @FXML private TextField capacityField;
    @FXML private TextField priceField; // optional

    private final EventService eventService = new EventService();

    private User loggedInUser;      // organizer
    private Event eventToEdit = null; // if non-null → edit mode

    // Called by parent controller after login
    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    // Called by MyEventsController when editing an existing event
    public void setEventToEdit(Event event) {
        this.eventToEdit = event;

        // Prefill fields
        titleField.setText(event.getTitle());
        descriptionArea.setText(event.getDescription());
        venueField.setText(event.getVenue());
        capacityField.setText(String.valueOf(event.getCapacity()));

        if (event.getDateTime() != null) {
            datePicker.setValue(event.getDateTime().toLocalDate());
            LocalTime t = event.getDateTime().toLocalTime();
            timeField.setText(String.format("%02d:%02d", t.getHour(), t.getMinute()));
        }

        if (event instanceof PaidEvent paidEvent) {
            priceField.setText(String.valueOf(paidEvent.getPrice()));
        } else {
            priceField.clear();
        }
    }

    @FXML
    private void handleCreateEvent() {
        try {
            String title = titleField.getText();
            String description = descriptionArea.getText();
            String venue = venueField.getText();

            // Capacity
            int capacity = Integer.parseInt(capacityField.getText());

            // Date + Time parsing
            if (datePicker.getValue() == null || timeField.getText().isBlank()) {
                showError("Please select a date and enter a valid time (HH:mm).");
                return;
            }

            LocalTime time = LocalTime.parse(timeField.getText()); // HH:mm
            LocalDateTime dateTime = datePicker.getValue().atTime(time);

            // Price (optional)
            Double price = null;
            if (!priceField.getText().isBlank()) {
                price = Double.parseDouble(priceField.getText());
            }

            if (loggedInUser == null) {
                showError("Logged-in organizer information is missing.");
                return;
            }

            int organizerId = loggedInUser.getId();

            boolean success;

            if (eventToEdit == null) {
                // CREATE NEW EVENT
                success = eventService.createEvent(
                        title,
                        description,
                        dateTime,
                        venue,
                        capacity,
                        organizerId,
                        price
                );
            } else {
                // UPDATE EXISTING EVENT
                Event updated = new Event(
                        eventToEdit.getId(),
                        title,
                        description,
                        dateTime,
                        venue,
                        capacity,
                        eventToEdit.getOrganizerId()
                );

                success = eventService.updateEvent(updated, price);
            }

            if (success) {
                showSuccess(eventToEdit == null
                        ? "Event created successfully!"
                        : "Event updated successfully!");
                closeWindow();
            } else {
                showError("Failed to save event. Please try again.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error: " + e.getMessage());
        }
    }

    // UI HELPERS
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