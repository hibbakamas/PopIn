package net.javaguids.popin.models;

public class Admin extends User {

    public Admin() {
        setRole(Role.ADMIN);
    }

    public Admin(int id, String username, String passwordHash) {
        super(id, username, passwordHash, Role.ADMIN);
    }

    public Admin(String username, String passwordHash) {
        super(username, passwordHash, Role.ADMIN);
    }
}
