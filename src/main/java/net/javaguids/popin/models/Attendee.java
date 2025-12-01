package net.javaguids.popin.models;

public class Attendee extends User {

    public Attendee() {
        setRole(new Role("ATTENDEE"));
    }

    public Attendee(int id, String username, String passwordHash) {
        super(id, username, passwordHash, new Role("ATTENDEE"));
    }

    public Attendee(String username, String passwordHash) {
        super(username, passwordHash, new Role("ATTENDEE"));
    }

    @Override
    public String getDashboardLabel() {
        return "Attendee Dashboard";
    }
}