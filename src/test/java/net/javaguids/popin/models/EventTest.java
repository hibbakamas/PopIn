package net.javaguids.popin.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    void constructorAndGettersWork() {
        LocalDateTime dt = LocalDateTime.of(2025, 1, 2, 18, 30);

        Event e = new Event(
                42,
                "My Event",
                "Description",
                dt,
                "London",
                100,
                7
        );

        assertEquals(42, e.getId());
        assertEquals("My Event", e.getTitle());
        assertEquals("Description", e.getDescription());
        assertEquals(dt, e.getDateTime());
        assertEquals("London", e.getVenue());
        assertEquals(100, e.getCapacity());
        assertEquals(7, e.getOrganizerId());
    }

    @Test
    void settersUpdateValues() {
        Event e = new Event();
        LocalDateTime dt = LocalDateTime.of(2025, 5, 6, 12, 0);

        e.setId(1);
        e.setTitle("Title");
        e.setDescription("Desc");
        e.setDateTime(dt);
        e.setVenue("Venue");
        e.setCapacity(50);
        e.setOrganizerId(3);

        assertEquals(1, e.getId());
        assertEquals("Title", e.getTitle());
        assertEquals("Desc", e.getDescription());
        assertEquals(dt, e.getDateTime());
        assertEquals("Venue", e.getVenue());
        assertEquals(50, e.getCapacity());
        assertEquals(3, e.getOrganizerId());
    }

    @Test
    void toStringIsNotNullOrEmpty() {
        Event e = new Event();
        e.setTitle("Some Event");

        String s = e.toString();
        assertNotNull(s);
        assertFalse(s.isBlank());
    }
}
