package net.javaguids.popin.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import net.javaguids.popin.database.EventDAO;
import net.javaguids.popin.database.RegistrationDAO;
import net.javaguids.popin.database.UserDAO;
import net.javaguids.popin.models.Event;
import net.javaguids.popin.models.PaidEvent;
import net.javaguids.popin.models.User;
import net.javaguids.popin.utils.SceneManager;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnalyticsController {

    // basic totals
    @FXML private Label totalUsersLabel;
    @FXML private Label totalEventsLabel;
    @FXML private Label totalRegistrationsLabel;

    // extra stats
    @FXML private Label mostActiveOrganizerLabel;
    @FXML private Label avgPriceLabel;
    @FXML private Label mostPopularEventLabel;

    private final UserDAO userDAO = new UserDAO();
    private final EventDAO eventDAO = new EventDAO();
    private final RegistrationDAO registrationDAO = new RegistrationDAO();

    @FXML
    public void initialize() {
        loadStats();
    }

    private void loadStats() {
        // --------- BASIC TOTALS ---------
        int userCount = userDAO.countAll();              // from UserDAO
        int eventCount = eventDAO.countAll();            // from EventDAO
        int registrationCount = registrationDAO.listAll().size(); // from RegistrationDAO

        totalUsersLabel.setText("Total users: " + userCount);
        totalEventsLabel.setText("Total events: " + eventCount);
        totalRegistrationsLabel.setText("Total registrations: " + registrationCount);

        // --------- MOST ACTIVE ORGANIZER (by event count) ---------
        List<Event> allEvents = eventDAO.findAll();
        if (allEvents.isEmpty()) {
            mostActiveOrganizerLabel.setText("Most active organizer: no events yet.");
        } else {
            Map<Integer, Long> countByOrganizer = allEvents.stream()
                    .collect(Collectors.groupingBy(Event::getOrganizerId, Collectors.counting()));

            Map.Entry<Integer, Long> topEntry =
                    Collections.max(countByOrganizer.entrySet(), Map.Entry.comparingByValue());

            int organizerId = topEntry.getKey();
            long organizerEvents = topEntry.getValue();
            User organizer = userDAO.findById(organizerId);

            String name = (organizer != null)
                    ? organizer.getUsername()
                    : ("User #" + organizerId);

            mostActiveOrganizerLabel.setText(
                    "Most active organizer: " + name + " (" + organizerEvents + " event(s))"
            );
        }

        // --------- AVERAGE PRICE PER PAID EVENT ---------
        double avgPrice = allEvents.stream()
                .filter(e -> e instanceof PaidEvent)
                .mapToDouble(e -> ((PaidEvent) e).getPrice())
                .average()
                .orElse(0.0);

        if (avgPrice > 0) {
            avgPriceLabel.setText(
                    String.format("Average ticket price (paid events): €%.2f", avgPrice)
            );
        } else {
            avgPriceLabel.setText("Average ticket price: no paid events yet.");
        }

        // --------- MOST POPULAR UPCOMING EVENT (by registrations) ---------
        List<Event> upcoming = eventDAO.findAllUpcoming();
        Event mostPopular = null;
        int maxRegistrations = 0;

        for (Event e : upcoming) {
            int count = registrationDAO.countRegistered(e.getId());
            if (count > maxRegistrations) {
                maxRegistrations = count;
                mostPopular = e;
            }
        }

        if (mostPopular != null) {
            mostPopularEventLabel.setText(
                    "Most popular upcoming event: " +
                            mostPopular.getTitle() +
                            " (" + maxRegistrations + " registration(s))"
            );
        } else {
            mostPopularEventLabel.setText("Most popular upcoming event: none yet.");
        }
    }

    @FXML
    private void handleClose() {
        // Instead of closing the Stage, navigate back via SceneManager
        SceneManager.switchTo("adminDashboard", "Admin Dashboard");
    }
}
