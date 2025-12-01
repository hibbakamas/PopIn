package net.javaguids.popin.models;

public class Organizer extends User {

    public Organizer() {
        setRole(new Role("ORGANIZER"));
    }

    public Organizer(int id, String username, String passwordHash) {
        super(id, username, passwordHash, new Role("ORGANIZER"));
    }

    public Organizer(String username, String passwordHash) {
        super(username, passwordHash, new Role("ORGANIZER"));
    }

    @Override
    public String getDashboardLabel() {
        return "Organizer Dashboard";
    }
}