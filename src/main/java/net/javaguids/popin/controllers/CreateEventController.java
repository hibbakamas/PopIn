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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CreateEventController {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private TextField venueField;
    @FXML private TextField capacityField;
    @FXML private TextField priceField;

    private final EventService eventService = new EventService();
    private User loggedInUser;
    private Event eventToEdit = null;

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public void setEventToEdit(Event event) {
        this.eventToEdit = event;

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
            String title = safeTrim(titleField.getText());
            String description = safeTrim(descriptionArea.getText());
            String venue = safeTrim(venueField.getText());

            if (title.isEmpty()) {
                showError("Title cannot be blank.");
                return;
            }
            if (venue.isEmpty()) {
                showError("Venue cannot be blank.");
                return;
            }

            int capacity;
            try {
                capacity = Integer.parseInt(safeTrim(capacityField.getText()));
                if (capacity <= 0) {
                    showError("Capacity must be a positive whole number.");
                    return;
                }
            } catch (NumberFormatException e) {
                showError("Capacity must be a whole number.");
                return;
            }

            if (datePicker.getValue() == null) {
                showError("Please select a date for your event.");
                return;
            }

            String timeText = safeTrim(timeField.getText());
            if (timeText.isEmpty()) {
                showError("Please enter a time in the format HH:mm.");
                return;
            }

            LocalTime time;
            try {
                time = LocalTime.parse(timeText, TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                showError("Time must be in format HH:mm.");
                return;
            }

            LocalDateTime dateTime = datePicker.getValue().atTime(time);

            Double price = null;
            String priceText = safeTrim(priceField.getText());
            if (!priceText.isEmpty()) {
                try {
                    price = Double.parseDouble(priceText);
                    if (price < 0) {
                        showError("Price must be a positive number.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    showError("Price must be a number.");
                    return;
                }
            }

            if (loggedInUser == null) {
                showError("Logged-in organizer information is missing.");
                return;
            }

            int organizerId = loggedInUser.getId();
            boolean success;

            if (eventToEdit == null) {
                success = eventService.createEvent(title, description, dateTime, venue, capacity, organizerId, price);
            } else {
                Event updated = new Event(eventToEdit.getId(), title, description, dateTime, venue, capacity, eventToEdit.getOrganizerId());
                success = eventService.updateEvent(updated, price);
            }

            if (success) {
                showSuccess(eventToEdit == null ? "Event created successfully!" : "Event updated successfully!");
                closeWindow();
            } else {
                showError("Failed to save event. Please try again.");
            }

        } catch (Exception e) {
            showError("An unexpected error occurred while saving the event.");
        }
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Invalid event details");
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