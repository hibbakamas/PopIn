package net.javaguids.popin.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import net.javaguids.popin.database.RegistrationDAO;
import net.javaguids.popin.models.Event;
import net.javaguids.popin.models.User;
import net.javaguids.popin.utils.SceneManager;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class MyRegistrationsController {

    @FXML
    private TableView<Event> registrationTable;

    @FXML
    private TableColumn<Event, String> titleColumn;

    @FXML
    private TableColumn<Event, String> dateColumn;

    @FXML
    private TableColumn<Event, String> venueColumn;

    private final RegistrationDAO registrationDAO = new RegistrationDAO();
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private User loggedInUser;

    // Called by the dashboard after login
    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        initColumns();
        loadRegistrations();
    }

    private void initColumns() {
        titleColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTitle()));

        dateColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDateTime().format(formatter)));

        venueColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getVenue()));
    }

    private void loadRegistrations() {
        if (loggedInUser == null) return;
        List<Event> events = registrationDAO.findByUserId(loggedInUser.getId());
        registrationTable.getItems().setAll(events);
    }

    // 🔄 Replaced close-window logic with SceneManager
    @FXML
    private void handleClose() {
        // Attendee returns to their dashboard
        var controller = SceneManager.switchTo("attendeeDashboard", "PopIn – Attendee Dashboard");
        if (controller instanceof AttendeeDashboardController adc) {
            adc.setLoggedInUser(loggedInUser);
        }
    }
}
