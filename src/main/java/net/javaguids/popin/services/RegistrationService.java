package net.javaguids.popin.services;

import net.javaguids.popin.database.EventDAO;
import net.javaguids.popin.database.RegistrationDAO;
import net.javaguids.popin.models.Event;

public class RegistrationService {

    private final RegistrationDAO registrationDAO = new RegistrationDAO();
    private final EventDAO eventDAO = new EventDAO();

    // REGISTER USER FOR EVENT
    public boolean registerUser(int eventId, int userId) {

        // Check if already registered
        if (registrationDAO.isUserRegistered(eventId, userId)) {
            throw new IllegalStateException("You are already registered for this event.");
        }

        // Check event capacity
        Event event = eventDAO.findAllUpcoming()
                .stream()
                .filter(e -> e.getId() == eventId)
                .findFirst()
                .orElse(null);

        if (event == null) {
            throw new IllegalArgumentException("Event not found.");
        }

        int currentCount = registrationDAO.countRegistered(eventId);

        if (currentCount >= event.getCapacity()) {
            throw new IllegalStateException("This event is full.");
        }

        // Register
        boolean success = registrationDAO.registerUser(eventId, userId);

        if (!success) {
            throw new RuntimeException("Could not register for event.");
        }

        return true;
    }


    // CANCEL REGISTRATION
    public boolean cancelRegistration(int eventId, int userId) {

        if (!registrationDAO.isUserRegistered(eventId, userId)) {
            throw new IllegalStateException("You are not registered for this event.");
        }

        return registrationDAO.updateStatus(eventId, userId, "CANCELLED");
    }

    // CHECK-IN USER (organizer/admin)
    public boolean checkInUser(int eventId, int userId) {

        if (!registrationDAO.isUserRegistered(eventId, userId)) {
            throw new IllegalStateException("User is not registered for this event.");
        }

        return registrationDAO.updateStatus(eventId, userId, "CHECKED_IN");
    }

    // HELPER: SEE IF FULL
    public boolean isEventFull(int eventId) {
        Event event = eventDAO.findAllUpcoming()
                .stream()
                .filter(e -> e.getId() == eventId)
                .findFirst()
                .orElse(null);

        if (event == null) return true;

        return registrationDAO.countRegistered(eventId) >= event.getCapacity();
    }
}
