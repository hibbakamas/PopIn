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
    @FXML private TextField timeField; // expects "HH:mm"
    @FXML private TextField venueField;
    @FXML private TextField capacityField;
    @FXML private TextField priceField; // optional

    private final EventService eventService = new EventService();
    private User loggedInUser;      // organizer
    private Event eventToEdit = null; // if non-null → edit mode

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

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
            // ---------- BASIC TEXT FIELDS ----------
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

            // ---------- CAPACITY ----------
            int capacity;
            try {
                String capacityText = safeTrim(capacityField.getText());
                capacity = Integer.parseInt(capacityText);
                if (capacity <= 0) {
                    showError("Capacity must be a positive whole number.");
                    return;
                }
            } catch (NumberFormatException e) {
                showError("Capacity must be a whole number.");
                return;
            }

            // ---------- DATE & TIME ----------
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
                time = LocalTime.parse(timeText, TIME_FORMATTER); // HH:mm
            } catch (DateTimeParseException e) {
                showError("Time must be in format HH:mm.");
                return;
            }

            LocalDateTime dateTime = datePicker.getValue().atTime(time);

            // ---------- PRICE (OPTIONAL) ----------
            Double price = null;
            String priceText = safeTrim(priceField.getText());
            if (!priceText.isEmpty()) {
                try {
                    price = Double.parseDouble(priceText);
                    if (price < 0) {
                        showError("Price must be a positive number, or leave it blank for a free event.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    showError("Price must be a number, or leave it blank for free.");
                    return;
                }
            }

            // ---------- ORGANIZER ----------
            if (loggedInUser == null) {
                showError("Logged-in organizer information is missing.");
                return;
            }
            int organizerId = loggedInUser.getId();

            // ---------- CREATE vs UPDATE ----------
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
            // No stack trace in UI; keep it friendly
            // (you can log e somewhere if you want, but not required)
            showError("An unexpected error occurred while saving the event.");
        }
    }

    // UI HELPERS

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