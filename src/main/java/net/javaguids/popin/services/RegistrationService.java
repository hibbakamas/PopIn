package net.javaguids.popin.services;

import net.javaguids.popin.database.EventDAO;
import net.javaguids.popin.database.RegistrationDAO;
import net.javaguids.popin.exceptions.EventFullException;
import net.javaguids.popin.models.Event;

public class RegistrationService implements RegistrationServiceInterface {

    private final RegistrationDAO registrationDAO = new RegistrationDAO();
    private final EventDAO eventDAO = new EventDAO();

    @Override
    public boolean registerUser(int eventId, int userId) {
        if (registrationDAO.isUserRegistered(eventId, userId)) {
            throw new IllegalStateException("You are already registered for this event.");
        }

        Event event = eventDAO.findAllUpcoming().stream()
                .filter(e -> e.getId() == eventId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Event not found."));

        int currentCount = registrationDAO.countRegistered(eventId);
        if (currentCount >= event.getCapacity()) {
            throw new EventFullException("This event is full.");
        }

        boolean reactivated = registrationDAO.updateStatus(eventId, userId, "REGISTERED");
        if (reactivated) {
            return true;
        }

        boolean success = registrationDAO.registerUser(eventId, userId);
        if (!success) {
            throw new RuntimeException("Could not register for event.");
        }
        return true;
    }

    @Override
    public boolean cancelRegistration(int eventId, int userId) {
        if (!registrationDAO.isUserRegistered(eventId, userId)) {
            throw new IllegalStateException("You are not registered for this event.");
        }
        return registrationDAO.updateStatus(eventId, userId, "CANCELLED");
    }

    @Override
    public boolean checkInUser(int eventId, int userId) {
        if (!registrationDAO.isUserRegistered(eventId, userId)) {
            throw new IllegalStateException("User is not registered for this event.");
        }
        return registrationDAO.updateStatus(eventId, userId, "CHECKED_IN");
    }

    @Override
    public boolean isEventFull(int eventId) {
        Event event = eventDAO.findAllUpcoming().stream()
                .filter(e -> e.getId() == eventId)
                .findFirst()
                .orElse(null);

        if (event == null) return true;
        return registrationDAO.countRegistered(eventId) >= event.getCapacity();
    }

    @Override
    public boolean isUserRegistered(int eventId, int userId) {
        return registrationDAO.isUserRegistered(eventId, userId);
    }
}