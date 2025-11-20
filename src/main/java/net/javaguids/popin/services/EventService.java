package net.javaguids.popin.services;

import net.javaguids.popin.database.EventDAO;
import net.javaguids.popin.models.Event;
import net.javaguids.popin.models.PaidEvent;

import java.time.LocalDateTime;
import java.util.List;

public class EventService {

    private final EventDAO eventDAO = new EventDAO();

    // CREATE EVENT WITH VALIDATION
    public boolean createEvent(
            String title,
            String description,
            LocalDateTime dateTime,
            String venue,
            int capacity,
            int organizerId,
            Double price // null = free event
    ) {

        validateTitle(title);
        validateVenue(venue);
        validateCapacity(capacity);
        validateDateTime(dateTime);

        Event event;

        if (price != null && price > 0) {
            event = new PaidEvent(title, description, dateTime, venue, capacity, organizerId, price);
        } else {
            event = new Event(title, description, dateTime, venue, capacity, organizerId);
        }

        return eventDAO.createEvent(event);
    }

    // GET UPCOMING EVENTS
    public List<Event> getUpcomingEvents() {
        return eventDAO.findAllUpcoming();
    }

    // VALIDATION HELPERS
    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
    }

    private void validateVenue(String venue) {
        if (venue == null || venue.isBlank()) {
            throw new IllegalArgumentException("Venue cannot be empty.");
        }
    }

    private void validateCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0.");
        }
    }

    private void validateDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("Event must have a date and time.");
        }
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Event date must be in the future.");
        }
    }
}
