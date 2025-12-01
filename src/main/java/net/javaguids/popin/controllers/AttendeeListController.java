package net.javaguids.popin.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import net.javaguids.popin.database.RegistrationDAO;
import net.javaguids.popin.database.UserDAO;
import net.javaguids.popin.models.Event;
import net.javaguids.popin.models.Registration;
import net.javaguids.popin.models.User;

import java.util.ArrayList;
import java.util.List;

public class AttendeeListController {

    @FXML
    private ListView<String> attendeeListView;

    private final RegistrationDAO registrationDAO = new RegistrationDAO();
    private final UserDAO userDAO = new UserDAO();

    private Event event;

    /** Called right after switching scenes */
    public void setEvent(Event event) {
        this.event = event;
        loadAttendees();
    }

    private void loadAttendees() {
        if (event == null) return;

        try {
            List<Registration> allRegs = registrationDAO.listAll();
            List<String> usernames = new ArrayList<>();

            for (Registration reg : allRegs) {

                if (reg.getEventId() != event.getId())
                    continue;

                String status = reg.getStatus();
                if (!"REGISTERED".equalsIgnoreCase(status)
                        && !"CHECKED_IN".equalsIgnoreCase(status)) {
                    continue;
                }

                User u = userDAO.findById(reg.getUserId());
                if (u != null) {
                    usernames.add(u.getUsername());
                }
            }

            ObservableList<String> list =
                    FXCollections.observableArrayList(usernames);
            attendeeListView.setItems(list);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not load attendees: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error");
        alert.setContentText(msg);
        alert.show();
    }
}
