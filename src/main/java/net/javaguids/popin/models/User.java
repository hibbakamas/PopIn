package net.javaguids.popin.models;

public abstract class User {
    private int id;
    private String username;
    private String passwordHash;
    private Role role;

    public User() {}

    public User(int id, String username, String passwordHash, Role role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public User(String username, String passwordHash, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // ---------- STATIC FACTORY HELPERS ----------
    private static String safeRoleName(Role role) {
        if (role == null || role.getName() == null) return "";
        return role.getName().toUpperCase();
    }

    /**
     * Factory for NEW users (no id yet) based on role.
     */
    public static User create(String username, String passwordHash, Role role) {
        String r = safeRoleName(role);
        if ("ADMIN".equals(r)) {
            return new Admin(username, passwordHash);
        } else if ("ORGANIZER".equals(r)) {
            return new Organizer(username, passwordHash);
        } else if ("ATTENDEE".equals(r)) {
            return new Attendee(username, passwordHash);
        }
        return new Attendee(username, passwordHash);
    }

    /**
     * Factory for EXISTING users loaded from DB (with id).
     */
    public static User create(int id, String username, String passwordHash, Role role) {
        String r = safeRoleName(role);
        if ("ADMIN".equals(r)) {
            return new Admin(id, username, passwordHash);
        } else if ("ORGANIZER".equals(r)) {
            return new Organizer(id, username, passwordHash);
        } else if ("ATTENDEE".equals(r)) {
            return new Attendee(id, username, passwordHash);
        }
        return new Attendee(id, username, passwordHash);
    }

    // ---------- GETTERS / SETTERS ----------

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }

    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Role getRole() { return role; }

    public void setRole(Role role) { this.role = role; }

    // ---------- ABSTRACT BEHAVIOUR ----------

    /**
     * Each user type can describe its dashboard in a user-friendly way.
     * This demonstrates abstraction and polymorphism.
     */
    public abstract String getDashboardLabel();
}