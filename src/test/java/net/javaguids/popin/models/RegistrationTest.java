package net.javaguids.popin.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Registration model.
 */
class RegistrationTest {

    @Test
    void registrationStoresIdsAndStatus() {
        Registration reg = new Registration();
        reg.setId(5);
        reg.setEventId(42);
        reg.setUserId(7);
        reg.setStatus("REGISTERED");

        assertEquals(5, reg.getId());
        assertEquals(42, reg.getEventId());
        assertEquals(7, reg.getUserId());
        assertEquals("REGISTERED", reg.getStatus());
    }
}
