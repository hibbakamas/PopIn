package net.javaguids.popin.models;

import java.time.LocalDateTime;

public class Event {

    private int id;
    private String title;
    private String description;
    private LocalDateTime dateTime;
    private String venue;
    private int capacity;
    private int organizerId;

    public Event() {}

    public Event(int id, String title, String description, LocalDateTime dateTime,
                 String venue, int capacity, int organizerId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.venue = venue;
        this.capacity = capacity;
        this.organizerId = organizerId;
    }

    public Event(String title, String description, LocalDateTime dateTime,
                 String venue, int capacity, int organizerId) {
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.venue = venue;
        this.capacity = capacity;
        this.organizerId = organizerId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getOrganizerId() { return organizerId; }
    public void setOrganizerId(int organizerId) { this.organizerId = organizerId; }
}
