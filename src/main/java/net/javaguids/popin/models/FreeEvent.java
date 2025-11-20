package net.javaguids.popin.models;

import java.time.LocalDateTime;

public class FreeEvent extends Event {

    public FreeEvent() {}

    public FreeEvent(String title, String description, LocalDateTime dateTime,
                     String venue, int capacity, int organizerId) {
        super(title, description, dateTime, venue, capacity, organizerId);
    }
}
