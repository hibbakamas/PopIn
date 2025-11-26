package net.javaguids.popin.services;

import net.javaguids.popin.database.EventDAO;
import net.javaguids.popin.models.Event;
import net.javaguids.popin.models.PaidEvent;

import java.time.LocalDateTime;
import java.util.List;

public class EventService {

    private final EventDAO eventDAO = new EventDAO();

    public boolean createEvent(String title,
                               String description,
                               LocalDateTime dateTime,
                               String venue,
                               int capacity,
                               int organizerId,
                               Double price) {

        Event event;
        if (price != null) {
            event = new PaidEvent(title, description, dateTime, venue, capacity, organizerId, price);
        } else {
            event = new Event(title, description, dateTime, venue, capacity, organizerId);
        }
        return eventDAO.createEvent(event);
    }

    public boolean updateEvent(Event event, Double price) {
        return eventDAO.updateEvent(event, price);
    }

    public boolean deleteEvent(int id) {
        return eventDAO.deleteEvent(id);
    }

    public List<Event> getUpcomingEvents() {
        return eventDAO.findAllUpcoming();
    }

    public List<Event> getAllEvents() {
        return eventDAO.findAll();
    }

    public List<Event> getEventsByOrganizer(int organizerId) {
        return eventDAO.findByOrganizerId(organizerId);
    }
}