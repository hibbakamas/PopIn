package net.javaguids.popin.services;

import net.javaguids.popin.database.EventDAO;
import net.javaguids.popin.models.Event;
import net.javaguids.popin.models.PaidEvent;

import java.time.LocalDateTime;
import java.util.List;

public class EventService implements EventServiceInterface {

    private final EventDAO eventDAO = new EventDAO();

    @Override
    public boolean createEvent(String title,
                               String description,
                               LocalDateTime dateTime,
                               String venue,
                               int capacity,
                               int organizerId,
                               Double price) {

        Event event = (price != null)
                ? new PaidEvent(title, description, dateTime, venue, capacity, organizerId, price)
                : new Event(title, description, dateTime, venue, capacity, organizerId);

        return eventDAO.createEvent(event);
    }

    @Override
    public boolean updateEvent(Event event, Double price) {
        return eventDAO.updateEvent(event, price);
    }

    @Override
    public boolean deleteEvent(int id) {
        return eventDAO.deleteEvent(id);
    }

    @Override
    public List<Event> getUpcomingEvents() {
        return eventDAO.findAllUpcoming();
    }

    @Override
    public List<Event> getAllEvents() {
        return eventDAO.findAll();
    }

    @Override
    public List<Event> getEventsByOrganizer(int organizerId) {
        return eventDAO.findByOrganizerId(organizerId);
    }
}