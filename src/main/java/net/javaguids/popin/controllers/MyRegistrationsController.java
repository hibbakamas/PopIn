package net.javaguids.popin.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.javaguids.popin.database.RegistrationDAO;
import net.javaguids.popin.models.Event;
import net.javaguids.popin.models.User;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class MyRegistrationsController {

    @FXML private TableView<Event> registrationTable;
    @FXML private TableColumn<Event, String> titleColumn;
    @FXML private TableColumn<Event, String> dateColumn;
    @FXML private TableColumn<Event, String> venueColumn;

    private final RegistrationDAO registrationDAO = new RegistrationDAO();
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private User loggedInUser;

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;

        initColumns();
        loadRegistrations();
    }

    private void initColumns() {
        titleColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTitle()));

        dateColumn.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getDateTime().format(formatter)
                ));

        venueColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getVenue()));
    }

    private void loadRegistrations() {
        if (loggedInUser == null) return;

        List<Event> events = registrationDAO.findByUserId(loggedInUser.getId());
        registrationTable.getItems().setAll(events);
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) registrationTable.getScene().getWindow();
        stage.close();
    }
}
