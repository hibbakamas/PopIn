package net.javaguids.popin.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple unit tests for the User model:
 *  - setters/getters work
 *  - role can be attached correctly
 */
class UserTest {

    @Test
    void userStoresCoreFields() {
        User user = new User();
        user.setId(10);
        user.setUsername("alice");
        user.setPasswordHash("hash123");

        Role role = new Role();
        role.setId(2);
        role.setName("ATTENDEE");
        user.setRole(role);

        assertEquals(10, user.getId());
        assertEquals("alice", user.getUsername());
        assertEquals("hash123", user.getPasswordHash());
        assertNotNull(user.getRole());
        assertEquals("ATTENDEE", user.getRole().getName());
    }
}
