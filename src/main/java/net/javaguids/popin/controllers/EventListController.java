package net.javaguids.popin.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import net.javaguids.popin.database.ReportDAO;
import net.javaguids.popin.models.Event;
import net.javaguids.popin.models.User;
import net.javaguids.popin.services.EventService;
import net.javaguids.popin.services.RegistrationService;

import java.util.List;

public class EventListController {

    @FXML private ListView<Event> eventListView;
    @FXML private TextField searchField;
    @FXML private Button registerButton;

    // NEW:
    @FXML private Button reportButton;

    private final EventService eventService = new EventService();
    private final RegistrationService registrationService = new RegistrationService();
    private final ReportDAO reportDAO = new ReportDAO(); // NEW

    // Holds ALL events so we can filter them
    private List<Event> fullEventList;

    // Logged‑in attendee (set from AttendeeDashboardController)
    private User loggedInUser;

    // Called from AttendeeDashboardController
    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        updateRegisterButtonState();
        updateReportButtonState();
    }

    @FXML
    public void initialize() {
        loadEvents();

        // Live search listener
        searchField.textProperty().addListener((obs, oldV, newV) -> {
            filterEvents(newV);
        });

        // Cell formatting
        eventListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);
                if (empty || event == null) {
                    setText(null);
                } else {
                    setText(event.getTitle() + " — " + event.getDateTime());
                }
            }
        });

        // Selection change → update buttons
        eventListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldEvent, newEvent) -> {
                    updateRegisterButtonState();
                    updateReportButtonState();
                }
        );
    }

    /** Load full event list */
    private void loadEvents() {
        try {
            fullEventList = eventService.getUpcomingEvents();
            ObservableList<Event> observableList =
                    FXCollections.observableArrayList(fullEventList);
            eventListView.setItems(observableList);
        } catch (Exception e) {
            showError("Could not load events: " + e.getMessage());
        }
    }

    /** Filter events live by title */
    private void filterEvents(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            eventListView.setItems(FXCollections.observableArrayList(fullEventList));
            return;
        }
        String lower = keyword.toLowerCase();
        List<Event> filtered = fullEventList.stream()
                .filter(event -> event.getTitle().toLowerCase().contains(lower))
                .toList();
        eventListView.setItems(FXCollections.observableArrayList(filtered));
    }

    // =========================
    // REGISTER / UNREGISTER BTN
    // =========================
    private void updateRegisterButtonState() {
        Event selected = eventListView.getSelectionModel().getSelectedItem();

        if (loggedInUser == null) {
            registerButton.setText("Login required");
            registerButton.setDisable(true);
            return;
        }

        if (selected == null) {
            registerButton.setText("Select an event");
            registerButton.setDisable(true);
            return;
        }

        boolean isRegistered =
                registrationService.isUserRegistered(selected.getId(), loggedInUser.getId());
        boolean isFull =
                registrationService.isEventFull(selected.getId());

        if (isRegistered) {
            registerButton.setText("Unregister");
            registerButton.setDisable(false);
        } else if (isFull) {
            registerButton.setText("Event full");
            registerButton.setDisable(true);
        } else {
            registerButton.setText("Register");
            registerButton.setDisable(false);
        }
    }

    @FXML
    private void handleToggleRegistration() {
        Event selected = eventListView.getSelectionModel().getSelectedItem();
        if (selected == null || loggedInUser == null) {
            return;
        }

        boolean isRegistered =
                registrationService.isUserRegistered(selected.getId(), loggedInUser.getId());

        try {
            if (isRegistered) {
                registrationService.cancelRegistration(selected.getId(), loggedInUser.getId());
                showInfo("You have been unregistered from \"" + selected.getTitle() + "\".");
            } else {
                registrationService.registerUser(selected.getId(), loggedInUser.getId());
                showInfo("You are registered for \"" + selected.getTitle() + "\".");
            }
        } catch (Exception e) {
            showError(e.getMessage());
        }

        // Refresh button label+state after action
        updateRegisterButtonState();
    }

    // =========================
    // REPORT EVENT BUTTON
    // =========================
    private void updateReportButtonState() {
        if (reportButton == null) return;

        Event selected = eventListView.getSelectionModel().getSelectedItem();

        // No user or no event selected → disable
        if (loggedInUser == null || selected == null) {
            reportButton.setDisable(true);
            return;
        }

        // Only attendees can report (organizers/admins shouldn't)
        String roleName = loggedInUser.getRole().getName();
        if (!"ATTENDEE".equalsIgnoreCase(roleName)) {
            reportButton.setDisable(true);
            return;
        }

        // If they've already reported, we could disable it too (optional)
        boolean alreadyReported = reportDAO.hasUserReported(selected.getId(), loggedInUser.getId());
        reportButton.setDisable(alreadyReported);
    }

    @FXML
    private void handleReportEvent() {
        Event selected = eventListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Please select an event to report.");
            return;
        }

        if (loggedInUser == null) {
            showError("You must be logged in to report an event.");
            return;
        }

        String roleName = loggedInUser.getRole().getName();
        if (!"ATTENDEE".equalsIgnoreCase(roleName)) {
            showError("Only attendees can report events.");
            return;
        }

        if (reportDAO.hasUserReported(selected.getId(), loggedInUser.getId())) {
            showError("You have already reported this event.");
            updateReportButtonState();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Report Event");
        confirm.setContentText("Are you sure you want to report \"" + selected.getTitle() + "\"?");
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                boolean ok = reportDAO.addReport(selected.getId(), loggedInUser.getId());
                if (ok) {
                    showInfo("Event reported successfully.");
                } else {
                    showError("Could not report event. Please try again.");
                }
                updateReportButtonState();
            }
        });
    }

    // =========================
    // ALERT HELPERS
    // =========================
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error");
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