package net.javaguids.popin.models;

public class Registration {

    private int id;
    private int eventId;
    private int userId;
    private String status; // "REGISTERED", "CANCELLED", "CHECKED_IN"

    public Registration() {}

    public Registration(int id, int eventId, int userId, String status) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.status = status;
    }

    public Registration(int eventId, int userId, String status) {
        this.eventId = eventId;
        this.userId = userId;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
