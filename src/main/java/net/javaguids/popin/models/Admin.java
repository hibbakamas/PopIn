package net.javaguids.popin.models;

public class Admin extends User {

    public Admin() {
        setRole(new Role("ADMIN"));
    }

    public Admin(int id, String username, String passwordHash) {
        super(id, username, passwordHash, new Role("ADMIN"));
    }

    public Admin(String username, String passwordHash) {
        super(username, passwordHash, new Role("ADMIN"));
    }

    @Override
    public String getDashboardLabel() {
        return "Admin Dashboard";
    }
}