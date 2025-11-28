package net.javaguids.popin.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import net.javaguids.popin.database.EventDAO;
import net.javaguids.popin.database.RegistrationDAO;
import net.javaguids.popin.database.UserDAO;

public class AnalyticsController {

    @FXML private Label totalUsersLabel;
    @FXML private Label totalEventsLabel;
    @FXML private Label totalRegistrationsLabel;

    private final UserDAO userDAO = new UserDAO();
    private final EventDAO eventDAO = new EventDAO();
    private final RegistrationDAO registrationDAO = new RegistrationDAO();

    @FXML
    public void initialize() {
        loadStats();
    }

    private void loadStats() {
        int userCount = userDAO.listAll().size();
        int eventCount = eventDAO.findAll().size();
        int registrationCount = registrationDAO.listAll().size();

        totalUsersLabel.setText("Total users: " + userCount);
        totalEventsLabel.setText("Total events: " + eventCount);
        totalRegistrationsLabel.setText("Total registrations: " + registrationCount);
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) totalUsersLabel.getScene().getWindow();
        stage.close();
    }
}
