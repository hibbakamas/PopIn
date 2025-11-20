package net.javaguids.popin.models;

public class Regular extends User {

    public Regular() {
        setRole(Role.ATTENDEE);
    }

    public Regular(int id, String username, String passwordHash) {
        super(id, username, passwordHash, Role.ATTENDEE);
    }

    public Regular(String username, String passwordHash) {
        super(username, passwordHash, Role.ATTENDEE);
    }
}
