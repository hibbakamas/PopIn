package net.javaguids.popin.services;

import net.javaguids.popin.models.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventServiceInterface {
    boolean createEvent(String title, String description, LocalDateTime dateTime,
                        String venue, int capacity, int organizerId, Double price);

    boolean updateEvent(Event event, Double price);

    boolean deleteEvent(int id);

    List<Event> getUpcomingEvents();

    List<Event> getAllEvents();

    List<Event> getEventsByOrganizer(int organizerId);
}