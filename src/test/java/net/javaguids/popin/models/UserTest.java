package net.javaguids.popin.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void createAdminReturnsAdmin() {
        Role role = new Role("ADMIN");
        User user = User.create("alice", "hash123", role);

        assertTrue(user instanceof Admin);
        assertEquals("ADMIN", user.getRole().getName());
        assertEquals("alice", user.getUsername());
        assertEquals("hash123", user.getPasswordHash());
    }

    @Test
    void createOrganizerReturnsOrganizer() {
        Role role = new Role("ORGANIZER");
        User user = User.create("bob", "hash456", role);

        assertTrue(user instanceof Organizer);
        assertEquals("ORGANIZER", user.getRole().getName());
    }

    @Test
    void createAttendeeReturnsAttendee() {
        Role role = new Role("ATTENDEE");
        User user = User.create("carol", "hash789", role);

        assertTrue(user instanceof Attendee);
        assertEquals("ATTENDEE", user.getRole().getName());
    }

    @Test
    void createWithUnknownRoleDefaultsToAttendee() {
        Role role = new Role("SOMETHING_WEIRD");
        User user = User.create("dave", "hashX", role);

        assertTrue(user instanceof Attendee);
        assertEquals("ATTENDEE", user.getRole().getName());
    }
}